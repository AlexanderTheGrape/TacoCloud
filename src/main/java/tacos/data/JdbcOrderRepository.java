package tacos.data;

import lombok.AllArgsConstructor;
import org.springframework.asm.Type;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tacos.model.Ingredient;
import tacos.model.Taco;
import tacos.model.TacoOrder;

import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Repository
public class JdbcOrderRepository implements OrderRepository {

    private JdbcOperations jdbcOperations;

    @Override
    @Transactional // https://en.wikipedia.org/wiki/Database_transaction
    public TacoOrder save(TacoOrder order) {
        PreparedStatementCreatorFactory pscf = getPreparedStatementCreatorFactory();

        order.setPlacedAt(new Date());
        PreparedStatementCreator preparedStatementCreator = pscf.newPreparedStatementCreator(
                Arrays.asList(
                    order.getDeliveryName(), order.getDeliveryStreet(), order.getDeliveryCity(),
                    order.getDeliveryState(), order.getDeliveryZip(), order.getCcNumber(),
                    order.getCcExpiration(), order.getCcCVV(), order.getPlacedAt()
                ));

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(preparedStatementCreator, keyHolder);
        long orderId = keyHolder.getKey().longValue();
        order.setId(orderId);

        List<Taco> tacos = order.getTacos();
        int i = 0;
        for (Taco taco : tacos) {
            saveTaco(orderId, i++, taco);
        }

        return order;
    }

    private static PreparedStatementCreatorFactory getPreparedStatementCreatorFactory() {
        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory("insert into Taco_Order "
                + "(delivery_name, delivery_street, delivery_city, delivery_state, delivery_zip, cc_number, cc_expiration, cc_cvv, placed_at) "
                + "values (?,?,?,?,?,?,?,?,?)",
                Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR,Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP
        );
        // Sets the db generated primary key in the object, so that it is a complete,
        // accurate object when returned to method caller
        pscf.setReturnGeneratedKeys(true);
        return pscf;
    }

    private long saveTaco(Long orderId, int orderKey, Taco taco) {
        taco.setCreatedAt(new Date());
        PreparedStatementCreatorFactory preparedStatementCreatorFactory = new PreparedStatementCreatorFactory(
                "insert into Taco "
                + "(name, created_at, taco_order, taco_order_key) "
                + "values (?, ?, ?, ?)",
                Types.VARCHAR, Types.TIMESTAMP, Type.LONG, Type.LONG // TODO try later to replace Type.x with a java.sql.Types alternative
        );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(true);

        PreparedStatementCreator preparedStatementCreator = preparedStatementCreatorFactory.newPreparedStatementCreator(
                Arrays.asList(taco.getName(), taco.getCreatedAt(), orderId, orderKey)
        );

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(preparedStatementCreator, keyHolder);
        long tacoId = keyHolder.getKey().longValue();
        taco.setId(tacoId);

        saveIngredientRefs(tacoId, taco.getIngredients());

        return tacoId;
    }

    private void saveIngredientRefs(long tacoId, List<Ingredient> ingredients) {
        // TODO find out if id is what is meant to be used for 'ingredient' of Ingredient_Ref
        int key = 0;
        for (Ingredient ingredient : ingredients) {
            jdbcOperations.update(
                    "insert into Ingredient_Ref (ingredient, taco, taco_key) values (?, ?, ?)",
                    ingredient.getId(), tacoId, key++);
        }
    }


}

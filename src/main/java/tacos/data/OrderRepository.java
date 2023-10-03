package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.model.TacoOrder;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<TacoOrder, UUID> {

}

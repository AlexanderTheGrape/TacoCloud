package tacos.data;

import org.springframework.data.repository.CrudRepository;
import tacos.model.Ingredient;

import java.util.Optional;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {

}

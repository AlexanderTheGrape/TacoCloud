package tacos.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.data.IngredientRepository;
import tacos.model.Ingredient;
import tacos.model.Ingredient.Type;
import tacos.model.Taco;
import tacos.model.TacoOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
This modest annotation (@Slf4j)
has the same effect as if you were to explicitly add the following lines within the class:
private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DesignTacoController.class);
 */
@Slf4j
@Controller
@RequestMapping("/design")
/*
The @SessionAttributes annotation indicates that the TacoOrder object that is put into the model and should be maintained in session.
 */
@SessionAttributes("tacoOrder")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;

    public DesignTacoController(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepo.findAll();
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
        }
    }

    @ModelAttribute(name = "tacoOrder")
    public TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    /*
    "...showDesignForm also populates the given Model with an empty Taco object under a key
    whose name is "design". This will enable the form to have a blank slate on which to
    create a taco masterpiece."
     */
    @GetMapping
    public String showDesignForm() {
        return "design";
    }

    @PostMapping
    public String processTaco(@Valid Taco taco, Errors errors, @ModelAttribute TacoOrder tacoOrder) {

        if (errors.hasErrors()) {
            log.error("Error in taco form submission: " + errors.getAllErrors());
            return "design";
        }

        log.info("Processing taco: {}", taco);
        tacoOrder.addTaco(taco);

        return "redirect:/orders/current";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

    private List<Ingredient> filterByType(Iterable<Ingredient> ingredients, Type type) {
        List<Ingredient> ingredientsList = new ArrayList<>();
        ingredients.forEach(x -> {
            if (x.getType().equals(type)) {
                ingredientsList.add(x);
            }
        });
        return ingredientsList;
    }

}

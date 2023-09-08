package tacos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;

import java.util.List;
import java.util.ArrayList;

@Data
public class TacoOrder {

    @NotBlank(message="Delivery name is required")
    private String deliveryName;

    @NotBlank(message="Street is required")
    private String deliveryStreet;

    @NotBlank(message="State is required")
    private String deliveryState;

    @NotBlank(message="City is required")
    private String deliveryCity;

    @NotBlank(message="Zip code is required")
    private String deliveryZip;

    @CreditCardNumber(message="Not a valid credit card number - correct credit card number required")
    private String ccNumber;

    @Pattern(regexp="^(0$")
    private String ccExpiration;

    @NotBlank(message="is required")
    private String ccCVV;


    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco taco) {
        this.tacos.add(taco);
    }
}

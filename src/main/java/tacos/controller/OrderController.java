package tacos.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tacos.data.OrderRepository;
import tacos.model.TacoOrder;

@Slf4j
@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
@AllArgsConstructor
public class OrderController {

    private OrderRepository orderRepo;

    @GetMapping("/current")
    public String orderForm() {
        log.info("debug - /current mapping has been called. Returning orderForm page.");
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid @ModelAttribute TacoOrder order, Errors errors, SessionStatus sessionStatus,
                               RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            log.error("Error(s) in form validation: {}", errors.getAllErrors());
            return "orderForm";
        }

        orderRepo.save(order);
        sessionStatus.setComplete();

        redirectAttributes.addFlashAttribute("tacoOrderId", order.getId().toString());
        return "redirect:/";
    }
}

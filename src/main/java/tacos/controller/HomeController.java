package tacos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // The Controller
public class HomeController {
    @GetMapping("/") // Handles requests for the root path /
    public String home() {
/*
The home() method is as simple as controller methods come. It’s annotated with
@GetMapping to indicate that if an HTTP GET request is received for the root path /,
then this method should handle that request. It does so by doing nothing more than
returning a String value of home.
This value is interpreted as the logical name of a view. How that view is implemented
depends on a few factors, but because Thymeleaf is in your classpath, you can
define that template with Thymeleaf.
 */

        // Service logs the client info and may store it for statistics
        // TODO create service and migrate functionality there

        return "home"; // Returns the (optional) view name, which in this case will in turn generate an html response
    }
}

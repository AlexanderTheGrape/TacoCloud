package tacos.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Controller // The Controller
public class HomeController {
    /*
    This / value is interpreted as the logical name of a view. How that view is implemented
    depends on a few factors, but because Thymeleaf is in your classpath, you can
    define that template with Thymeleaf.
     */
    @GetMapping("/") // Handles requests for the root path /
    public String home(HttpServletRequest request) {

        // Service logs the client info and may store it for statistics
        // TODO create service and migrate functionality there
        log.info("Received incoming request to home page.");
        Enumeration<String> enumeration = request.getHeaderNames();
        List<String> headerNamesList = new ArrayList<>();
        while(enumeration.hasMoreElements()) {
            String nextHeader = enumeration.nextElement();
            headerNamesList.add(nextHeader);
        }
        log.info("Request header names: " + headerNamesList.toString());
        log.info("Remote addr: " + request.getRemoteAddr());

        return "home"; // Returns the (optional) view name, which in this case will in turn generate an html response
    }
}

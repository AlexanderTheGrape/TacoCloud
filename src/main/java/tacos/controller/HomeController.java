package tacos.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import tacos.service.RequestAnalyzerService;

@Slf4j
@Controller // The Controller
public class HomeController {

    @Autowired
    RequestAnalyzerService requestAnalyzerService;

    /*
    This / value is interpreted as the logical name of a view. How that view is implemented
    depends on a few factors, but because Thymeleaf is in your classpath, you can
    define that template with Thymeleaf.
     */
    @GetMapping("/") // Handles requests for the root path /
    public String home(HttpServletRequest request) {
        log.info("Received incoming request to home page.");

        requestAnalyzerService.analyzeRequest(request);

        return "home"; // Returns the (optional) view name, which in this case will in turn generate an html response
    }
}

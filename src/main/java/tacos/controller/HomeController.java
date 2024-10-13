package tacos.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import tacos.model.ClientRequestsData;
import tacos.service.RequestAnalyzerService;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Controller
public class HomeController {

    @Autowired
    RequestAnalyzerService requestAnalyzerService;

    /**
    This / value is interpreted as the logical name of a view. How that view is implemented
    depends on a few factors, but because Thymeleaf is in your classpath, you can
    define that template with Thymeleaf.
     */
    @GetMapping("/") // Handles requests for the root path /
    public String home(HttpServletRequest request, Model model) {
        log.info("Received incoming request to home page from {}", request.getRemoteAddr());
        requestAnalyzerService.analyzeRequest(request);

        // adds tacoOrderId = null to the model, if model.asMap().get(x) returns null
        model.addAttribute("tacoOrderId", model.asMap().get("tacoOrderId"));

        return "home"; // Returns the (optional) view name, which in this case will in turn generate an html response
    }

    /**
     * This method will also be invoked when a request is handled and
     * will construct a list of Ingredient objects to be put into the model.
     * Model is an object that ferries data between a
     * controller and whatever view is charged with rendering that data. Ultimately, data
     * that’s placed in Model attributes is copied into the servlet request attributes, where the
     * view can find them and use them to render a page in the user’s browser.
     * @param request provided automatically by Spring
     */
    @ModelAttribute(name = "clientRequestInfoString")
    public String addClientInfoStringToModel(HttpServletRequest request) {
        return requestAnalyzerService.listLastIPAddressAccesses();
    }

    @ModelAttribute(name = "clientRequestsData")
    public ClientRequestsData addClientInfoToModel(HttpServletRequest request) {
        return requestAnalyzerService.getFilteredClientRequestsData();
    }

    @ModelAttribute(name = "clientRequestsStrings")
    public ArrayList<String> addClientInfoStringsToModel(HttpServletRequest request) {
        return requestAnalyzerService.getFilteredClientRequestsDataAsStrings();
    }
}

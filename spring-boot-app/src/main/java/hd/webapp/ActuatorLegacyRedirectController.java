package hd.webapp;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
public class ActuatorLegacyRedirectController {

    @GetMapping("/actuator/httptrace")
    public void handleLegacyHttpTrace(HttpServletResponse response) throws IOException {
        // Option A: Redirect the caller to the new Spring Boot 3 endpoint
        response.sendRedirect("/actuator/httpexchanges");
        
        // Option B: If you prefer to just return a clear message instead of redirecting, 
        // comment out the line above and uncomment the lines below:
        /*
        response.setStatus(HttpServletResponse.SC_GONE); // 410 Gone
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"'/actuator/httptrace' is deprecated. Please use '/actuator/httpexchanges' instead.\"}");
        */
    }
}

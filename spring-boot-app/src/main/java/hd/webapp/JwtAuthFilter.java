package hd.webapp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final  CustomUserDetailsService customUserDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            // 1. Use YOUR service to validate and extract username
            if (jwtService.verifyToken(jwt)) {
                String username = jwtService.extractSubject(jwt);
                if (username != null) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    // 2. If username is found and no authentication exists yet
                    if(SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Create Authentication object
                        // Note: We pass an empty list of authorities here. 
                        // If your JwtService extracts roles, add them here.
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            userDetails.getAuthorities() 
                        ); 
                        // Set details if needed (e.g., IP address)
                        authToken.setDetails(new org.springframework.security.web.authentication.WebAuthenticationDetailsSource().buildDetails(request));
                        // 3. Set the context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Log error but do not stop the chain. 
            // Spring Security will handle the "Anonymous" rejection later if the endpoint is protected.
            logger.error("Error processing JWT: {}");
        }

        filterChain.doFilter(request, response);
    }
}
package hd.webapp;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=================================================");
        System.out.println("===> " + req.getMethod() + " " + req.getRequestURI());
        System.out.println("     Auth type:    " + (a == null ? "null" : a.getClass().getSimpleName()));
        System.out.println("     Auth name:    " + (a != null ? a.getName() : "null"));
        System.out.println("     Authorities:  " + (a != null ? a.getAuthorities() : "null"));
        System.out.println("     Authz header: " + req.getHeader("Authorization"));
        System.out.println("     CSRF header:  " + req.getHeader("X-XSRF-TOKEN"));
        System.out.println("     Origin:       " + req.getHeader("Origin"));
        System.out.println("=================================================");
        chain.doFilter(req, res);
    }
}
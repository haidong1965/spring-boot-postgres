package hd.webapp;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("dev")
public class AuthDebugFilter extends OncePerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(AuthDebugFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        
        log.info("=================================================");
        log.info("===> {} {}",req.getMethod(), req.getRequestURI());
        log.info("     Auth type:   {}", (a == null ? "null" : a.getClass().getSimpleName()));
        log.info("     Auth name:   {}", (a != null ? a.getName() : "null"));
        log.info("     Authorities: {}", (a != null ? a.getAuthorities() : "null"));
        log.info("     Authz header:{}", req.getHeader("Authorization"));
        log.info("     CSRF header: {}", req.getHeader("X-XSRF-TOKEN"));
        log.info("     Origin:      {}", req.getHeader("Origin"));
        log.info("=================================================");
        chain.doFilter(req, res);
    }
}
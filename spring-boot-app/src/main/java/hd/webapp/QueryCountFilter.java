package hd.webapp;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class QueryCountFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(QueryCountFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        QueryCounter.reset();

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("{} {} -> {} SQL statements",
                    request.getMethod(),
                    request.getRequestURI(),
                    QueryCounter.getCount());

            QueryCounter.clear();
        }
    }

	
}

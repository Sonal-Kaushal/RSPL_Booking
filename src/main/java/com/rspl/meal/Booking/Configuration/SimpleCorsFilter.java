package com.rspl.meal.Booking.Configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    // Allowed origins for CORS
    private final Set<String> allowedOrigins = new HashSet<>();

    public SimpleCorsFilter(){
        // Add specific origins as needed
        allowedOrigins.add("http://localhost:4200");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String originHeader = request.getHeader("Origin");

        // If the Origin header is present and is an allowed origin, set the CORS headers
        if(originHeader != null && (allowedOrigins.contains(originHeader) || allowedOrigins.contains("*"))) {
            response.setHeader("Access-Control-Allow-Origin", originHeader); // Reflect the Origin header
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization, Access-Control-Allow-Credentials");
            response.setHeader("Access-Control-Expose-Headers", "Authorization");
        }

        // If the request method is OPTIONS, respond with OK status
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization logic, if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic, if needed
    }
}

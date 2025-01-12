//package webhook.processor.controller.impl;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class RequestFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        log.info("Request from host: {}", request.getRemoteHost());
//        log.info("Request from port: {}", request.getRemotePort());
//        log.info("Request from user: {}", request.getRemoteUser());
//        log.info("Request cookies: {}", (Object[]) request.getCookies());
//        log.info("Request path: {}", request.getPathInfo());
//        log.info("Request headers: {}", request.getHeaderNames());
//        filterChain.doFilter(request, response);
//    }
//}

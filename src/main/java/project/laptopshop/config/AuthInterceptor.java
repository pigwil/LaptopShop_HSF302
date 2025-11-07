package project.laptopshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import project.laptopshop.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String requestURI = request.getRequestURI();

        log.info("AuthInterceptor: Request URI = {}", requestURI);

        if (requestURI.startsWith("/admin")) {
            log.info("AuthInterceptor: Accessing admin path. User in session: {}", user != null ? user.getUsername() : "null");
            if (user == null || user.getRole() != User.Role.ADMIN) {
                log.warn("AuthInterceptor: Access denied for admin path. User role: {}", user != null ? user.getRole() : "null");
                response.sendRedirect("/login");
                return false;
            }
            log.info("AuthInterceptor: Access granted for admin path. User role: {}", user.getRole());
        } else if (requestURI.startsWith("/cart") || requestURI.startsWith("/order")) {
            log.info("AuthInterceptor: Accessing protected user path. User in session: {}", user != null ? user.getUsername() : "null");
            if (user == null) {
                log.warn("AuthInterceptor: Access denied for protected user path. User is null.");
                response.sendRedirect("/login");
                return false;
            }
            log.info("AuthInterceptor: Access granted for protected user path. User role: {}", user.getRole());
        }

        return true;
    }
}

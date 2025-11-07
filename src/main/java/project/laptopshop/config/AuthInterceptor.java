package project.laptopshop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import project.laptopshop.entity.User;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/admin")) {
            if (user == null || user.getRole() != User.Role.ADMIN) {
                response.sendRedirect("/login");
                return false;
            }
        } else if (requestURI.startsWith("/cart") || requestURI.startsWith("/order")) {
            if (user == null) {
                response.sendRedirect("/login");
                return false;
            }
        }

        return true;
    }
}

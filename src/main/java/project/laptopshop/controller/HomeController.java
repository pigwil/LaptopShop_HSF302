package project.laptopshop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.laptopshop.entity.Laptop;
import project.laptopshop.service.LaptopService;

import java.util.List;

@Controller
public class HomeController {

    private final LaptopService laptopService;

    public HomeController(LaptopService laptopService) {
        this.laptopService = laptopService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model, HttpSession session) {
        List<Laptop> products = laptopService.getAllLaptops();
        model.addAttribute("products", products);

        // Lấy thông tin người dùng và số lượng giỏ hàng từ session (nếu có)
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        Object cartCount = session.getAttribute("cartCount");
        if (cartCount != null) {
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("cartCount", 0);
        }

        return "home";
    }
}

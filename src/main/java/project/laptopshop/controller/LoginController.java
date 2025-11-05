package project.laptopshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Username hoặc mật khẩu không đúng!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }

        return "login";
    }

    // Trang home sau khi đăng nhập
    @GetMapping({"/", "/home"})
    public String showHomePage() {
        return "home";
    }

    // Trang báo lỗi khi không có quyền
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
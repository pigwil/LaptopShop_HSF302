package project.laptopshop.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.laptopshop.dto.RegisterDTO;
import project.laptopshop.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Đăng ký user mới
            userService.register(registerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}
package project.laptopshop.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.laptopshop.dto.ChangePasswordDTO;
import project.laptopshop.entity.User;
import project.laptopshop.service.UserService;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model, HttpSession session) {
        // Bắt buộc đăng nhập mới được vào trang này
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "account/change-password"; // Giả sử bạn có file change-password.html trong templates/account
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordDTO changePasswordDTO,
                                 BindingResult result,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "account/change-password";
        }

        try {
            userService.changePassword(user.getId(), changePasswordDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/account/change-password";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/account/change-password";
        }
    }
}

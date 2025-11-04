package project.laptopshop.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private UserService userService;

    // Hiển thị trang đổi mật khẩu
    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "change-password";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordDTO changePasswordDTO,
                                 BindingResult result,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "change-password";
        }

        try {
            // Lấy thông tin user đang đăng nhập
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            // Đổi mật khẩu
            userService.changePassword(user.getId(), changePasswordDTO);

            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/account/change-password";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/account/change-password";
        }
    }
}

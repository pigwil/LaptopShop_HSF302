package project.laptopshop.controller;

import project.laptopshop.dto.CheckoutDTO;
import project.laptopshop.entity.User;
import project.laptopshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import project.laptopshop.service.OrderService;

@Controller
public class CheckoutController {
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/check-out")
    public String viewCheckout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (cartService.getCartFromOrders(user.getId()).isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("checkoutDTO", new CheckoutDTO());
        model.addAttribute("totalPrice", cartService.getTotalPrice(user.getId()));
        return "check-out";
    }

    @PostMapping("/check-out")
    public String processCheckout(@Valid @ModelAttribute CheckoutDTO checkoutDTO, BindingResult result,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        if (result.hasErrors()) return "check-out";

        try {
            orderService.createOrder(user, checkoutDTO);
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/check-out";
        }
    }
}
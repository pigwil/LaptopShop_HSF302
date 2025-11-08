package project.laptopshop.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@PreAuthorize("isAuthenticated()")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItemDTO> cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cartService.getTotalPrice(session));
        return "cart";
    }

    @PostMapping("cart/add")
    public String addToCart(@RequestParam String laptopCode, @RequestParam(defaultValue = "1") Integer quantity, HttpSession session) {
        cartService.addToCart(laptopCode, quantity, session);
        return "redirect:/cart";
    }

    @PostMapping("cart/update")
    public String updateCart(@RequestParam String laptopCode, @RequestParam Integer quantity, HttpSession session) {
        cartService.updateCart(laptopCode, quantity, session);
        return "redirect:/cart";
    }

    @PostMapping("cart/remove")
    public String removeFromCart(@RequestParam String laptopCode, HttpSession session) {
        cartService.removeFromCart(laptopCode, session);
        return "redirect:/cart";
    }
}
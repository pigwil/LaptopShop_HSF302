package project.laptopshop.controller;

import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.entity.User;
import project.laptopshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<CartItemDTO> carts = cartService.getCartFromOrders(user.getId());
        for( CartItemDTO cart : carts){
            System.out.println("Current Cart: " + cart);
        }
        model.addAttribute("cart", carts);
        model.addAttribute("totalPrice", cartService.getTotalPrice(user.getId()));
        return "cart";
    }

    @PostMapping("cart/add")
    public String addToCart(@RequestParam String laptopCode, @RequestParam(defaultValue = "1") Integer quantity, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        cartService.addToCart(laptopCode, quantity, user.getId());
        return "redirect:/cart";
    }

    @PostMapping("cart/update")
    public String updateCart(@RequestParam String laptopCode, @RequestParam Integer quantity, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        cartService.updateCart(laptopCode, quantity, user.getId());
        return "redirect:/cart";
    }

    @PostMapping("cart/remove")
    public String removeFromCart(@RequestParam String laptopCode, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        cartService.removeFromCart(laptopCode, user.getId());
        return "redirect:/cart";
    }

}
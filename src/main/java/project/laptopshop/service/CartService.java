package project.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import project.laptopshop.dto.CartItemDTO;

import java.util.List;

public interface CartService {
    List<CartItemDTO> getCart(HttpSession session);
    void addToCart(String laptopCode, Integer quantity, HttpSession session);
    void updateCart(String laptopCode, Integer quantity, HttpSession session);
    void removeFromCart(String laptopCode, HttpSession session);
    Double getTotalPrice(HttpSession session);
    void clearCart(HttpSession session);
}

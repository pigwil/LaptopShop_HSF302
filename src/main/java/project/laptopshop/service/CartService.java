package project.laptopshop.service;

import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.entity.User;

import java.util.List;

public interface CartService {
    List<CartItemDTO> getCartFromOrders(Long userId);
    void addToCart(String laptopCode, Integer quantity, Long userId);
    void updateCart(String laptopCode, Integer quantity, Long userId);
    void removeFromCart(String laptopCode, Long userId);
    Double getTotalPrice(Long userId);
    void clearCart(Long userId);
}

package project.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.entity.Laptop;
import project.laptopshop.repository.LaptopRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private LaptopRepository laptopRepository;
    private static final String CART_SESSION_KEY = "cart";

    @Override
    @SuppressWarnings("unchecked")
    public List<CartItemDTO> getCart(HttpSession session) {
        List<CartItemDTO> cart = (List<CartItemDTO>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public void addToCart(String laptopCode, Integer quantity, HttpSession session) {
        Laptop laptop = laptopRepository.findByLaptopCode(laptopCode);
        if (laptop == null) {
            throw new RuntimeException("Laptop not found");
        }
        List<CartItemDTO> cart = getCart(session);
        for (CartItemDTO item : cart) {
            if (item.getLaptopCode().equals(laptopCode)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cart.add(new CartItemDTO(laptopCode, laptop.getLaptopName(), laptop.getPrice(), quantity));
    }

    @Override
    public void updateCart(String laptopCode, Integer quantity, HttpSession session) {
        List<CartItemDTO> cart = getCart(session);
        cart.removeIf(item -> item.getLaptopCode().equals(laptopCode));
        if (quantity > 0) {
            addToCart(laptopCode, quantity, session);
        }
    }

    @Override
    public void removeFromCart(String laptopCode, HttpSession session) {
        List<CartItemDTO> cart = getCart(session);
        cart.removeIf(item -> item.getLaptopCode().equals(laptopCode));
    }

    @Override
    public Double getTotalPrice(HttpSession session) {
        return getCart(session).stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    @Override
    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
}

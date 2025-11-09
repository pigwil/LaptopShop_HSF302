package project.laptopshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.entity.Laptop;
import project.laptopshop.entity.Order;
import project.laptopshop.entity.OrderDetail;
import project.laptopshop.repository.LaptopRepository;
import project.laptopshop.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private LaptopRepository laptopRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<CartItemDTO> getCartFromOrders(Long userId) {
        List<Order> draftOrders = orderRepository.findByCreatedByIdAndOrderStatus(userId, Order.Status.Draft);
        for (Order order : draftOrders) {
            System.out.println("Current Order: " + order.getId());
        }
        List<CartItemDTO> cartItems = new ArrayList<>();

        for (Order order : draftOrders) {
            for (OrderDetail detail : order.getOrderDetails()) {
                cartItems.add(new CartItemDTO(
                        detail.getLaptop().getLaptopCode(),
                        detail.getLaptop().getLaptopName(),
                        detail.getUnitPrice(),
                        detail.getQuantity()
                ));
            }
        }
        return cartItems;
    }

    @Override
    public void addToCart(String laptopCode, Integer quantity, Long userId) {
        Laptop laptop = laptopRepository.findByLaptopCode(laptopCode);
        if (laptop == null) {
            throw new RuntimeException("Laptop not found");
        }

        List<Order> draftOrders = orderRepository.findByCreatedByIdAndOrderStatus(userId, Order.Status.Draft);
        Order draftOrder = draftOrders.isEmpty() ? new Order(userId, Order.Status.Draft) : draftOrders.get(0);

        OrderDetail existingDetail = draftOrder.getOrderDetails().stream()
                .filter(d -> d.getLaptop().getLaptopCode().equals(laptopCode))
                .findFirst()
                .orElse(null);

        if (existingDetail != null) {
            existingDetail.setQuantity(existingDetail.getQuantity() + quantity);
        } else {
            OrderDetail newDetail = new OrderDetail(draftOrder, laptop, laptop.getPrice(), quantity);
            draftOrder.getOrderDetails().add(newDetail);
        }

        orderRepository.save(draftOrder);
    }

    @Override
    public void updateCart(String laptopCode, Integer quantity, Long userId) {
        List<Order> draftOrders = orderRepository.findByCreatedByIdAndOrderStatus(userId, Order.Status.Draft);
        if (draftOrders.isEmpty()) return;

        Order draftOrder = draftOrders.get(0);
        draftOrder.getOrderDetails().removeIf(d -> d.getLaptop().getLaptopCode().equals(laptopCode));

        if (quantity > 0) {
            addToCart(laptopCode, quantity, userId);
        } else {
            orderRepository.save(draftOrder);
        }
    }

    @Override
    public void removeFromCart(String laptopCode, Long userId) {
        List<Order> draftOrders = orderRepository.findByCreatedByIdAndOrderStatus(userId, Order.Status.Draft);
        if (draftOrders.isEmpty()) return;

        Order draftOrder = draftOrders.get(0);
        draftOrder.getOrderDetails().removeIf(d -> d.getLaptop().getLaptopCode().equals(laptopCode));
        orderRepository.save(draftOrder);
    }

    @Override
    public Double getTotalPrice(Long userId) {
        return getCartFromOrders(userId).stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public void clearCart(Long userId) {
        List<Order> draftOrders = orderRepository.findByCreatedByIdAndOrderStatus(userId, Order.Status.Draft);
        draftOrders.forEach(order -> {
            order.getOrderDetails().clear();
            orderRepository.save(order);
        });
    }
}

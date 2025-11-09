package project.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import project.laptopshop.dto.CheckoutDTO;
import project.laptopshop.entity.Order;
import project.laptopshop.entity.User;

import java.util.List;
@Service
public interface OrderService {
    List<Order> getFilteredOrders(String status);
    Order getOrderById(Long id);
    Order updateOrderStatus(Long id, Order.Status newStatus) throws Exception;
    void updatePaymentStatus(Long id, boolean isPaid);
    void createOrder(User user, @Valid CheckoutDTO checkoutDTO);
}

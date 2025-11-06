package project.laptopshop.service;

import org.springframework.stereotype.Service;
import project.laptopshop.entity.Order;

import java.util.List;
@Service
public interface OrderService {
    List<Order> getFilteredOrders(String status);
    Order getOrderById(Long id);
    Order updateOrderStatus(Long id, Order.Status newStatus) throws Exception;
    void updatePaymentStatus(Long id, boolean isPaid);
}

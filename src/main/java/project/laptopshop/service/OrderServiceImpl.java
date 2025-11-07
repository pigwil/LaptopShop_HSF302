package project.laptopshop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.laptopshop.entity.Laptop;
import project.laptopshop.entity.Order;
import project.laptopshop.entity.OrderDetail;
import project.laptopshop.repository.LaptopRepository;
import project.laptopshop.repository.OrderRepository;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final LaptopRepository laptopRepository;

    public OrderServiceImpl(OrderRepository orderRepository, LaptopRepository laptopRepository) {
        this.orderRepository = orderRepository;
        this.laptopRepository = laptopRepository;
    }

    @Override
    public List<Order> getFilteredOrders(String status) {
        if (status == null || status.isEmpty() || status.equals("All")) {
            return orderRepository.findAllByOrderStatusNotOrderByCreatedDateDesc(Order.Status.Draft);
        }
        try {
            Order.Status orderStatus = Order.Status.valueOf(status);
            return orderRepository.findAllByOrderStatusOrderByCreatedDateDesc(orderStatus);
        } catch (IllegalArgumentException e) {
            return orderRepository.findAllByOrderStatusNotOrderByCreatedDateDesc(Order.Status.Draft);
        }
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, Order.Status newStatus) throws Exception {
        Order order = getOrderById(id);

        // Logic khi xác nhận đơn hàng
        if (newStatus == Order.Status.Confirmed && order.getOrderStatus() == Order.Status.Draft) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Laptop laptop = detail.getLaptop();
                if (laptop.getQuantityInStock() < detail.getQuantity()) {
                    throw new Exception("Không đủ hàng cho sản phẩm: " + laptop.getLaptopName() + ". Trong kho chỉ còn " + laptop.getQuantityInStock());
                }
                int newQuantity = laptop.getQuantityInStock() - detail.getQuantity();
                laptop.setQuantityInStock(newQuantity);

                if (newQuantity == 0) {
                    laptop.setLaptopStatus(Laptop.LaptopStatus.Out_Of_Stock);
                }
                laptopRepository.save(laptop);
            }
        }

        // Logic khi hủy đơn hàng đã xác nhận
        if (newStatus == Order.Status.Cancelled && order.getOrderStatus() == Order.Status.Confirmed) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Laptop laptop = detail.getLaptop();
                int newQuantity = laptop.getQuantityInStock() + detail.getQuantity();
                laptop.setQuantityInStock(newQuantity);

                if (newQuantity > 0) {
                    laptop.setLaptopStatus(Laptop.LaptopStatus.Available);
                }
                laptopRepository.save(laptop);
            }
        }

        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    public void updatePaymentStatus(Long id, boolean isPaid) {
        Order order = getOrderById(id);
        order.setPayed(isPaid);
        orderRepository.save(order);
    }
}

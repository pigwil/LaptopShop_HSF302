package project.laptopshop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.laptopshop.dto.CheckoutDTO;
import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.entity.Laptop;
import project.laptopshop.entity.Order;
import project.laptopshop.entity.OrderDetail;
import project.laptopshop.entity.User;
import project.laptopshop.repository.LaptopRepository;
import project.laptopshop.repository.OrderDetailRepository;
import project.laptopshop.repository.OrderRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final LaptopRepository laptopRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

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
        if (order.getOrderStatus() == Order.Status.Cancelled) {
            throw new Exception("Không thể cập nhật trạng thái cho đơn hàng đã bị hủy.");
        }

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
        if (order.getOrderStatus() == Order.Status.Cancelled) {
            throw new RuntimeException("Không thể cập nhật thanh toán cho đơn hàng đã bị hủy.");
        }
        order.setPayed(isPaid);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void createOrder(User user, CheckoutDTO checkoutDTO) {
        List<CartItemDTO> cart = cartService.getCartFromOrders(user.getId());
        if (cart.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }
        Order order = new Order();
        order.setCreatedBy(user);
        order.setCreatedDate(LocalDate.now());
        order.setOrderStatus(Order.Status.Draft);
        order.setPayed(false);
        order.setShippingAddress(checkoutDTO.getShippingAddress());
        order.setIs_archived(0);

        order = orderRepository.save(order);
        for (CartItemDTO item : cart) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);

            Laptop laptop = laptopRepository.findByLaptopCode(item.getLaptopCode());
            if (laptop == null) {
                throw new RuntimeException("Không tìm thấy laptop với mã: " + item.getLaptopCode());
            }

            detail.setLaptop(laptop);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());
            detail.setTotalPrice(item.getPrice() * item.getQuantity());

            orderDetailRepository.save(detail);
        }
        cartService.clearCart(user.getId());
    }
}
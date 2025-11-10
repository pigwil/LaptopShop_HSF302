package project.laptopshop.controller;

import jakarta.transaction.Transactional;
import project.laptopshop.dto.CartItemDTO;
import project.laptopshop.dto.CheckoutDTO;
import project.laptopshop.entity.Laptop;
import project.laptopshop.entity.Order;
import project.laptopshop.entity.OrderDetail;
import project.laptopshop.entity.User;
import project.laptopshop.repository.LaptopRepository;
import project.laptopshop.repository.OrderRepository;
import project.laptopshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import project.laptopshop.service.LaptopService;
import project.laptopshop.service.OrderService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    LaptopRepository laptopRepository;

    @GetMapping("/check-out")
    public String viewCheckout(@RequestParam String laptopCode,
                               HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Laptop laptop = laptopRepository.findByLaptopCode(laptopCode);
        if (laptop == null) {
            throw new RuntimeException("Không tìm thấy laptop");
        }

        // Tạo item mô phỏng cart
        CartItemDTO item = new CartItemDTO();
        item.setLaptopCode(laptop.getLaptopCode());
        item.setLaptopName(laptop.getLaptopName());
        item.setQuantity(1);
        item.setPrice(laptop.getPrice());
        item.setQuantityInStock(laptop.getQuantityInStock()); // ✅ thêm số lượng tồn

        model.addAttribute("cartItems", List.of(item));
        model.addAttribute("totalPrice", item.getPrice());
        model.addAttribute("stock", laptop.getQuantityInStock()); // ✅ truyền riêng nếu cần

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setPaymentMethod("COD");
        model.addAttribute("checkoutDTO", checkoutDTO);

        return "check-out";
    }

    @PostMapping("/check-out")
    @Transactional
    public String submitCheckout(@ModelAttribute CheckoutDTO checkoutDTO,
                                 @RequestParam("laptopCodes") List<String> laptopCodes,
                                 @RequestParam("quantities") List<Integer> quantities,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Order order = new Order();
        order.setCreatedBy(user);
        order.setOrderStatus(Order.Status.Confirmed);
        order.setCreatedDate(LocalDate.from(LocalDateTime.now()));
        order.setShippingAddress(checkoutDTO.getShippingAddress());
        order.setShippingName(user.getFullName());
        order.setShippingPhone(user.getPhone());
        order.setPayed(false);

        List<OrderDetail> details = new ArrayList<>();

        for (int i = 0; i < laptopCodes.size(); i++) {
            Laptop laptop = laptopRepository.findByLaptopCode(laptopCodes.get(i));
            if (laptop == null) throw new RuntimeException("Laptop không tồn tại");

            int quantityOrdered = quantities.get(i);

            if (laptop.getQuantityInStock() < quantityOrdered) {
                throw new RuntimeException("Sản phẩm " + laptop.getLaptopName() + " chỉ còn " + laptop.getQuantityInStock() + " trong kho");
            }
            laptop.setQuantityInStock(laptop.getQuantityInStock() - quantityOrdered);
            laptopRepository.save(laptop);

            // Tạo OrderDetail
            OrderDetail detail = new OrderDetail();
            detail.setLaptop(laptop);
            detail.setQuantity(quantityOrdered);
            detail.setUnitPrice(laptop.getPrice());
            detail.setTotalPrice(laptop.getPrice() * quantityOrdered);
            detail.setOrder(order);

            details.add(detail);
        }

        order.setOrderDetails(details);
        orderRepository.save(order);

        return "redirect:/home";
    }


    @GetMapping("/order-history")
    public String viewOrderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Order> orders = orderRepository.findByCreatedByIdAndOrderStatusIn(
                user.getId(),
                List.of(Order.Status.Confirmed, Order.Status.Delivered)
        );

        model.addAttribute("orders", orders);
        return "order-history";
    }

    @PostMapping("/order-history/cancel")
    @Transactional
    public String cancelOrder(@RequestParam Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Kiểm tra user có quyền hủy đơn không
        if (!order.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa đơn này");
        }

        // Chỉ cho phép hủy nếu trạng thái chưa giao
        if (order.getOrderStatus() == Order.Status.Confirmed) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Laptop laptop = detail.getLaptop();

                // Tăng lại số lượng
                laptop.setQuantityInStock(
                        laptop.getQuantityInStock() + detail.getQuantity()
                );

                // Nếu số lượng > 0 thì set lại status
                if (laptop.getQuantityInStock() > 0 &&
                        laptop.getLaptopStatus() == Laptop.LaptopStatus.Out_Of_Stock) {

                    laptop.setLaptopStatus(Laptop.LaptopStatus.Available);
                }

                laptopRepository.save(laptop);
            }

            // Cập nhật trạng thái đơn
            order.setOrderStatus(Order.Status.Cancelled);
            orderRepository.save(order);
        }

        return "redirect:/order-history";
    }


}
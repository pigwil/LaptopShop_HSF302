package project.laptopshop.controller;

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
        CartItemDTO item = new CartItemDTO();
        item.setLaptopCode(laptop.getLaptopCode());
        item.setLaptopName(laptop.getLaptopName());
        item.setQuantity(1);
        item.setPrice(laptop.getPrice());
        model.addAttribute("cartItems", List.of(item));
        model.addAttribute("totalPrice", item.getPrice());

        CheckoutDTO checkoutDTO = new CheckoutDTO();
        checkoutDTO.setPaymentMethod("COD");
        model.addAttribute("checkoutDTO", checkoutDTO);

        return "check-out";
    }

    @PostMapping("/check-out")
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

            OrderDetail detail = new OrderDetail();
            detail.setLaptop(laptop);
            detail.setQuantity(quantities.get(i));
            detail.setUnitPrice(laptop.getPrice());
            detail.setTotalPrice(laptop.getPrice() * quantities.get(i));
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
    public String cancelOrder(@RequestParam Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // Kiểm tra quyền của user
        if (!order.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa đơn này");
        }

        // Cập nhật trạng thái
        if (order.getOrderStatus() == Order.Status.Confirmed) {
            order.setOrderStatus(Order.Status.Cancelled);
        }

        orderRepository.save(order);
        return "redirect:/order-history";
    }

}
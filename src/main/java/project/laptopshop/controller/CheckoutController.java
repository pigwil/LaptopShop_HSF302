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
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    LaptopRepository laptopRepository;

    @GetMapping("/check-out")
    public String viewCheckout(@RequestParam String laptopCode,
                               HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Lấy laptop từ DB
        Laptop laptop = laptopRepository.findByLaptopCode(laptopCode);
        if (laptop == null) {
            throw new RuntimeException("Không tìm thấy laptop");
        }

        // Tạo CartItem tạm thời để hiển thị trên checkout
        CartItemDTO item = new CartItemDTO();
        item.setLaptopCode(laptop.getLaptopCode());
        item.setLaptopName(laptop.getLaptopName());
        item.setQuantity(1); // default 1
        item.setPrice(laptop.getPrice()); // price cho 1 chiếc

        model.addAttribute("cartItems", List.of(item));

        // Tính tổng tiền
        model.addAttribute("totalPrice", item.getPrice());

        // CheckoutDTO mặc định
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
            detail.setTotalPrice(laptop.getPrice() * quantities.get(i));
            detail.setOrder(order);
            details.add(detail);
        }

        order.setOrderDetails(details);
        orderRepository.save(order);

        return "redirect:/home";
    }

}
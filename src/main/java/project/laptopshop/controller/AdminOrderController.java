package project.laptopshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.laptopshop.entity.Order;
import project.laptopshop.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showOrderList(Model model,
                                @RequestParam(value = "status", required = false) String status) {

        List<Order> orders = orderService.getFilteredOrders(status);

        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Order Management");

        String currentStatus = (status == null || status.isEmpty()) ? "All" : status;
        model.addAttribute("currentStatus", currentStatus);

        return "admin/order-list";
    }

    @GetMapping("/{id}")
    public String showOrderDetail(@PathVariable Long id, Model model) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Order Details #" + id);
            return "admin/order-detail";
        } catch (Exception e) {
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") Order.Status newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating order: " + e.getMessage());
        }

        return "redirect:/admin/orders";
    }

    @PostMapping("/{id}/update-payment")
    public String updatePaymentStatus(@PathVariable Long id,
                                      @RequestParam("payed") boolean isPaid,
                                      RedirectAttributes redirectAttributes) {
        try {
            orderService.updatePaymentStatus(id, isPaid);
            redirectAttributes.addFlashAttribute("successMessage", "Payment status for Order #" + id + " updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating payment status: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }
}

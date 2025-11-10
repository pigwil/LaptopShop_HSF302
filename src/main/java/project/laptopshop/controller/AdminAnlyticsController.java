package project.laptopshop.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import project.laptopshop.repository.OrderDetailRepository;
import project.laptopshop.repository.UserRepository;

@Controller
public class AdminAnlyticsController {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/analytics")
    public String analyticsPage(Model model) {
        // Top 5 sản phẩm bán chạy
        var topProducts = orderDetailRepository.findTop5BestSellingProducts(PageRequest.of(0, 5));
        model.addAttribute("topProducts", topProducts);

        // Top 5 khách hàng mua nhiều nhất
        var topCustomers = userRepository.findTop5BestCustomers(PageRequest.of(0, 5));
        model.addAttribute("topCustomers", topCustomers);

        // Đặt title để highlight menu sidebar
        model.addAttribute("pageTitle", "Analytics");

        return "admin/analytics"; // Thymeleaf sẽ render admin/analytics.html
    }
}

package project.laptopshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.laptopshop.entity.Laptop;
import project.laptopshop.service.LaptopService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/laptops")
@RequiredArgsConstructor
public class AdminLaptopController {

    private final LaptopService laptopService;

    @GetMapping("/admin/dashboard")
    public String showDashboard() {
        return "admin/dashboard"; // templates/admin/dashboard.html
    }
    
    @GetMapping
    public String getAllLaptops(Model model) {
        List<Laptop> laptops = laptopService.getAllLaptops();
        model.addAttribute("laptops", laptops);
        model.addAttribute("pageTitle", "Quản lý Laptop");
        return "admin/laptop-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("laptop", new Laptop());
        model.addAttribute("pageTitle", "Thêm Laptop mới");
        model.addAttribute("actionUrl", "/admin/laptops/save");
        return "admin/laptop-form";
    }

    @PostMapping("/save")
    public String createLaptop(@ModelAttribute Laptop laptop,
                               @RequestParam(value = "image", required = false) MultipartFile imageFile)
            throws IOException {
        laptopService.createLaptop(laptop, imageFile);
        return "redirect:/admin/laptops";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Laptop laptop = laptopService.getLaptopById(id);
        model.addAttribute("laptop", laptop);
        model.addAttribute("pageTitle", "Chỉnh sửa Laptop");
        model.addAttribute("actionUrl", "/admin/laptops/update/" + id);
        return "admin/laptop-form";
    }

    @PostMapping("/update/{id}")
    public String updateLaptop(@PathVariable Long id,
                               @ModelAttribute Laptop laptop,
                               @RequestParam(value = "image", required = false) MultipartFile imageFile)
            throws IOException {
        laptopService.updateLaptop(id, laptop, imageFile);
        return "redirect:/admin/laptops";
    }

    @PostMapping("/delete/{id}")
    public String deleteLaptop(@PathVariable Long id) {
        laptopService.softDeleteLaptop(id);
        return "redirect:/admin/laptops";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam Laptop.LaptopStatus status) {
        laptopService.updateLaptopStatus(id, status);
        return "redirect:/admin/laptops";
    }
}

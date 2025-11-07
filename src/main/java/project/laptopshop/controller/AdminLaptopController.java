package project.laptopshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.laptopshop.entity.Laptop;
import project.laptopshop.service.LaptopService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminLaptopController {

    private final LaptopService laptopService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }
    
    // --- QUẢN LÝ SẢN PHẨM (LAPTOP) ---

    // Hiển thị danh sách tất cả laptop
    @GetMapping("/laptops")
    public String getAllLaptops(Model model) {
        List<Laptop> laptops = laptopService.getAllLaptops();
        model.addAttribute("laptops", laptops);
        model.addAttribute("activePage", "laptops");
        return "admin/laptop-list";
    }

    // Hiển thị form tạo laptop mới
    @GetMapping("/laptops/create")
    public String showCreateForm(Model model) {
        model.addAttribute("laptop", new Laptop());
        model.addAttribute("pageTitle", "Thêm Laptop mới");
        model.addAttribute("actionUrl", "/admin/laptops/save");
        model.addAttribute("activePage", "laptops");
        return "admin/laptop-form";
    }

    // Xử lý lưu laptop mới hoặc cập nhật laptop hiện có
    @PostMapping("/laptops/save")
    public String saveLaptop(@Valid @ModelAttribute("laptop") Laptop laptop,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes,
                             Model model) throws IOException {
        
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", laptop.getId() == 0 ? "Thêm Laptop mới" : "Chỉnh sửa Laptop");
            model.addAttribute("actionUrl", laptop.getId() == 0 ? "/admin/laptops/save" : "/admin/laptops/update/" + laptop.getId());
            model.addAttribute("activePage", "laptops");
            return "admin/laptop-form";
        }

        try {
            if (laptop.getId() == 0) { // Tạo mới
                laptopService.createLaptop(laptop, imageFile);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm laptop thành công!");
            } else { // Cập nhật
                laptopService.updateLaptop(laptop.getId(), laptop, imageFile);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật laptop thành công!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Nếu có lỗi, quay lại form và hiển thị lỗi
            model.addAttribute("pageTitle", laptop.getId() == 0 ? "Thêm Laptop mới" : "Chỉnh sửa Laptop");
            model.addAttribute("actionUrl", laptop.getId() == 0 ? "/admin/laptops/save" : "/admin/laptops/update/" + laptop.getId());
            model.addAttribute("activePage", "laptops");
            return "admin/laptop-form";
        }

        return "redirect:/admin/laptops";
    }

    // Hiển thị form chỉnh sửa laptop
    @GetMapping("/laptops/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Laptop laptop = laptopService.getLaptopById(id);
        model.addAttribute("laptop", laptop);
        model.addAttribute("pageTitle", "Chỉnh sửa Laptop");
        model.addAttribute("actionUrl", "/admin/laptops/save"); // Sẽ xử lý cập nhật trong saveLaptop
        model.addAttribute("activePage", "laptops");
        return "admin/laptop-form";
    }

    // Xử lý xóa mềm laptop
    @PostMapping("/laptops/delete/{id}")
    public String deleteLaptop(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            laptopService.softDeleteLaptop(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa mềm laptop thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/laptops";
    }

    // Cập nhật trạng thái laptop (ví dụ: từ Available sang Out_Of_Stock)
    @PostMapping("/laptops/{id}/status")
    public String updateLaptopStatus(@PathVariable Long id,
                                     @RequestParam Laptop.LaptopStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            laptopService.updateLaptopStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái laptop thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/laptops";
    }
}

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
import project.laptopshop.entity.User;
import project.laptopshop.service.LaptopService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminLaptopController {

    private final LaptopService laptopService;

    // ✅ Trang dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        return "admin/dashboard";
    }

    // ✅ Hiển thị danh sách laptop
    @GetMapping("/laptops")
    public String getAllLaptops(Model model) {
        List<Laptop> laptops = laptopService.getAllLaptops();
        model.addAttribute("laptops", laptops);
        model.addAttribute("pageTitle", "Quản lý Laptop");
        model.addAttribute("activePage", "laptops");
        return "admin/laptop-list";
    }

    // ✅ Form tạo laptop mới
    @GetMapping("/laptops/create")
    public String showCreateForm(Model model) {
        model.addAttribute("laptop", new Laptop());
        model.addAttribute("pageTitle", "Thêm Laptop mới");
        model.addAttribute("isEdit", false);
        model.addAttribute("actionUrl", "/admin/laptops/save");
        model.addAttribute("activePage", "laptops");
        return "admin/laptop-form";
    }

    // ✅ Form chỉnh sửa laptop
    @GetMapping("/laptops/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Laptop laptop = laptopService.getLaptopById(id);
        model.addAttribute("laptop", laptop);
        model.addAttribute("pageTitle", "Chỉnh sửa Laptop");
        model.addAttribute("isEdit", true);
        model.addAttribute("actionUrl", "/admin/laptops/save");
        model.addAttribute("activePage", "laptops");
        return "admin/laptop-form";
    }

    // ✅ Xử lý lưu (thêm mới hoặc cập nhật)
    @PostMapping("/laptops/save")
    public String saveLaptop(@Valid @ModelAttribute("laptop") Laptop laptop,
                             BindingResult result,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                             RedirectAttributes redirectAttributes,
                             Model model) throws IOException {

        // ⚠️ Nếu có lỗi validate thì quay lại form
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", laptop.getId() == null ? "Thêm Laptop mới" : "Chỉnh sửa Laptop");
            model.addAttribute("isEdit", laptop.getId() != null);
            model.addAttribute("actionUrl", "/admin/laptops/save");
            model.addAttribute("activePage", "laptops");
            return "admin/laptop-form";
        }

        try {
            if (laptop.getId() == null) {
                // ✅ Thêm mới: set user mặc định
                User defaultUser = new User();
                defaultUser.setId(1L); // giả sử admin có id = 1 trong DB
                laptop.setUser(defaultUser);

                laptopService.createLaptop(laptop, imageFile);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm laptop thành công!");
            } else {
                // ✅ Cập nhật
                laptopService.updateLaptop(laptop.getId(), laptop, imageFile);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật laptop thành công!");
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", laptop.getId() == null ? "Thêm Laptop mới" : "Chỉnh sửa Laptop");
            model.addAttribute("isEdit", laptop.getId() != null);
            model.addAttribute("actionUrl", "/admin/laptops/save");
            model.addAttribute("activePage", "laptops");
            return "admin/laptop-form";
        }

        return "redirect:/admin/laptops";
    }

    // ✅ Xóa mềm laptop
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

    // ✅ Cập nhật trạng thái laptop
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

package project.laptopshop.service;

import org.springframework.web.multipart.MultipartFile;
import project.laptopshop.entity.Laptop;

import java.io.IOException;
import java.util.List;

public interface LaptopService {
    List<Laptop> getAllLaptops();
    Laptop getLaptopById(Long id);
    Laptop getLaptopByCode(String laptopCode);
    Laptop createLaptop(Laptop laptop, MultipartFile imageFile) throws IOException;
    Laptop updateLaptop(Long id, Laptop updatedLaptop, MultipartFile imageFile) throws IOException;
    void softDeleteLaptop(Long id);
    void updateLaptopStatus(Long id, Laptop.LaptopStatus status);
}

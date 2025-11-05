package project.laptopshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.laptopshop.entity.Laptop;
import project.laptopshop.repository.LaptopRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LaptopServiceImpl implements LaptopService {

    private final LaptopRepository laptopRepository;

    @Value("${upload.path:laptop-images}")
    private String uploadDir;

    @Override
    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAllActive();
    }

    @Override
    public Laptop getLaptopById(Long id) {
        Laptop laptop = laptopRepository.findByIdAndIs_deleted(id, 0);
        if (laptop == null) throw new RuntimeException("Laptop không tồn tại: " + id);
        return laptop;
    }

    @Override
    public Laptop getLaptopByCode(String laptopCode) {
        Laptop laptop = laptopRepository.findActiveByLaptopCode(laptopCode);
        if (laptop == null) throw new RuntimeException("Laptop không tồn tại: " + laptopCode);
        return laptop;
    }

    @Override
    public Laptop createLaptop(Laptop laptop, MultipartFile imageFile) throws IOException {
        if (laptopRepository.existsByLaptopCode(laptop.getLaptopCode())) {
            throw new RuntimeException("Mã laptop đã tồn tại!");
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            laptop.setImgPath(saveImageFile(imageFile));
        }
        laptop.setLaptopStatus(Laptop.LaptopStatus.Available);
        laptop.setIs_deleted(0);
        return laptopRepository.save(laptop);
    }

    @Override
    public Laptop updateLaptop(Long id, Laptop updatedLaptop, MultipartFile imageFile) throws IOException {
        Laptop laptop = laptopRepository.findByIdAndIs_deleted(id, 0);
        if (laptop == null) throw new RuntimeException("Laptop không tồn tại: " + id);

        laptop.setLaptopName(updatedLaptop.getLaptopName());
        laptop.setBrand(updatedLaptop.getBrand());
        laptop.setCpuInfo(updatedLaptop.getCpuInfo());
        laptop.setRamInfo(updatedLaptop.getRamInfo());
        laptop.setPrice(updatedLaptop.getPrice());
        laptop.setLaptopStatus(updatedLaptop.getLaptopStatus());

        if (imageFile != null && !imageFile.isEmpty()) {
            laptop.setImgPath(saveImageFile(imageFile));
        }

        return laptopRepository.save(laptop);
    }

    @Override
    public void softDeleteLaptop(Long id) {
        Laptop laptop = laptopRepository.findByIdAndIs_deleted(id, 0);
        if (laptop == null) throw new RuntimeException("Laptop không tồn tại: " + id);
        laptop.setIs_deleted(1);
        laptopRepository.save(laptop);
    }

    @Override
    public void updateLaptopStatus(Long id, Laptop.LaptopStatus status) {
        Laptop laptop = laptopRepository.findByIdAndIs_deleted(id, 0);
        if (laptop == null) throw new RuntimeException("Laptop không tồn tại: " + id);
        laptop.setLaptopStatus(status);
        laptopRepository.save(laptop);
    }

    private String saveImageFile(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Không thể tạo thư mục: " + uploadDir);

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID() + extension;

        File dest = new File(dir, fileName);
        file.transferTo(dest);

        return uploadDir + "/" + fileName;
    }
}

package project.laptopshop.service;

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
public class LaptopServiceImpl implements LaptopService {

    private final LaptopRepository laptopRepository;
    private final String uploadDir;

    public LaptopServiceImpl(LaptopRepository laptopRepository, @Value("${upload.path:laptop-images}") String uploadDir) {
        this.laptopRepository = laptopRepository;
        this.uploadDir = uploadDir;
    }

    @Override
    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAllActive();
    }

    @Override
    public Laptop getLaptopById(Long id) {
        return laptopRepository.findByIdAndIs_deleted(id, 0)
                .orElseThrow(() -> new RuntimeException("Laptop không tồn tại: " + id));
    }

    @Override
    public Laptop getLaptopByCode(String laptopCode) {
        return laptopRepository.findActiveByLaptopCode(laptopCode)
                .orElseThrow(() -> new RuntimeException("Laptop không tồn tại: " + laptopCode));
    }

    @Override
    public Laptop createLaptop(Laptop laptop, MultipartFile imageFile) throws IOException {
        if (laptopRepository.existsByLaptopCode(laptop.getLaptopCode())) {
            throw new RuntimeException("Mã laptop đã tồn tại!");
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            laptop.setImgPath(saveImageFile(imageFile));
        } else {
            laptop.setImgPath("/images/default-laptop.png");
        }
        if (laptop.getQuantityInStock() > 0) {
            laptop.setLaptopStatus(Laptop.LaptopStatus.Available);
        } else {
            laptop.setLaptopStatus(Laptop.LaptopStatus.Out_Of_Stock);
        }
        laptop.setIs_deleted(0);
        return laptopRepository.save(laptop);
    }

    @Override
    public Laptop updateLaptop(Long id, Laptop updatedLaptop, MultipartFile imageFile) throws IOException {
        Laptop laptop = getLaptopById(id);

        laptop.setLaptopName(updatedLaptop.getLaptopName());
        laptop.setBrand(updatedLaptop.getBrand());
        laptop.setCpuInfo(updatedLaptop.getCpuInfo());
        laptop.setRamInfo(updatedLaptop.getRamInfo());
        laptop.setPrice(updatedLaptop.getPrice());
        laptop.setQuantityInStock(updatedLaptop.getQuantityInStock());

        if (updatedLaptop.getQuantityInStock() > 0) {
            laptop.setLaptopStatus(Laptop.LaptopStatus.Available);
        } else {
            laptop.setLaptopStatus(Laptop.LaptopStatus.Out_Of_Stock);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            laptop.setImgPath(saveImageFile(imageFile));
        }

        return laptopRepository.save(laptop);
    }

    @Override
    public void softDeleteLaptop(Long id) {
        Laptop laptop = getLaptopById(id);
        laptop.setIs_deleted(1);
        laptopRepository.save(laptop);
    }

    @Override
    public void updateLaptopStatus(Long id, Laptop.LaptopStatus status) {
        Laptop laptop = getLaptopById(id);
        laptop.setLaptopStatus(status);
        laptopRepository.save(laptop);
    }

    private String saveImageFile(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Không thể tạo thư mục: " + uploadDir);
        }

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID() + extension;

        File dest = new File(dir, fileName);
        file.transferTo(dest);

        return "/" + uploadDir + "/" + fileName;
    }
}

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
            laptop.setImgPath(saveImageFile(imageFile)); // Sửa từ setImage thành setImgPath
        } else {
            laptop.setImgPath("/images/default-laptop.png"); // Đặt ảnh mặc định nếu không có ảnh được tải lên
        }
        laptop.setLaptopStatus(Laptop.LaptopStatus.Available);
        // Đảm bảo trường is_deleted được thiết lập khi tạo mới
        laptop.setIs_deleted(0);
        return laptopRepository.save(laptop);
    }

    @Override
    public Laptop updateLaptop(Long id, Laptop updatedLaptop, MultipartFile imageFile) throws IOException {
        Laptop laptop = getLaptopById(id);

        laptop.setLaptopName(updatedLaptop.getLaptopName()); // Sửa từ setName thành setLaptopName
        laptop.setBrand(updatedLaptop.getBrand());
        laptop.setCpuInfo(updatedLaptop.getCpuInfo()); // Sửa từ setSpecs thành setCpuInfo
        laptop.setRamInfo(updatedLaptop.getRamInfo()); // Thêm setRamInfo
        laptop.setPrice(updatedLaptop.getPrice());
        // Loại bỏ setOldPrice vì không có trường này trong entity
        laptop.setLaptopStatus(updatedLaptop.getLaptopStatus());

        if (imageFile != null && !imageFile.isEmpty()) {
            laptop.setImgPath(saveImageFile(imageFile)); // Sửa từ setImage thành setImgPath
        }
        // Giữ nguyên ảnh cũ nếu không có ảnh mới được tải lên

        return laptopRepository.save(laptop);
    }

    @Override
    public void softDeleteLaptop(Long id) {
        Laptop laptop = getLaptopById(id);
        laptop.setIs_deleted(1); // Cập nhật trường is_deleted
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

        return "/" + uploadDir + "/" + fileName; // Trả về đường dẫn tương đối để dùng trong HTML
    }
}

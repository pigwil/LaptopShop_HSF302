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

    // Đường dẫn thư mục upload (lấy từ application.properties)
    private final String uploadDir;

    public LaptopServiceImpl(LaptopRepository laptopRepository,
                             @Value("${upload.path:uploads/laptop-images}") String uploadDir) {
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
        if (laptopRepository.existsByLaptopCode(laptop.getLaptopCode().trim())) {
            throw new RuntimeException("Mã laptop đã tồn tại!");
        }

        if (laptop.getUser() == null) {
            throw new RuntimeException("Laptop phải có user!");
        }

        // Lưu ảnh nếu có
        if (imageFile != null && !imageFile.isEmpty()) {
            laptop.setImgPath(saveImageFile(imageFile));
        } else {
            laptop.setImgPath("/images/default-laptop.png");
        }

        // Nếu trạng thái không được set thủ công, tự động set theo tồn kho
        if (laptop.getLaptopStatus() == null) {
            laptop.setLaptopStatus(
                    laptop.getQuantityInStock() > 0
                            ? Laptop.LaptopStatus.Available
                            : Laptop.LaptopStatus.Out_Of_Stock
            );
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
        
        // Cập nhật trạng thái từ form
        laptop.setLaptopStatus(updatedLaptop.getLaptopStatus());

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

    /**
     * Lưu ảnh vào thư mục uploads/laptop-images trong project và trả về URL cho trình duyệt
     */
    private String saveImageFile(MultipartFile file) throws IOException {
        // Lấy đường dẫn tuyệt đối tới thư mục uploads/laptop-images
        String absolutePath = System.getProperty("user.dir") + File.separator + uploadDir;

        File dir = new File(absolutePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Không thể tạo thư mục: " + absolutePath);
        }

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID() + extension;

        File dest = new File(dir, fileName);
        file.transferTo(dest);

        // Trả về URL để hiển thị ảnh
        return "/" + uploadDir.replace("\\", "/") + "/" + fileName;
    }
}

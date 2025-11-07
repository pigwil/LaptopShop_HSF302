package project.laptopshop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import project.laptopshop.entity.Laptop;
import project.laptopshop.entity.Order;
import project.laptopshop.entity.OrderDetail;
import project.laptopshop.entity.User;
import project.laptopshop.repository.LaptopRepository;
import project.laptopshop.repository.OrderDetailRepository;
import project.laptopshop.repository.OrderRepository;
import project.laptopshop.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
public class DataInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LaptopRepository laptopRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public DataInit(UserRepository userRepository, LaptopRepository laptopRepository, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.userRepository = userRepository;
        this.laptopRepository = laptopRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Bắt đầu khởi tạo dữ liệu mẫu...");

        // --- Tạo User ---
        // Sửa logic: Chỉ tạo nếu user "admin" chưa tồn tại
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUserCode("ADMIN001");
            admin.setFullName("Administrator");
            admin.setUsername("admin");
            admin.setEmail("admin@laptopshop.com");
            admin.setPassword("admin123");
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            log.info("Đã tạo tài khoản admin.");
        }

        if (!userRepository.existsByUsername("user01")) {
            User user = new User();
            user.setUserCode("USER000001");
            user.setFullName("John Smith");
            user.setUsername("user01");
            user.setEmail("user01@example.com");
            user.setPhone("0912345678");
            user.setAddress("Ha Noi");
            user.setPassword("123456");
            user.setRole(User.Role.USER);
            userRepository.save(user);
            log.info("Đã tạo tài khoản user01.");
        }

        // --- Tạo Laptop (chỉ khi chưa có laptop nào) ---
        if (laptopRepository.count() == 0) {
            Optional<User> adminOptional = userRepository.findByUsername("admin");
            if (adminOptional.isEmpty()) {
                log.error("Không tìm thấy tài khoản admin để gán cho laptop.");
                return;
            }
            User admin = adminOptional.get();

            Laptop l1 = new Laptop();
            l1.setLaptopCode("LAP001");
            l1.setLaptopName("Dell XPS 13");
            l1.setBrand("Dell");
            l1.setCpuInfo("Intel Core i7 12th Gen");
            l1.setRamInfo("16GB LPDDR5");
            l1.setPrice(25000000);
            l1.setQuantityInStock(10);
            l1.setLaptopStatus(Laptop.LaptopStatus.Available);
            l1.setImgPath("/images/dell-xps-13.jpg");
            l1.setUser(admin);
            laptopRepository.save(l1);

            log.info("Đã tạo laptop mẫu.");
        }

        log.info("Khởi tạo dữ liệu hoàn tất.");
    }
}

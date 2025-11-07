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
            user.setUserCode("USER001");
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

        // --- Tạo Laptop ---
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
            l1.setQuantityInStock(10); // <-- ĐÃ THÊM
            l1.setLaptopStatus(Laptop.LaptopStatus.Available);
            l1.setImgPath("/images/dell-xps-13.jpg");
            l1.setUser(admin);
            laptopRepository.save(l1);

            Laptop l2 = new Laptop();
            l2.setLaptopCode("LAP002");
            l2.setLaptopName("MacBook Air M3");
            l2.setBrand("Apple");
            l2.setCpuInfo("Apple M3 chip");
            l2.setRamInfo("8GB Unified Memory");
            l2.setPrice(32000000);
            l2.setQuantityInStock(5); // <-- ĐÃ THÊM
            l2.setLaptopStatus(Laptop.LaptopStatus.Available);
            l2.setImgPath("/images/macbook-air-m3.jpg");
            l2.setUser(admin);
            laptopRepository.save(l2);

            log.info("Đã tạo 2 laptop mẫu.");
        }

        // --- Tạo Order và OrderDetail ---
        if (orderRepository.count() == 0 && userRepository.existsByUsername("user01") && laptopRepository.count() > 0) {
            Optional<User> userOptional = userRepository.findByUsername("user01");
            Optional<Laptop> dellOptional = laptopRepository.findActiveByLaptopCode("LAP001");

            if (userOptional.isPresent() && dellOptional.isPresent()) {
                User user = userOptional.get();
                Laptop dell = dellOptional.get();

                Order order1 = new Order();
                order1.setCreatedBy(user);
                order1.setCreatedDate(LocalDate.now());
                order1.setOrderStatus(Order.Status.Draft);
                order1.setPayed(false);
                order1.setIs_archived(0);
                order1.setShippingName(user.getFullName());
                order1.setShippingPhone(user.getPhone());
                order1.setShippingAddress(user.getAddress());
                Order savedOrder = orderRepository.save(order1);

                OrderDetail d1 = new OrderDetail();
                d1.setOrder(savedOrder);
                d1.setLaptop(dell);
                d1.setQuantity(1);
                d1.setUnitPrice(dell.getPrice());
                orderDetailRepository.save(d1);

                log.info("Đã tạo 1 đơn hàng mẫu.");
            }
        }

        log.info("Khởi tạo dữ liệu hoàn tất.");
    }
}

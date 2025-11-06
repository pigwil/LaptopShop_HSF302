package project.laptopshop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.time.LocalDateTime;

@Slf4j
@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LaptopRepository laptopRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUserCode("ADMIN001");
            admin.setFullName("Administrator");
            admin.setUsername("admin");
            admin.setEmail("admin@laptopshop.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user01")) {
            User user = new User();
            user.setUserCode("USER001");
            user.setFullName("John Smith");
            user.setUsername("user01");
            user.setEmail("user01@example.com");
            user.setPhone("0912345678");
            user.setAddress("Ha Noi");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(User.Role.USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        User admin = userRepository.findByUsername("admin").get();
        User user = userRepository.findByUsername("user01").get();

        if (laptopRepository.count() == 0) {
            Laptop l1 = new Laptop();
            l1.setLaptopCode("LAP001");
            l1.setLaptopName("Dell XPS 13");
            l1.setBrand("Dell");
            l1.setCpuInfo("Intel Core i7 12th Gen");
            l1.setRamInfo("16GB LPDDR5");
            l1.setPrice(25000000);
            l1.setQuantityInStock(10);
            l1.setLaptopStatus(Laptop.LaptopStatus.Available);
            l1.setImgPath("https://example.com/img/dellxps13.jpg");
            l1.setUser(admin);
            laptopRepository.save(l1);

            Laptop l2 = new Laptop();
            l2.setLaptopCode("LAP002");
            l2.setLaptopName("MacBook Air M3");
            l2.setBrand("Apple");
            l2.setCpuInfo("Apple M3 chip");
            l2.setRamInfo("8GB Unified Memory");
            l2.setPrice(32000000);
            l2.setQuantityInStock(5);
            l2.setLaptopStatus(Laptop.LaptopStatus.Available);
            l2.setImgPath("https://example.com/img/macbookairm3.jpg");
            l2.setUser(admin);
            laptopRepository.save(l2);

            Laptop l3 = new Laptop();
            l3.setLaptopCode("LAP003");
            l3.setLaptopName("ASUS TUF Gaming F15");
            l3.setBrand("ASUS");
            l3.setCpuInfo("Intel Core i5-12500H");
            l3.setRamInfo("16GB DDR5, RTX 3060");
            l3.setPrice(28000000);
            l3.setQuantityInStock(7);
            l3.setLaptopStatus(Laptop.LaptopStatus.Available);
            l3.setImgPath("https://example.com/img/asusf15.jpg");
            l3.setUser(admin);
            laptopRepository.save(l3);

        }

        Laptop dell = laptopRepository.findByLaptopCode("LAP001");
        Laptop macbook = laptopRepository.findByLaptopCode("LAP002");

        if (orderRepository.count() == 0) {
            Order order1 = new Order();
            order1.setCreatedBy(user);
            order1.setCreatedDate(LocalDate.now());
            order1.setOrderStatus(Order.Status.Draft);
            order1.setPayed(false);
            order1.setIs_archived(0);
            order1.setShippingName("John Smith");
            order1.setShippingPhone("0912345678");
            order1.setShippingAddress("123 Main Street, Ha Noi");
            orderRepository.save(order1);

            Order order2 = new Order();
            order2.setCreatedBy(user);
            order2.setCreatedDate(LocalDate.now().minusDays(2));
            order2.setOrderStatus(Order.Status.Confirmed);
            order2.setPayed(true);
            order2.setIs_archived(0);
            order2.setShippingName("John Smith");
            order2.setShippingPhone("0912345678");
            order2.setShippingAddress("123 Main Street, Ha Noi");
            orderRepository.save(order2);

        }

        Order order1 = orderRepository.findAll().get(0);
        Order order2 = orderRepository.findAll().get(1);

        if (orderDetailRepository.count() == 0) {
            OrderDetail d1 = new OrderDetail();
            d1.setOrder(order1);
            d1.setLaptop(dell);
            d1.setQuantity(1);
            d1.setUnitPrice(25000000);
            orderDetailRepository.save(d1);

            OrderDetail d2 = new OrderDetail();
            d2.setOrder(order2);
            d2.setLaptop(macbook);
            d2.setQuantity(2);
            d2.setUnitPrice(32000000);
            orderDetailRepository.save(d2);
        }
    }

}

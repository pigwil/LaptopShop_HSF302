package project.laptopshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.laptopshop.entity.User;
import project.laptopshop.repository.UserRepository;

import java.time.LocalDateTime;

@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
            System.out.println("✅ Đã tạo tài khoản ADMIN: admin / admin123");
        }
    }
}
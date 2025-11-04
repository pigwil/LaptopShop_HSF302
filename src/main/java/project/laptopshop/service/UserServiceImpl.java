package project.laptopshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.callback.EntityCallback;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.laptopshop.dto.ChangePasswordDTO;
import project.laptopshop.dto.RegisterDTO;
import project.laptopshop.entity.User;
import project.laptopshop.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityCallback entityCallback;

    @Override
    public User register(RegisterDTO dto) throws Exception {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Mật khẩu xác nhận không khớp");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new Exception("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception("Email đã tồn tại");
        }

        User user = new User();
        String userCode = "USER" + String.format("%06d", userRepository.count() + 1);
        user.setUserCode(userCode);
        user.setFullName(dto.getUsername());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) throws Exception {
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new Exception("Mật khẩu mới xác nhận không khớp");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("Không tìm thấy tài khoản"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new Exception("Mật khẩu cũ không đúng");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
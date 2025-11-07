package project.laptopshop.service;

import org.springframework.stereotype.Service;
import project.laptopshop.dto.ChangePasswordDTO;
import project.laptopshop.dto.LoginDTO;
import project.laptopshop.dto.RegisterDTO;
import project.laptopshop.entity.User;
import project.laptopshop.repository.UserRepository;

import java.util.Optional;

@Service
public class    UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        // Đảm bảo lấy đúng fullName từ DTO
        user.setFullName(registerDTO.getFullName()); 
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setRole(User.Role.USER);

        // Logic tạo userCode nhất quán
        long count = userRepository.count();
        String userCode = "USER" + String.format("%06d", count + 1);
        user.setUserCode(userCode);

        userRepository.save(user);
    }

    @Override
    public Optional<User> login(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByUsername(loginDTO.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (loginDTO.getPassword().equals(user.getPassword())) {
                return userOptional;
            }
        }
        return Optional.empty();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu mới xác nhận không khớp");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));

        if (!dto.getOldPassword().equals(user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }
}

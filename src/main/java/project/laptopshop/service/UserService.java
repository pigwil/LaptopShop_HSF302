package project.laptopshop.service;

import project.laptopshop.dto.ChangePasswordDTO;
import project.laptopshop.dto.LoginDTO;
import project.laptopshop.dto.RegisterDTO;
import project.laptopshop.entity.User;

import java.util.Optional;

public interface UserService {
    void register(RegisterDTO registerDTO);
    Optional<User> login(LoginDTO loginDTO);
    User findByUsername(String username);
    void changePassword(Long userId, ChangePasswordDTO dto);
}

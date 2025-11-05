package project.laptopshop.service;

import project.laptopshop.dto.ChangePasswordDTO;
import project.laptopshop.dto.RegisterDTO;
import project.laptopshop.entity.User;

public interface UserService {

    User register(RegisterDTO dto) throws Exception;

    void changePassword(Long userId, ChangePasswordDTO dto) throws Exception;

    User findByUsername(String username);

    User findById(Long id);
}
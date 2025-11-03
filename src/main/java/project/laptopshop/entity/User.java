package project.laptopshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank
    @Size(max = 10)
    @Column(name = "user_code", length = 10, unique = true, nullable = false)
    private String userCode;

    @NotBlank
    @Size(max = 50)
    @Column(name = "full_name", length = 50, nullable = false)
    private String fullName;

    @Size(max = 150)
    @Column(name = "address", length = 150)
    private String address;

    @Pattern(
            regexp = "^(\\+84|0)(3|5|7|8|9)\\d{8}$",
            message = "Số điện thoại phải hợp lệ của Việt Nam (bắt đầu bằng 0 hoặc +84, gồm 10 chữ số)."
    )
    @Column(name = "phone", length = 15)
    private String phone;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank
    @Size(min = 8, max = 25)
    @Column(name = "password", nullable = false, length = 25)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Laptop> laptops;

    public enum Role {
        Admin,
        User
    }

}

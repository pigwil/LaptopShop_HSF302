package project.laptopshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "laptops")
@AllArgsConstructor
@NoArgsConstructor
public class Laptop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(max = 10)
    @Column(name = "laptop_code", length = 10, nullable = false, unique = true)
    private String laptopCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "laptop_name", length = 100, nullable = false)
    private String laptopName;

    @Enumerated(EnumType.STRING)
    @Column(name = "laptop_status", nullable = false)
    private LaptopStatus laptopStatus;

    @NotBlank
    @Size(max = 50)
    @Column(name = "brand", length = 50, nullable = false)
    private String brand;

    @Size(max = 255)
    @Column(name = "cpu_info")
    private String cpuInfo;

    @Size(max = 255)
    @Column(name = "ram_info")
    private String ramInfo;

    @Positive
    @Column(name = "price", nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_code",
            referencedColumnName = "user_code",
            nullable = false
    )
    private User user;

    @OneToMany(mappedBy = "laptop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @NotBlank
    @Column(name = "img_path", nullable = false, length = 1000)
    private String imgPath;

    public enum LaptopStatus {
        Stock,
        Archived,
        Available
    }
}

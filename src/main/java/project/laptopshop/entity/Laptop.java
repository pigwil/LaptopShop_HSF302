package project.laptopshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
    private Long id;

    @NotBlank
    @Size(max = 10)
    @Column(name = "laptop_code", length = 10, nullable = false, unique = true)
    private String laptopCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "laptop_name", columnDefinition = "NVARCHAR(100)", nullable = false)
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

    @Min(0)
    @Column(name = "quantity_in_stock", nullable = false)
    private int quantityInStock; // <-- ĐÃ THÊM TRƯỜNG NÀY

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "laptop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @NotBlank
    @Column(name = "img_path", nullable = false, length = 1000)
    private String imgPath = "/images/default-laptop.png";

    @Column(name = "is_deleted")
    private int is_deleted = 0;

    public Laptop(String laptopCode, String laptopName, LaptopStatus laptopStatus, String brand, String cpuInfo, String ramInfo, double price, int quantityInStock, User user, String imgPath, int is_deleted) {
        this.laptopCode = laptopCode;
        this.laptopName = laptopName;
        this.laptopStatus = laptopStatus;
        this.brand = brand;
        this.cpuInfo = cpuInfo;
        this.ramInfo = ramInfo;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.user = user;
        this.imgPath = imgPath;
        this.is_deleted = is_deleted;
    }

    public enum LaptopStatus {
        Out_Of_Stock,
        Archived,
        Available
    }


}

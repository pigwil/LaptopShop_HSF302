package project.laptopshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false) // Sửa: Dùng order_id (khóa chính)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laptop_id", nullable = false) // Sửa: Dùng laptop_id (khóa chính)
    private Laptop laptop;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Positive
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    public OrderDetail(Order draftOrder, Laptop laptop, @Positive double price, Integer quantity) {
    }

    @Transient
    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}

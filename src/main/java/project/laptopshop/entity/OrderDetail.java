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
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laptop_code", referencedColumnName = "laptop_code", nullable = false)
    private Laptop laptop;

    @Min(1)
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Positive
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Transient
    public double getTotalPrice() {
        return unitPrice * quantity;
    }
}

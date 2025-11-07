package project.laptopshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Sửa: Dùng user_id (khóa chính)
    private User createdBy;

    @PastOrPresent
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    private Status orderStatus;

    @Column(name = "payed", nullable = false)
    private boolean payed = false;

    @Column(name = "is_archived")
    private int is_archived;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_Phone")
    private String shippingPhone;

    @Column(name = "shipping_Name")
    private String shippingName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @Transient
    public double getTotalAmount() {
        double total = 0.0;
        if (this.orderDetails != null) {
            for (OrderDetail detail : this.orderDetails) {
                total += detail.getTotalPrice();
            }
        }
        return total;
    }

    public enum Status {
        Draft,
        Confirmed,
        Cancelled,
        Delivered,
        Return,
    }
}

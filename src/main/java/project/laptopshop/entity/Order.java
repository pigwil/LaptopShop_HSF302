package project.laptopshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "created_by",
            referencedColumnName = "user_code",
            nullable = false
    )
    private User createdBy;

    @PastOrPresent
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate = LocalDate.now();

    @NotBlank
    @Size(max = 20)
    @Column(name = "order_type", length = 20, nullable = false)
    private String orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    private Status orderStatus;

    @Column(name = "payed", nullable = false)
    private boolean payed = false;

    @Column(name = "is_archived")
    private int is_archived;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    public enum Status {
        Draft,
        Confirmed,
        Cancelled,
        Delivered
    }
}

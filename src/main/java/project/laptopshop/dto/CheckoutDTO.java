package project.laptopshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDTO {
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;

    @NotBlank(message = "Số điện thoại giao hàng không được để trống")
    private String shippingPhone;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String shippingName;

    private String paymentMethod = "CASH";
}

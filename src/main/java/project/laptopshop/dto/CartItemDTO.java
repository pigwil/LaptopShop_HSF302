package project.laptopshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private String laptopCode;
    private String laptopName;
    private Double price;
    private Integer quantity;
    private int quantityInStock;

    public CartItemDTO(@NotBlank @Size(max = 10) String laptopCode, @NotBlank @Size(max = 100) String laptopName, @Positive double unitPrice, @Min(1) int quantity) {
    }
}

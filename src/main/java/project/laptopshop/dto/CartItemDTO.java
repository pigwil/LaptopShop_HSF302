package project.laptopshop.dto;

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
}

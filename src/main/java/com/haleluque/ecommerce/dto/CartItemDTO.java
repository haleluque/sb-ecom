package com.haleluque.ecommerce.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long cartItemId;
    private double discount;
    //private double productPrice;
    private Integer quantity;
    private ProductDTO product;
}

package com.ecommerce.sb_ecom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long productId;
    private String ProductName;
    private  String image;
    private String description;
    private Integer quantity;
    private double discount;
    private  double specialPrice;
}

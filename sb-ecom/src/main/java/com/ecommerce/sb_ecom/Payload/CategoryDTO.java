package com.ecommerce.sb_ecom.Payload;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    //@Schema(description = "Category ID " )
    private Long CategoryId;
   // @Schema(description = "Category name foe category you wish to create")
    private String CategoryName;

}

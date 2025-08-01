package com.ecommerce.sb_ecom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponce {
     List<CategoryDTO> content;
     private Integer pageNumber;
     private  Integer pageSize;
     private  Integer totalElement;
     private  Integer totalPage;
     private  boolean lastPage;
}

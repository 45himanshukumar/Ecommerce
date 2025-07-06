package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Model.Category;
import com.ecommerce.sb_ecom.Payload.CategoryDTO;
import com.ecommerce.sb_ecom.Payload.CategoryResponce;



public interface CategoryService {
    CategoryResponce getAllcategories();
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    String deletecategory(Long categoryId);

    CategoryDTO updatecategory(CategoryDTO categoryDTO,Long categoryId);
}

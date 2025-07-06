package com.ecommerce.sb_ecom.Repositry;

import com.ecommerce.sb_ecom.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryName);
}

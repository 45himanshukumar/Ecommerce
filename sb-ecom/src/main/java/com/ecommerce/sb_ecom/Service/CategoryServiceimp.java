package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Exception.APIException;
import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.Category;
import com.ecommerce.sb_ecom.Payload.CategoryDTO;
import com.ecommerce.sb_ecom.Payload.CategoryResponce;
import com.ecommerce.sb_ecom.Repositry.CategoryRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceimp implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CategoryResponce getAllcategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty())
            throw new APIException("No category created till now.");
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponce categoryResponse = new CategoryResponce();
        categoryResponse.setContent(categoryDTOS);
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category=  modelMapper.map(categoryDTO ,Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryFromDb != null)
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!");

         Category saveCategory=categoryRepository.save(category);
        return modelMapper.map(saveCategory,CategoryDTO.class);
    }

    @Override
    public String deletecategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourseNotFoundException("Category","categoryId",categoryId));

        categoryRepository.delete(category);
        return "Category with categoryId: " + categoryId + " deleted successfully !!";
    }

    @Override
    public CategoryDTO updatecategory(CategoryDTO categoryDTO, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourseNotFoundException("Category","categoryId",categoryId));
        Category category= modelMapper.map(categoryDTO,Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }
}

package com.ecommerce.sb_ecom.Controller;

import com.ecommerce.sb_ecom.Model.Category;
import com.ecommerce.sb_ecom.Payload.CategoryDTO;
import com.ecommerce.sb_ecom.Payload.CategoryResponce;
import com.ecommerce.sb_ecom.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
public class categorycontroller {

    private CategoryService categoryService;
   // private Long nextid=1L;
    public categorycontroller(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @GetMapping("api/public/categories")
    public ResponseEntity<CategoryResponce> getAllcategory(){
        CategoryResponce categoryResponce=categoryService.getAllcategories();
        return  new ResponseEntity<>(categoryResponce,HttpStatus.OK);
    }
    @PostMapping("api/public/categories")
    public ResponseEntity<CategoryDTO> CreateCategory( @Valid @RequestBody CategoryDTO categoryDTO){
     //   category.setCategoryId(nextid++);
        categoryDTO.setCategoryId(null);
       CategoryDTO savedCategoryDto= categoryService.createCategory(categoryDTO);
        return  new ResponseEntity<>(savedCategoryDto,HttpStatus.CREATED);
    }
    @DeleteMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<String> Deletecategory(@PathVariable Long categoryId){
        //try {
            String status = categoryService.deletecategory(categoryId);
            return new ResponseEntity<>(status, HttpStatus.OK);
       // }catch (ResponseStatusException e){
          //  return new ResponseEntity<>(e.getReason(),e.getStatusCode());
       // }
    }

    @PutMapping("api/admin/categories/{categoryId}")
  public ResponseEntity<CategoryDTO>Updatcategory( @Valid @RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){
     // try{
          CategoryDTO savedCategoryDTO = categoryService.updatecategory(categoryDTO,categoryId);
          return new ResponseEntity<>(savedCategoryDTO, HttpStatus.OK);
    //  }catch(ResponseStatusException e){
      //    return new ResponseEntity<>(e.getReason(),e.getStatusCode());
      //}
  }
}

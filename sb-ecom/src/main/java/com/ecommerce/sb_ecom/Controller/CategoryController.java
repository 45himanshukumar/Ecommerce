package com.ecommerce.sb_ecom.Controller;

import com.ecommerce.sb_ecom.Configure.AppConstant;
import com.ecommerce.sb_ecom.Payload.CategoryDTO;
import com.ecommerce.sb_ecom.Payload.CategoryResponce;
import com.ecommerce.sb_ecom.Service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;




@RestController
public class CategoryController {

    private CategoryService categoryService;
   // private Long nextid=1L;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Tag(name = "Category APIS",description = "APIs for managing categories")
    @GetMapping("api/public/categories")
    public ResponseEntity<CategoryResponce> getAllcategory(@RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false ) Integer pageNumber
            ,@RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE ,required = false) Integer pageSize
            ,@RequestParam(name="SortBy" ,defaultValue = AppConstant.SORT_CATEGORY_BY,required = false) String sortBy
             ,@RequestParam(name="sortOrder" ,defaultValue = AppConstant.SORT_DIR,required = false) String sortorder
    )
    {
        CategoryResponce categoryResponce=categoryService.getAllcategories(pageNumber, pageSize,sortBy,sortorder);
        return  new ResponseEntity<>(categoryResponce,HttpStatus.OK);
    }
    @Tag(name = "Category APIS",description = "APIs for managing categories")
   @Operation(summary = "Create Category" ,description = "API to crate a new category")
    @ApiResponses({@ApiResponse(
            responseCode = "201", description = "Category is created successfully"),
            @ApiResponse(
                    responseCode = "400", description = "Invaild Input",content = @Content),
            @ApiResponse(
                    responseCode = "500", description = "Internal Server Error"),

    })
    @PostMapping("api/admin/categories")
    public ResponseEntity<CategoryDTO> CreateCategory( @Valid @RequestBody CategoryDTO categoryDTO){
     //   category.setCategoryId(nextid++);
        //categoryDTO.setCategoryId(null);
       CategoryDTO savedCategoryDto= categoryService.createCategory(categoryDTO);
        return  new ResponseEntity<>(savedCategoryDto,HttpStatus.CREATED);
    }
    @DeleteMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> Deletecategory(@PathVariable Long categoryId){
        //try {
            CategoryDTO deletedCategory = categoryService.deletecategory(categoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
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

package com.ecommerce.sb_ecom.Controller;


import com.ecommerce.sb_ecom.Configure.AppConstant;
import com.ecommerce.sb_ecom.Model.Product;
import com.ecommerce.sb_ecom.Payload.ProductDTO;
import com.ecommerce.sb_ecom.Payload.ProductResponse;
import com.ecommerce.sb_ecom.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

       @Autowired
       ProductService productService;
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long categoryId){
        ProductDTO savedproductDTO=    productService.addProduct(categoryId,productDTO);
        return new  ResponseEntity<>(savedproductDTO, HttpStatus.CREATED);
    }
    @PostMapping("/seller/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProductSeller(@Valid @RequestBody ProductDTO productDTO,
                                                       @PathVariable Long categoryId){
        ProductDTO savedProductDTO = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false ) Integer pageNumber
            ,@RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE ,required = false) Integer pageSize
            ,@RequestParam(name="SortBy" ,defaultValue = AppConstant.SORT_PRODUCT_BY,required = false) String sortBy
            ,@RequestParam(name="sortOrder" ,defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder
    ){
     ProductResponse productResponse=productService.getAllProducts(pageNumber,pageSize,sortBy,sortOrder);
       return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(
            @PathVariable Long categoryId,
            @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false ) Integer pageNumber
            ,@RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE ,required = false) Integer pageSize
            ,@RequestParam(name="SortBy" ,defaultValue = AppConstant.SORT_PRODUCT_BY,required = false) String sortBy
            ,@RequestParam(name="sortOrder" ,defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder
    ){
        ProductResponse productResponse=  productService.searchByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);

          return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{Keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable String Keyword,
                                                               @RequestParam(name = "pageNumber",defaultValue = AppConstant.PAGE_NUMBER,required = false ) Integer pageNumber
            ,@RequestParam(name="pageSize" ,defaultValue = AppConstant.PAGE_SIZE ,required = false) Integer pageSize
            ,@RequestParam(name="SortBy" ,defaultValue = AppConstant.SORT_PRODUCT_BY,required = false) String sortBy
            ,@RequestParam(name="sortOrder" ,defaultValue = AppConstant.SORT_DIR,required = false) String sortOrder
    ){
      ProductResponse productResponse=  productService.searchByKeyword(Keyword,pageNumber,pageSize,sortBy,sortOrder);
      return new ResponseEntity<>(productResponse,HttpStatus.FOUND);
    }

      @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO>UpdateProduct(@Valid @RequestBody ProductDTO productDTO, @PathVariable Long productId){
      ProductDTO updatedProductDTO=  productService.UpdateProduct(productId,productDTO);
        return new  ResponseEntity<>(updatedProductDTO,HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO>deleteProduct(@PathVariable Long productId){
       ProductDTO deletedProductDTO= productService.DeleteProduct(productId);
          return new ResponseEntity<>(deletedProductDTO,HttpStatus.OK);
    }
    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO>updateProductImage(@PathVariable Long productId,
                                                        @RequestParam("image")MultipartFile image) throws IOException {
       ProductDTO udatedProductImage=  productService.updateProductImage(productId,image);
       return new ResponseEntity<>(udatedProductImage,HttpStatus.OK);
    }
    @GetMapping("/admin/products")
    public ResponseEntity<ProductResponse> getAllProductsForAdmin(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProductsForAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }


    @GetMapping("/seller/products")
    public ResponseEntity<ProductResponse> getAllProductsForSeller(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_DIR, required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getAllProductsForSeller(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @PutMapping("/seller/products/{productId}")
    public ResponseEntity<ProductDTO> updateProductSeller(@Valid @RequestBody ProductDTO productDTO,
                                                          @PathVariable Long productId){
        ProductDTO updatedProductDTO = productService.UpdateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/seller/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProductSeller(@PathVariable Long productId){
        ProductDTO deletedProduct = productService.DeleteProduct(productId);
        return new ResponseEntity<>(deletedProduct, HttpStatus.OK);
    }

    @PutMapping("/seller/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImageSeller(@PathVariable Long productId,
                                                               @RequestParam("image")MultipartFile image) throws IOException {
        ProductDTO updatedProduct = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}

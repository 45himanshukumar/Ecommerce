package com.ecommerce.sb_ecom.Service;


import com.ecommerce.sb_ecom.Exception.APIException;
import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.Cart;
import com.ecommerce.sb_ecom.Model.Category;
import com.ecommerce.sb_ecom.Model.Product;
import com.ecommerce.sb_ecom.Payload.CartDTO;
import com.ecommerce.sb_ecom.Payload.ProductDTO;
import com.ecommerce.sb_ecom.Payload.ProductResponse;
import com.ecommerce.sb_ecom.Repositry.CartRepository;
import com.ecommerce.sb_ecom.Repositry.CategoryRepository;
import com.ecommerce.sb_ecom.Repositry.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceimp implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Autowired
    CartService cartService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addproduct(Long categoryId, ProductDTO productDTO) {

        Category  category= categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourseNotFoundException("Category","categoryId",categoryId));
        //check this product present or not

        boolean isProductNotPresent=true;
        List<Product>products= category.getProducts();
        for (Product value : products) {
            if (value.getProductName().equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if(isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);

            return modelMapper.map(product, ProductDTO.class);
        }
        else {
            throw new APIException("Product already existing ");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findAll(pageDetails);

         List<Product> products= pageProducts.getContent();
             List<ProductDTO> productDTOS=   products.stream()
                     .map(product -> modelMapper.map(product,ProductDTO.class))
                     .toList();
             if(products.isEmpty()){
                 throw  new APIException("Product not present");
             }
             ProductResponse productResponse= new ProductResponse();
             productResponse.setContent(productDTOS);
             productResponse.setPageNumber(pageProducts.getNumber());
             productResponse.setPageSize(pageProducts.getSize());
             productResponse.setTotalPage(pageProducts.getTotalPages());
             productResponse.setTotalElement(pageProducts.getTotalPages());
             productResponse.setLastPage(pageProducts.isLast());

        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Category  category= categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourseNotFoundException("Category","categoryId",categoryId));
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findByCategoryOrderByPriceAsc(category,pageDetails);

        List<Product> products= pageProducts.getContent();
         //List<Product> products=  productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS=   products.stream()
                .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        if(products.isEmpty()){
            throw new APIException(category.getCategoryName()+"Product not Found this categoy");

        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPage(pageProducts.getTotalPages());
        productResponse.setTotalElement(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByKeyword(String Keyword,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
       // List<Product> products=  productRepository.findByProductNameLikeIgnoreCase('%'+Keyword+'%');
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails= PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> pageProducts=productRepository.findByProductNameLikeIgnoreCase('%'+Keyword+'%',pageDetails);
        List<Product> products= pageProducts.getContent();
        List<ProductDTO> productDTOS=   products.stream()
               .map(product -> modelMapper.map(product,ProductDTO.class))
                .toList();
        if(products.isEmpty()){
            throw  new APIException("Product not Found with keyword"+ Keyword);
        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPage(pageProducts.getTotalPages());
        productResponse.setTotalElement(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;

    }

    @Override
    public ProductDTO UpdateProduct(Long productId, ProductDTO productDTO) {
        Product productFromDb= productRepository.findById(productId)
                .orElseThrow(()-> new ResourseNotFoundException("Product","productId",productId));
       Product product= modelMapper.map(productDTO,Product.class);
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setPrice(product.getPrice());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setSpecialPrice(product.getSpecialPrice());

       Product savedProduct=  productRepository.save(productFromDb);

          List<Cart> carts= cartRepository.findCartByproductId(productId);

          List<CartDTO> cartDTOS=carts.stream().map(cart -> {
              CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
              List<ProductDTO> products=cart.getCartItems().stream()
                      .map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                      .toList();
              cartDTO.setProducts(products);
              return cartDTO;
          }).collect(Collectors.toList());

          cartDTOS.forEach(cart-> cartService.updateProductInCarts(cart.getCartId(),productId));


        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO DeleteProduct(Long productId) {
        Product product= productRepository.findById(productId)
                .orElseThrow(()-> new ResourseNotFoundException("Product","productId",productId));

        List<Cart> carts= cartRepository.findCartByproductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(),productId)
        );

       productRepository.delete(product);
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        //get the product from db
        Product productFromDb= productRepository.findById(productId)
                .orElseThrow(()-> new ResourseNotFoundException("Product","productId",productId));
        //upload the image to server
        // get the file name of upload image
         //String path= "images/";
        String filename=fileService.uploadImage(path,image);

        //update the new file name to the product
            productFromDb.setImage(filename);
        //save the updated image
         Product updatedProduct= productRepository.save(productFromDb);
        //return the dto after the mapping product to dto
        return  modelMapper.map(updatedProduct,ProductDTO.class);

    }




}

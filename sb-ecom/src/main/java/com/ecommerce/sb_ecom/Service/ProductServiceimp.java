package com.ecommerce.sb_ecom.Service;


import com.ecommerce.sb_ecom.Configure.AppConstant;
import com.ecommerce.sb_ecom.Exception.APIException;
import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.Cart;
import com.ecommerce.sb_ecom.Model.Category;
import com.ecommerce.sb_ecom.Model.Product;
import com.ecommerce.sb_ecom.Model.User;
import com.ecommerce.sb_ecom.Payload.CartDTO;
import com.ecommerce.sb_ecom.Payload.ProductDTO;
import com.ecommerce.sb_ecom.Payload.ProductResponse;
import com.ecommerce.sb_ecom.Repositry.CartRepository;
import com.ecommerce.sb_ecom.Repositry.CategoryRepository;
import com.ecommerce.sb_ecom.Repositry.ProductRepository;
import com.ecommerce.sb_ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Autowired
    AuthUtil authUtil;


    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {

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
        //update
        List<Product> products= pageProducts.getContent();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO dto = modelMapper.map(product, ProductDTO.class);
                    dto.setImage(constructImageUrl(product.getImage()));
                    return dto;
                })
                .toList();
        //
             if(products.isEmpty()){
                 throw  new APIException("Product not present");
             }
             ProductResponse productResponse= new ProductResponse();
             productResponse.setContent(productDTOS);
             productResponse.setPageNumber(pageProducts.getNumber());
             productResponse.setPageSize(pageProducts.getSize());
             productResponse.setTotalPages(pageProducts.getTotalPages());
             productResponse.setTotalElements((long) pageProducts.getTotalPages());
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
        //update
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO dto = modelMapper.map(product, ProductDTO.class);
                    dto.setImage(constructImageUrl(product.getImage()));
                    return dto;
                })
                .toList();
        //
        if(products.isEmpty()){
            throw new APIException(category.getCategoryName()+"Product not Found this categoy");

        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setTotalElements((long) pageProducts.getTotalPages());
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
        //update
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO dto = modelMapper.map(product, ProductDTO.class);
                    dto.setImage(constructImageUrl(product.getImage()));
                    return dto;
                })
                .toList();
        //
        if(products.isEmpty()){
            throw  new APIException("Product not Found with keyword"+ Keyword);
        }
        ProductResponse productResponse= new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setTotalElements((long) pageProducts.getTotalPages());
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
    private String constructImageUrl(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }
    @Override
    public ProductResponse getAllProductsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);

        List<Product> products = pageProducts.getContent();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setImage(constructImageUrl(product.getImage()));
                    return productDTO;
                })
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsForSeller(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        User user = authUtil.loggedInUser();
        Page<Product> pageProducts = productRepository.findByUser(user, pageDetails);

        List<Product> products = pageProducts.getContent();

        List<ProductDTO> productDTOS = products.stream()
                .map(product -> {
                    ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
                    productDTO.setImage(constructImageUrl(product.getImage()));
                    return productDTO;
                })
                .toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }



}

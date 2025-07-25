package com.ecommerce.sb_ecom.Controller;

import com.ecommerce.sb_ecom.Model.Cart;
import com.ecommerce.sb_ecom.Payload.CartDTO;
import com.ecommerce.sb_ecom.Repositry.CartRepository;
import com.ecommerce.sb_ecom.Service.CartService;
import com.ecommerce.sb_ecom.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartRepository cartRepository;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
         CartDTO cartDTO= cartService.addProductToCart(productId,quantity);
         return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>>getcarts(){
        List<CartDTO> cartDTOS= cartService.getAllCarts();
        return new ResponseEntity<List<CartDTO>>(cartDTOS,HttpStatus.OK);
    }
    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO>getCartById(){
        String emailId=authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(emailId);
        Long cartId=cart.getCartId();
        CartDTO cartDTO= cartService.getCart(emailId,cartId);
        return new  ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }
    @PutMapping("/cart/product/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO>updateCartProduct(@PathVariable Long productId,@PathVariable String operation){
          CartDTO cartDTO= cartService.updateProductQuantityCart(productId,operation.equalsIgnoreCase("delete")? -1:1);
          return new ResponseEntity<CartDTO>(cartDTO,HttpStatus.OK);
    }
    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public ResponseEntity<String>deleteProductFromCart(@PathVariable Long cartId,@PathVariable Long productId){
        String status  =cartService.deleteProductFromCart(cartId,productId);
         return new ResponseEntity<String>(status,HttpStatus.OK);
    }
}

package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Payload.CartDTO;

import java.util.List;

public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);
}

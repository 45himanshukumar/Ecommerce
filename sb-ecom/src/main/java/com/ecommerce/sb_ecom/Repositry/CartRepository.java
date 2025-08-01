package com.ecommerce.sb_ecom.Repositry;

import com.ecommerce.sb_ecom.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("SELECT c FROM Cart c WHERE c.user.email=?1 AND c.id=?2")
    Cart findCartByEmailAndCartByCartId(String emailId, Long cartId);

    @Query("SELECT c FROM Cart c JOIN FETCH c.cartItems ci JOIN FETCH ci.product p WHERE p.id=?1")
    List<Cart> findCartByproductId(Long productId);
}

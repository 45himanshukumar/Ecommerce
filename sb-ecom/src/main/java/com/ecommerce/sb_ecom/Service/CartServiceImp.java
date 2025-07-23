package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Exception.APIException;
import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.Cart;
import com.ecommerce.sb_ecom.Model.CartItem;
import com.ecommerce.sb_ecom.Model.Product;
import com.ecommerce.sb_ecom.Payload.CartDTO;
import com.ecommerce.sb_ecom.Payload.ProductDTO;
import com.ecommerce.sb_ecom.Repositry.CartItemRepository;
import com.ecommerce.sb_ecom.Repositry.CartRepository;
import com.ecommerce.sb_ecom.Repositry.ProductRepository;
import com.ecommerce.sb_ecom.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class CartServiceImp implements CartService {

    @Autowired
   private CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private AuthUtil authUtil;
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //find existing cart or created one
         Cart cart= createCart();
        //retrieve product details;
        Product product= productRepository.findById(productId)
                .orElseThrow(()-> new ResourseNotFoundException("product","productId",productId));
        //perform validation
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }
        //create cart item
        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        //save cart item
        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));

        cartRepository.save(cart);

        //return updated cart
        CartDTO cartDTO=modelMapper.map(cart ,CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });

        cartDTO.setProducts(productStream.toList());

        return cartDTO;


    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts= cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No Cart Exist");
        }
        List<CartDTO> cartDTOS=carts.stream()
                .map(cart->{
                    CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
                    List<ProductDTO>products=cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                            .collect(toList());
                     cartDTO.setProducts(products);
                     return cartDTO;
                }).collect(toList());
        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart= cartRepository.findCartByEmailAndCartByCartId(emailId,cartId);
        if(cart==null){
            throw new ResourseNotFoundException("Cart","cartId",cartId);
        }
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO>products=cart.getCartItems().stream().map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    public Cart createCart(){
        Cart userCart= cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }
        Cart cart= new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart  newcart=cartRepository.save(cart);
        return newcart;
    }
}

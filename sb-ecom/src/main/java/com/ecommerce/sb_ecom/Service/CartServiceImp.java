package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Exception.APIException;
import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.Cart;
import com.ecommerce.sb_ecom.Model.CartItem;
import com.ecommerce.sb_ecom.Model.Product;
import com.ecommerce.sb_ecom.Payload.CartDTO;
import com.ecommerce.sb_ecom.Payload.CartItemDTO;
import com.ecommerce.sb_ecom.Payload.ProductDTO;
import com.ecommerce.sb_ecom.Repositry.CartItemRepository;
import com.ecommerce.sb_ecom.Repositry.CartRepository;
import com.ecommerce.sb_ecom.Repositry.ProductRepository;
import com.ecommerce.sb_ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
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
                .map(cart-> {
                            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                                productDTO.setQuantity(cartItem.getQuantity());
                                return productDTO;
                            }).collect(Collectors.toList());

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

    @Transactional
    @Override
    public CartDTO updateProductQuantityCart(Long productId, Integer quantity) {
         String emailId=authUtil.loggedInEmail();
         Cart userCart= cartRepository.findCartByEmail(emailId);
         Long cartId=userCart.getCartId();
        Cart cart =cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourseNotFoundException("cart","cartId",cartId));

        Product product= productRepository.findById(productId)
                .orElseThrow(()-> new ResourseNotFoundException("product","productId",productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }
        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("product"+ product.getProductName()+" not availble in the cart");
        }
          // new quantity
        int newQuantity= cartItem.getQuantity()+quantity;

        if(newQuantity<0){
            throw new APIException("The resulting quantity cannot be negative");
        }
        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItem updatedItem= cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity()==0){
            cartRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO= modelMapper.map(cart , CartDTO.class);
        List<CartItem>cartItems=cart.getCartItems();

        Stream<ProductDTO>productStream=cartItems.stream().map(item->{
            ProductDTO productDTO=modelMapper.map(item.getProduct(),ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        });
        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
         Cart cart= cartRepository.findById(cartId)
                 .orElseThrow(()-> new ResourseNotFoundException("cart","cartId",cartId));

        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

          if(cartItem==null){
              throw new ResourseNotFoundException("Product","productId",productId);
          }
          cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
          cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);
        return  "product"+cartItem.getProduct().getProductName()+"removed froom the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

        Cart cart =cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourseNotFoundException("cart","cartId",cartId));

        Product product= productRepository.findById(productId)
                .orElseThrow(()-> new ResourseNotFoundException("product","productId",productId));

        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw  new APIException("product " + product.getProductName() + " not available in the cart!!!");
        }
        //1000-100*2
        double cartPrice= cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());
        //200
        cartItem.setProductPrice(product.getSpecialPrice());

        //800+200*2
        cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItem=cartItemRepository.save(cartItem);
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

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get user's email
        String emailId = authUtil.loggedInEmail();

        // Check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if (existingCart == null) {
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        } else {
            // Clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        // Process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // Find the product by ID
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourseNotFoundException("Product", "productId", productId));

            // Directly update product stock and total price
            // product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        // Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with the new items successfully";
    }


}

package com.ecommerce.sb_ecom.Service;

import com.ecommerce.sb_ecom.Exception.APIException;
import com.ecommerce.sb_ecom.Exception.ResourseNotFoundException;
import com.ecommerce.sb_ecom.Model.*;
import com.ecommerce.sb_ecom.Payload.OrderDTO;
import com.ecommerce.sb_ecom.Payload.OrderItemDTO;
import com.ecommerce.sb_ecom.Repositry.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImp implements OrderService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ModelMapper modelMapper;
    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
       //get the user cart
        Cart cart= cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw new ResourseNotFoundException("Cart","email",emailId);
        }
        Address address= addressRepository.findById(addressId)
                .orElseThrow(()->new ResourseNotFoundException("Address","addressId",addressId));

        //create  a new order with payment info
        Order order= new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);

        Payment payment= new Payment(paymentMethod,pgPaymentId,pgStatus,pgName,pgResponseMessage);
        payment.setOrder(order);
        payment=paymentRepository.save(payment);
        order.setPayment(payment);
        Order savedOrder=orderRepository.save(order);

        //get items from the cart into the order items
        List<CartItem> cartItems=cart.getCartItems();
        if(cartItems.isEmpty()){
            throw new APIException("Cart is Empty");
        }
        List<OrderItem>orderItems= new ArrayList<>();
        for(CartItem cartItem:cartItems){
          OrderItem  orderItem= new OrderItem();
          orderItem.setProduct(cartItem.getProduct());
          orderItem.setQuantity(cartItem.getQuantity());
          orderItem.setDiscount(cartItem.getDiscount());
          orderItem.setOrderProductPrice(cartItem.getProductPrice());
          orderItem.setOrder(savedOrder);
          orderItems.add(orderItem);
        }
        orderItems=orderItemRepository.saveAll(orderItems);


        //update product stock
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();

            // Reduce stock quantity
            product.setQuantity(product.getQuantity() - quantity);

            // Save product back to the database
            productRepository.save(product);

            // Remove items from cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems().add(modelMapper.map(item, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        //clear the cart

        //send back the order summary
        return orderDTO;
    }
}

package com.ecommerce.sb_ecom.Controller;


import com.ecommerce.sb_ecom.Payload.OrderDTO;
import com.ecommerce.sb_ecom.Payload.OrderRequestDTO;
import com.ecommerce.sb_ecom.Service.OrderService;
import com.ecommerce.sb_ecom.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
  private AuthUtil authUtil;
    @Autowired
    private OrderService orderService;

    @PostMapping("/order/users/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO>orderProduct(@PathVariable String paymentMethod,
                                                @RequestBody OrderRequestDTO orderRequestDTO){
        String emailId=authUtil.loggedInEmail();
        OrderDTO orderDTO= orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}

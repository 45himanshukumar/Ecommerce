package com.ecommerce.sb_ecom.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long addressId;
    private String paymentMehtod;
    private String pgName;
    private String pgPaymentId;
    private String pgStatus;
    private  String pgResponseMessage;
}

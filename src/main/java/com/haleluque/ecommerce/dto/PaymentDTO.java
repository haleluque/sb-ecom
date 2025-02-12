package com.haleluque.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String paymentMethod;
    private String pgPaymentId; //reference id from payment gateway, paypal for example
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;
}

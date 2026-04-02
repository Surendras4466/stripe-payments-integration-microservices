package com.myproject.payments.service.interfaces;

import com.myproject.payments.pojo.PaymentRequest;
import com.myproject.payments.pojo.PaymentResponse;

public interface PaymentService {
    
	PaymentResponse validateAndCreatePayment(
    		PaymentRequest paymentRequest);
}

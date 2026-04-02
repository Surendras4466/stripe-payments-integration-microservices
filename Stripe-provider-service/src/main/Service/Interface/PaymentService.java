package com.myproject.payments.service.interfaces;

import com.myproject.payments.pojo.CreatePaymentReq;
import com.myproject.payments.pojo.PaymentResponse;

public interface PaymentService {
	
	public PaymentResponse createPayment(CreatePaymentReq createPaymentReq);

}

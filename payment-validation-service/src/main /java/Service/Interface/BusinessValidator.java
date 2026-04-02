package com.myproject.payments.service.interfaces;

import com.myproject.payments.pojo.PaymentRequest;

public interface BusinessValidator {
	
	public void validate(
			PaymentRequest paymentRequest);

}

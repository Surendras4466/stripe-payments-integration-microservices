package com.myproject.payments.service.interfaces;

public interface StripeNotificationService {

	  void processNotification(String stripeSignature, String jsonRequest);
	
}

package com.myproject.payments.repository.interfaces;

import com.myproject.payments.Entity.MerchantPaymentRequestEntity;

public interface MerchantPaymentRequestRepository {
	
	public int saveMerchantPaymentRequest(MerchantPaymentRequestEntity 
			merchantPaymentRequestEntity);
	
	public int countRequestsForUserInLastMinutes(
			String endUserId, int minutes);

}

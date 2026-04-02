package com.myproject.payments.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.myproject.payments.cache.ValidatorRuleCacheRedis;
import com.myproject.payments.cache.ValidatorRuleCacheV2;
import com.myproject.payments.constant.ErrorCodeEnum;
import com.myproject.payments.constant.ValidatorRuleEnum;
import com.myproject.payments.exception.PaymentValidationException;
import com.myproject.payments.http.HttpRequest;
import com.myproject.payments.http.HttpServiceEngine;
import com.myproject.payments.pojo.PaymentRequest;
import com.myproject.payments.pojo.PaymentResponse;
import com.myproject.payments.service.helper.StripeProviderHelper;
import com.myproject.payments.service.interfaces.BusinessValidator;
import com.myproject.payments.service.interfaces.PaymentService;
import com.myproject.payments.stripeprovider.SPPaymentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final ApplicationContext applicationContext;

	private final ValidatorRuleCacheV2 validatorRuleCache;
	
	private final StripeProviderHelper stripeProviderHelper;
	
	private final HttpServiceEngine httpServiceEngine;

	@Override
	public PaymentResponse validateAndCreatePayment(
			PaymentRequest paymentRequest) {
		log.info("Validating and creating payment: {} ",
				paymentRequest);

		List<String> validatorRules = validatorRuleCache.getValidatorRules();
		log.debug("Loaded validator rules from cache: {}", validatorRules);
		
		// iterate configured validatorRules (loaded in init)
		if (validatorRules == null || validatorRules.isEmpty()) {
			log.error("No validator rules configured, skipping validations");
			throw new PaymentValidationException(
					ErrorCodeEnum.NO_VALIDATION_RULES_CONFIGURED.getErrorCode(),
					ErrorCodeEnum.NO_VALIDATION_RULES_CONFIGURED.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		for (String rule : validatorRules) {
			log.info("Applying validation rule: {}", rule);

			Optional<Class<? extends BusinessValidator>> validatorClass = ValidatorRuleEnum.getValidatorClassByRule(rule.trim());
			if(!validatorClass.isPresent()) {
				log.warn("No validator found for rule: {}", rule);
				continue;
			}

			// load the validator bean from application context
			BusinessValidator validator = applicationContext.getBean(
					validatorClass.get());

			if(validator == null) {
				log.warn("No bean found for validator class: {}", 
						validatorClass.get().getName());
				continue;
			}

			// call the validate method of the validator
			validator.validate(paymentRequest);
		}

		log.info("All validations passed for payment request: {}", 
				paymentRequest);

		// Code to invoke processing-service
		
		HttpRequest httpRequest = stripeProviderHelper.createHttpRequest(paymentRequest);
		log.info("Prepared HttpRequest for Stripe provider: {}", httpRequest);
		
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		
		SPPaymentResponse finalResponse = stripeProviderHelper.processResponse(httpResponse);
		PaymentResponse paymentResponse = new PaymentResponse();
		paymentResponse.setHostedPageUrl(finalResponse.getHostedPageUrl());
		
		log.info("Final PaymentResponse to be returned: {}", paymentResponse);
		return paymentResponse;
	}
}
package com.demo.fraudverifier.card;

import java.math.BigDecimal;
import java.util.UUID;

import com.demo.fraudverifier.VerificationResult;

import org.springframework.stereotype.Service;

@Service
class CardApplicationVerificationService {

	private static final BigDecimal LIMIT = new BigDecimal("9000");

	VerificationResult verify(UUID uuid, BigDecimal cardCapacity) {
		if (isOutOfRange(cardCapacity)) {
			return VerificationResult.failed(uuid);
		}
		return VerificationResult.passed(uuid);
	}

	private boolean isOutOfRange(BigDecimal cardCapacity) {
		return cardCapacity.compareTo(LIMIT) > 0;
	}
}

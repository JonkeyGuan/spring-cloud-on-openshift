package com.demo.fraudverifier.card;

import java.math.BigDecimal;
import java.util.UUID;

import com.demo.fraudverifier.VerificationResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
class CardApplicationVerificationController {

	private final CardApplicationVerificationService cardApplicationVerificationService;

	CardApplicationVerificationController(CardApplicationVerificationService cardApplicationVerificationService) {
		this.cardApplicationVerificationService = cardApplicationVerificationService;
	}

	@GetMapping("/verify")
	ResponseEntity<VerificationResult> verify(@RequestParam UUID uuid, @RequestParam BigDecimal cardCapacity) {
		VerificationResult result = cardApplicationVerificationService.verify(uuid, cardCapacity);
		return ResponseEntity.ok(result);
	}
}

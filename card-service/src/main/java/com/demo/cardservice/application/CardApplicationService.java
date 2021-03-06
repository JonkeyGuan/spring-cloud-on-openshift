package com.demo.cardservice.application;

import java.util.UUID;

import com.demo.cardservice.user.User;
import com.demo.cardservice.user.UserServiceClient;
import com.demo.cardservice.verification.VerificationApplication;
import com.demo.cardservice.verification.VerificationResult;
import com.demo.cardservice.verification.VerificationServiceClient;

import org.springframework.stereotype.Service;

@Service
class CardApplicationService {

	private final UserServiceClient userServiceClient;
	private final VerificationServiceClient verificationServiceClient;

	public CardApplicationService(UserServiceClient userServiceClient,
			VerificationServiceClient verificationServiceClient) {
		this.userServiceClient = userServiceClient;
		this.verificationServiceClient = verificationServiceClient;
	}

	public CardApplication registerApplication(CardApplicationDto applicationDTO) {
		User user = userServiceClient.registerUser(applicationDTO.user).getBody();
		CardApplication application = new CardApplication(UUID.randomUUID(), user, applicationDTO.cardCapacity);
		if (User.Status.OK != user.getStatus()) {
			application.setApplicationResult(ApplicationResult.rejected());
			return application;
		}
		VerificationResult verificationResult = verificationServiceClient
				.verify(new VerificationApplication(application.getUuid(), application.getCardCapacity())).getBody();
		if (!VerificationResult.Status.VERIFICATION_PASSED.equals(verificationResult.status)) {
			application.setApplicationResult(ApplicationResult.rejected());
		} else {
			application.setApplicationResult(ApplicationResult.granted());
		}
		return application;
	}
}

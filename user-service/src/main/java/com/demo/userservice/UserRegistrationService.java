package com.demo.userservice;

import static com.demo.userservice.VerificationResult.Status.VERIFICATION_PASSED;

import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

	private final VerificationServiceClient verificationServiceClient;

	UserRegistrationService(VerificationServiceClient verificationServiceClient) {
		this.verificationServiceClient = verificationServiceClient;
	}

	User registerUser(UserDto userDto) {
		User user = new User(userDto);
		verifyUser(user);
		return user;
	}

	private void verifyUser(User user) {
		VerificationResult verificationResult = verificationServiceClient.verifyNewUser(user.getUuid(), user.getAge());
		user.setStatus(VERIFICATION_PASSED.equals(verificationResult.getStatus()) ? User.Status.OK : User.Status.FRAUD);
	}
}

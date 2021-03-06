package com.demo.userservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/registration")
public class UserRegistrationController {

	private final UserRegistrationService userRegistrationService;

	UserRegistrationController(UserRegistrationService userRegistrationService) {
		this.userRegistrationService = userRegistrationService;
	}

	@PostMapping
	ResponseEntity<User> registerUser(@RequestBody UserDto userDto, UriComponentsBuilder uriComponentsBuilder) {
		User user = userRegistrationService.registerUser(userDto);
		UriComponents uriComponents = uriComponentsBuilder.path("/users/{uuid}").buildAndExpand(user.getUuid());
		return ResponseEntity.created(uriComponents.toUri()).body(user);
	}
}

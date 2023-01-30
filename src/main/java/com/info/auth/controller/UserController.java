package com.info.auth.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.info.auth.entity.Role;
import com.info.auth.entity.User;
import com.info.auth.exception.ApplicationException;
import com.info.auth.model.RestResponse;
import com.info.auth.model.UserParams;
import com.info.auth.service.CustomUserDetailsService;
import com.info.auth.util.LoggedUser;
import com.info.auth.util.RestHelper;

@RestController
@RequestMapping("/rest/")
public class UserController {

	@Autowired
	private LoggedUser loggedUser;

	@Autowired
	private CustomUserDetailsService userDetailService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("roles")
	public ResponseEntity<RestResponse> getRoles() {
		List<Role> roles = userDetailService.getAllRoles();
		return RestHelper.responseSuccess("roles", roles);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("users")
	public ResponseEntity<RestResponse> getUsers() {
		List<User> users = userDetailService.getAllUsers();
		return RestHelper.responseSuccess("users", users);
	}

	@GetMapping("user/{id}")
	public ResponseEntity<RestResponse> getUser(@PathVariable Long id) {
		User user = loggedUser.getUser();
		return RestHelper.responseSuccess("userDetails", user);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("user")
	public ResponseEntity<RestResponse> saveUser(@RequestBody UserParams params) {
		Optional<Role> role = userDetailService.findRoleById(params.getRoleId());
		if (!role.isPresent()) {
			throw new ApplicationException("Invalid Role");
		}
		if (!params.getPassword().equals(params.getConfirmPassword())) {
			throw new ApplicationException("Password and Confirm Password do not match.");
		}
		User user = new User();
		user.setName(params.getName());
		user.setUsername(params.getUsername());
		user.setCreationDate(Calendar.getInstance());
		user.setPassword(passwordEncoder.encode(params.getPassword()));
		user.setRole(role.get());
		userDetailService.saveUser(user);
		return RestHelper.responseMessage("User registered successfully.", HttpStatus.OK);
	}

	@PostMapping("user/changePassword")
	public ResponseEntity<RestResponse> changePassword(@RequestBody final UserParams params) {
		User user = loggedUser.getUser();
		String oldPassword = params.getOldPassword();
		String newPassword = params.getNewPassword();
		if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
			return RestHelper.responseError("Your old password does not match. Please try again.", HttpStatus.BAD_REQUEST);
		}
		if (!params.getNewPassword().equals(params.getConfirmPassword())) {
			throw new ApplicationException("Password and Confirm Password do not match.");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userDetailService.saveUser(user);
		return RestHelper.responseMessage("Your password changed successfully.", HttpStatus.OK);
	}

}

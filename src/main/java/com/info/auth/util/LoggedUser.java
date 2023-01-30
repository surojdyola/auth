package com.info.auth.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.info.auth.entity.User;
import com.info.auth.service.CustomUserDetailsService;

@Component
public class LoggedUser {
	
	@Autowired
	private CustomUserDetailsService userDetails;
	
	public User getUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userDetails.findUserByUsername(authentication.getName()).get();
	}

}

package com.info.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.info.auth.exception.ApplicationException;
import com.info.auth.model.CustomRestAuthenticationToken;
import com.info.auth.model.CustomUserDetails;
import com.info.auth.service.CustomUserDetailsService;

@Component
public class RestAuthenticationProvider implements AuthenticationProvider{
	
	@Autowired
	private CustomUserDetailsService userDetailService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = String.valueOf(authentication.getCredentials());
		CustomUserDetails userDetails = userDetailService.loadUserByUsername(username);

		try {
			if (!passwordEncoder.matches(password, userDetails.getPassword())) {
				throw new BadCredentialsException("Invalid username or password.");
			}
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
		// Everything Ok - remove any previous wrong attempts
		return new CustomRestAuthenticationToken(username, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return CustomRestAuthenticationToken.class.equals(authentication);
	}

}

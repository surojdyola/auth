package com.info.auth.model;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomRestAuthenticationToken extends UsernamePasswordAuthenticationToken{

	private static final long serialVersionUID = -5225515269931657314L;

	public CustomRestAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}
	
	public CustomRestAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
	}

}

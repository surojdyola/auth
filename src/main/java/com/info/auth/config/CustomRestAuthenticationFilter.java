package com.info.auth.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.info.auth.model.CustomRestAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRestAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtTokenUtil jwtTokenUtil;

	public CustomRestAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		log.info(username + " is request from IP: " + request.getRemoteAddr());
		CustomRestAuthenticationToken restAuthenticationToken = new CustomRestAuthenticationToken(username, password);
		return authenticationManager.authenticate(restAuthenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		log.info("Rest Authentication Successful!");
		String username = (String) authentication.getPrincipal();
		Map<String, String> tokens = new HashMap<>();
		String accessToken = jwtTokenUtil.generateAccessToken(username);
		String refreshToken = jwtTokenUtil.generateRefreshToken(username);
		tokens.put("access_token", accessToken);
		tokens.put("refresh_token", refreshToken);
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

}

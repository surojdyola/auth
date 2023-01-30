package com.info.auth.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.info.auth.model.CustomUserDetails;
import com.info.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private CustomUserDetailsService userService;

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey("]q-[8x<dKzM+.FLDJq'ZN$!_FD").parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate Access token for user
	public String generateAccessToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		String accessToken = doGenerateToken(claims, username, 10);
		userService.updateAccessToken(accessToken, username);
		return accessToken;
	}

	// generate Refresh token for user
	public String generateRefreshToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		String refreshToken = doGenerateToken(claims, username, 30);
		userService.updateRefreshToken(refreshToken, username);
		return refreshToken;
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject, int tokenValidity) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, "]q-[8x<dKzM+.FLDJq'ZN$!_FD").compact();
	}

	// validate token
	public Boolean validateToken(String token, CustomUserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)
				&& token.equals(userDetails.getUser().getAccessToken()));
	}

	// validate token
	public Boolean validateRefreshToken(String token, CustomUserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)
				&& token.equals(userDetails.getUser().getRefreshToken()));
	}
}

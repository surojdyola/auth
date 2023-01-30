package com.info.auth.config;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.info.auth.entity.User;
import com.info.auth.exception.ApplicationException;
import com.info.auth.model.CustomUserDetails;
import com.info.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter{

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getServletPath().equals("/rest/access/login")
				|| request.getServletPath().equals("/rest/access/token/refresh")) {
			filterChain.doFilter(request, response);
		} else {
			final String requestTokenHeader = request.getHeader("ACCESS-TOKEN");
			String username = null;
			String jwtToken = null;
			if (requestTokenHeader != null) {
				jwtToken = requestTokenHeader;
				try {
					username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				} catch (IllegalArgumentException e) {
					log.error("Token Invalid");
				} catch (ExpiredJwtException e) {
					User user = userDetailsService.findByAccessToken(jwtToken);
					if (Objects.nonNull(user)) {
						user.setAccessToken(null);
						user.setRefreshToken(null);
						userDetailsService.saveUser(user);
					}
					throw new ApplicationException("Your access token has expired, please login or use refresh token.");
				}
			} else {
				throw new ApplicationException("Your access token has expired, please login or use refresh token.");
			}
			// Once we get the token validate it.
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
				// if token is valid configure Spring Security to manually set
				// authentication
				if (!jwtTokenUtil.validateToken(jwtToken, userDetails)) {
					throw new ApplicationException("Token Invalid.");
				}
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// After setting the Authentication in the context, we specify
				// that the current user is authenticated. So it passes the
				// Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
			filterChain.doFilter(request, response);
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return !Objects.nonNull(request.getHeader("ACCESS-TOKEN"));
	}
}

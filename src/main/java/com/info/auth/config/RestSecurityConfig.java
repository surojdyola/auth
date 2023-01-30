package com.info.auth.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

@Order(1)
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class RestSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private RestAuthenticationProvider restAuthenticationProvider;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> response.sendError(401, authException.getMessage());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(restAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomRestAuthenticationFilter restAuthenticationFilter = new CustomRestAuthenticationFilter(
				authenticationManagerBean(), jwtTokenUtil);
		restAuthenticationFilter.setFilterProcessesUrl("/rest/access/login");

		http.cors().and().csrf().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);

		http.headers().defaultsDisabled().frameOptions().sameOrigin().httpStrictTransportSecurity()
				.includeSubDomains(true).maxAgeInSeconds(31536000).and().xssProtection().block(false).and()
				.contentSecurityPolicy(
						"default-src 'self' 'unsafe-inline' 'unsafe-eval'; object-src 'self'; script-src 'self' 'unsafe-eval' 'unsafe-inline'; media-src *;font-src 'self' data:; img-src 'self' data:;")
				.and().referrerPolicy(ReferrerPolicy.SAME_ORIGIN).and().cacheControl().and().contentTypeOptions();

		http.antMatcher("/rest/**").authorizeRequests().antMatchers("/rest/access/login").permitAll()
				.antMatchers("/rest/access/token/refresh").permitAll().anyRequest().authenticated().and()
				.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint());

	}

}

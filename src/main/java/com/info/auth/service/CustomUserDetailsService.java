package com.info.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.info.auth.entity.Role;
import com.info.auth.entity.User;
import com.info.auth.exception.ApplicationException;
import com.info.auth.model.CustomUserDetails;
import com.info.auth.repo.RoleRepository;
import com.info.auth.repo.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;
	
	@Override
	public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepo.findByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new ApplicationException("Invalid Username or Password");
		}
		return new CustomUserDetails(optionalUser.get());
	}
	
	public User saveUser(User user) {
		return userRepo.save(user);
	}
	
	public void updateAccessToken(String accessToken, String username) {
		userRepo.updateUserAccessToken(accessToken, username);
	}

	public void updateRefreshToken(String refreshToken, String username) {
		userRepo.updateUserRefreshToken(refreshToken, username);
	}
	
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}
	
	public List<Role> getAllRoles() {
		return roleRepo.findAll();
	}

	public Optional<Role> findRoleById(Long id) {
		return roleRepo.findById(id);
	}

	public Optional<User> findUserByUsername(String username) {
		return userRepo.findByUsername(username);
	}
	
	public User findUserById(Long id) {
		return userRepo.findById(id).get();
	}
	
	public User findByAccessToken(String accessToken) {
		return userRepo.findByAccessToken(accessToken);
	}
}

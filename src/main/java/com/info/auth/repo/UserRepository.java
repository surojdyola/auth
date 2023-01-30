package com.info.auth.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.info.auth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	@Query("Select u from User u where u.username=?1")
	Optional<User> findByUsername(String username);
	
	@Query("Select u from User u where u.accessToken = :accessToken")
	User findByAccessToken(String accessToken);
	
	@Modifying
	@Query("update User u set u.accessToken = :accessToken where u.username = :username")
	void updateUserAccessToken(String accessToken, String username);
	 
	@Modifying
	@Query("update User u set u.refreshToken = :refreshToken where u.username = :username")
	void updateUserRefreshToken(String refreshToken, String username);
}

package com.info.auth.entity;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String username;
	
	@JsonIgnore
	private String password;
	
	@Column(name = "creation_date")
	private Calendar creationDate;
	
	@JsonIgnore
	@Column(name = "access_token")
	private String accessToken;
	
	@JsonIgnore
	@Column(name = "refresh_token")
	private String refreshToken;
	
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

}

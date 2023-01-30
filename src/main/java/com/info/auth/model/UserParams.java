package com.info.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserParams {

	private String name;
	private String username;
	private String password;
	private String confirmPassword;
	private Long roleId;
	private String oldPassword;
	private String newPassword;
}

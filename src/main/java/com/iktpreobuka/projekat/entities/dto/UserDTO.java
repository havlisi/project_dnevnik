package com.iktpreobuka.projekat.entities.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserDTO {
	
	@NotNull(message = "First name must be provided.")
	@Size(min = 2, max = 30, message = "First name must be between "
			+ "{min} and {max} characters long.")
	private String firstName;
	
	@NotNull(message = "Last name must be provided.")
	@Size(min = 2, max = 30, message = "Last name must be between "
			+ "{min} and {max} characters long.")
	private String lastName;

	@NotNull(message = "Username must be provided.")
	@Size(min = 5, max = 25, message = "Username must be between "
			+ "{min} and {max} characters long.")
	private String username;
	
	@NotNull(message = "Please provide email address.")
	@Email(message = "Email is not valid.")
	private String email;
	
	@NotNull(message = "Password must be provided.")
	@Size(min = 5, max = 15, message = "Password must be between "
			+ "{min} and {max} characters long.")
	private String password;
	
	@NotNull(message = "Password must be provided.")
	@Size(min = 5, max = 15, message = "Password must be between "
			+ "{min} and {max} characters long.")
	private String changed_password;
	
	@Column(nullable = false)
	private String role;
	
	public UserDTO() {}

	public UserDTO(
			@NotNull(message = "First name must be provided.") @Size(min = 2, max = 30, message = "First name must be between {min} and {max} characters long.") String firstName,
			@NotNull(message = "Last name must be provided.") @Size(min = 2, max = 30, message = "Last name must be between {min} and {max} characters long.") String lastName,
			@NotNull(message = "Username must be provided.") @Size(min = 5, max = 25, message = "Username must be between {min} and {max} characters long.") String username,
			@NotNull(message = "Please provide email address.") @Email(message = "Email is not valid.") String email,
			@NotNull(message = "Password must be provided.") @Size(min = 5, max = 15, message = "Password must be between {min} and {max} characters long.") String password,
			@NotNull(message = "Password must be provided.") @Size(min = 5, max = 15, message = "Password must be between {min} and {max} characters long.") String changed_password,
			String role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.email = email;
		this.password = password;
		this.changed_password = changed_password;
		this.role = role;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getChanged_password() {
		return changed_password;
	}

	public void setChanged_password(String changed_password) {
		this.changed_password = changed_password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
}

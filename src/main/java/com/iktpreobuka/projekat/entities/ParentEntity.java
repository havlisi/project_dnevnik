package com.iktpreobuka.projekat.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class ParentEntity extends UserEntity {
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	private List<StudentEntity> student = new ArrayList<>();
	
	public ParentEntity() {}

	public ParentEntity(Integer id, String role, List<StudentEntity> student,
			@NotNull(message = "First name must be provided.") @Size(min = 2, max = 30, message = "First name must be between {min} and {max} characters long.") String firstName,
			@NotNull(message = "Last name must be provided.") @Size(min = 2, max = 30, message = "Last name must be between {min} and {max} characters long.") String lastName,
			@NotNull(message = "Username must be provided.") @Size(min = 5, max = 25, message = "Username must be between {min} and {max} characters long.") String username,
			@NotNull(message = "Please provide email address.") @Email(message = "Email is not valid.") String email,
			@NotNull(message = "Password must be provided.") @Size(min = 5, max = 15, message = "Password must be between {min} and {max} characters long.") String password) {
		super(id, role, firstName, lastName, username, email, password);
		this.student = student;
	}

	public List<StudentEntity> getStudent() {
		return student;
	}

	public void setStudent(List<StudentEntity> student) {
		this.student = student;
	}

}

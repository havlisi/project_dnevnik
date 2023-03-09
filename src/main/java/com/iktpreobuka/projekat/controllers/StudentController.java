package com.iktpreobuka.projekat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonView;
import com.iktpreobuka.projekat.entities.GradeEntity;
import com.iktpreobuka.projekat.entities.ParentEntity;
import com.iktpreobuka.projekat.entities.StudentEntity;
import com.iktpreobuka.projekat.entities.TeacherEntity;
import com.iktpreobuka.projekat.entities.TeacherSubject;
import com.iktpreobuka.projekat.entities.UserEntity;
import com.iktpreobuka.projekat.entities.dto.UserDTO;
import com.iktpreobuka.projekat.repositories.GradeRepository;
import com.iktpreobuka.projekat.repositories.ParentRepository;
import com.iktpreobuka.projekat.repositories.StudentRepository;
import com.iktpreobuka.projekat.repositories.TeacherRepository;
import com.iktpreobuka.projekat.repositories.TeacherSubjectRepository;
import com.iktpreobuka.projekat.repositories.UserRepository;
import com.iktpreobuka.projekat.security.Views;
import com.iktpreobuka.projekat.utils.ErrorMessageHelper;
import com.iktpreobuka.projekat.utils.RESTError;
import com.iktpreobuka.projekat.utils.UserCustomValidator;

@RestController
@RequestMapping(path = "/api/project/student")
public class StudentController {

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private TeacherSubjectRepository teacherSubjectRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private GradeRepository gradeRepository;
	
	@Autowired
	UserCustomValidator userValidator;
	
	@JsonView(Views.Admin.class)
	protected final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
	
	
	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAllStudents() {
		List<StudentEntity> students = (List<StudentEntity>) studentRepository.findAll();

		if (students.isEmpty()) {
	        logger.error("No students found in the database.");
			return new ResponseEntity<RESTError>(new RESTError(1, "No students found"), HttpStatus.NOT_FOUND);
		} else {
	        logger.info("Found student(s) in the database");
			return new ResponseEntity<List<StudentEntity>>(students, HttpStatus.OK);
		}
	}	

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/by-id/{id}")
	public ResponseEntity<?> getStudentsById(@PathVariable Integer id) {
		Optional<StudentEntity> student = studentRepository.findById(id);

		if (student.isPresent()) {
	        logger.info("Student found in the database: " + student.get().getFirstName() + student.get().getLastName() + ".");
			return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
		} else {
	        logger.error("No student found in the database with: " + id + ".");
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/by-username/{username}")
	public ResponseEntity<?> getStudentByUsername(@PathVariable String username) {
		Optional<StudentEntity> student = studentRepository.findByUsername(username);

		if (student.isPresent()) {
	        logger.info("Student found in the database: " + username + ".");
			return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
		} else {
	        logger.error("No student found in the database with " + username + ".");
			return new ResponseEntity<RESTError>(new RESTError(1, "Student not found"), HttpStatus.NOT_FOUND);
		}
	}

	@Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
	@RequestMapping(method = RequestMethod.GET, value = "/by-firstName/{firstName}")
	public ResponseEntity<?> getStudentByFirstName(@PathVariable String firstName, Authentication authentication) {
		String signedInUserEmail = authentication.getName();
		UserEntity currentUser = userRepository.findByEmail(signedInUserEmail);
		
		List<StudentEntity> allStudents = studentRepository.findByFirstName(firstName);
		logger.info("Found students with name - " + firstName + " in the database.");

		if (allStudents.isEmpty()) {
	        logger.error("No students found in the database with name: " + firstName);
			return new ResponseEntity<RESTError>(new RESTError(1, "No students found"), HttpStatus.NOT_FOUND);
		}

		if (currentUser.getRole().equals("ROLE_TEACHER")) {
			TeacherEntity teacher = (TeacherEntity) currentUser;
			
			List<StudentEntity> teachersStudents = new ArrayList<StudentEntity>();
			for (StudentEntity student : allStudents) {
				for (TeacherSubject teacherSubject : student.getTeacherSubjects()) {
					for (TeacherSubject teacherSubject2 : teacher.getTeacherSubject()) {
						if (teacherSubject.getId().equals(teacherSubject2.getId())) {
							teachersStudents.add(student);
						}
					}
				}
			}
			return new ResponseEntity<List<StudentEntity>>(teachersStudents, HttpStatus.OK);

		}
		return new ResponseEntity<List<StudentEntity>>(allStudents, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/by-lastName/{lastName}")
	public ResponseEntity<?> getStudentByLastName(@PathVariable String lastName) {
		List<StudentEntity> students = studentRepository.findByLastName(lastName);

		if (students.isEmpty()) {
	        logger.error("No students found in the database with lastname" + lastName);
			return new ResponseEntity<RESTError>(new RESTError(1, "No students found"), HttpStatus.NOT_FOUND);
		} else {
	        logger.info("Found students with lastname: " + lastName + " in the database .");
			return new ResponseEntity<List<StudentEntity>>(students, HttpStatus.OK);
		}
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/by-firstLetter/{firstLetter}")
	public ResponseEntity<?> getStudentByFirstLetter(@PathVariable String firstLetter) {
		List<StudentEntity> students = studentRepository.findByFirstNameStartingWith(firstLetter);

		if (students.isEmpty()) {
	        logger.error("No students found in the database with first letter of the name " + firstLetter);
			return new ResponseEntity<RESTError>(new RESTError(1, "No students found"), HttpStatus.NOT_FOUND);
		} else {
	        logger.info("Found students in the database with first letter of the name " + firstLetter);
			return new ResponseEntity<List<StudentEntity>>(students, HttpStatus.OK);
		}
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.GET, value = "/by-email/{email}")
	public ResponseEntity<?> getByEmail(@PathVariable String email) {
		Optional<StudentEntity> student = studentRepository.findByEmail(email);

		if (student.isPresent()) {
	        logger.info("Found students in the database with " + student.get().getEmail());
			return new ResponseEntity<StudentEntity>(student.get(), HttpStatus.OK);
		} else {
	        logger.error("No student found in the database with " + email);
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.POST, value = "/newStudentUser")
	public ResponseEntity<?> createStudent(@Valid @RequestBody UserDTO newUser, BindingResult result) {
	
		if (result.hasErrors()) {
	        logger.error("Sent incorrect parameters.");
			return new ResponseEntity<>(ErrorMessageHelper.createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} else {
	        logger.info("Validating if the users password matches the confirming password");
			userValidator.validate(newUser, result);
		}
		
		UserEntity existingUserWithEmail = userRepository.findByEmail(newUser.getEmail());
        logger.info("Finding out whether there's a user with the same email.");

		if (existingUserWithEmail != null) {
	        logger.error("There is a user with the same email.");
			return new ResponseEntity<RESTError>(new RESTError(1, "Email already exists"), HttpStatus.CONFLICT);
		}

		UserEntity existingUserWithUsername = userRepository.findByUsername(newUser.getUsername()).orElse(null);
        logger.info("Finding out whether there's a user with the same username.");

		if (existingUserWithUsername != null) {
	        logger.error("There is a user with the same username.");
			return new ResponseEntity<RESTError>(new RESTError(2, "Username already exists"), HttpStatus.CONFLICT);
		}
		
		StudentEntity newStudent = new StudentEntity();

		newStudent.setFirstName(newUser.getFirstName());
		newStudent.setLastName(newUser.getLastName());
		newStudent.setUsername(newUser.getUsername());
		newStudent.setEmail(newUser.getEmail());
		newStudent.setPassword(newUser.getPassword());

		newStudent.setRole("ROLE_STUDENT");        
		logger.info("Setting users role.");

		studentRepository.save(newStudent);
        logger.info("Saving student to the database");

		return new ResponseEntity<StudentEntity>(newStudent, HttpStatus.CREATED);
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/studentsParent/{parents_id}/student/{students_id}")
	public ResponseEntity<?> setStudentsParent(@PathVariable Integer parents_id, @PathVariable Integer students_id) {
		StudentEntity student = studentRepository.findById(students_id).orElse(null);
		ParentEntity parent = parentRepository.findById(parents_id).orElse(null);

		if (student == null) {
	        logger.error("There is no student found with " + students_id);
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}

		if (parent == null) {
	        logger.error("There is no parent found with " + parents_id);
			return new ResponseEntity<RESTError>(new RESTError(2, "No parent found"), HttpStatus.NOT_FOUND);
		}

		student.setParent(parent);
        logger.info("Setting students parent");

		studentRepository.save(student);
        logger.info("Saving student to the database");

		return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/studentsTeachingSubj/teachsubj/{teachSubj_id}/student/{students_id}")
	public ResponseEntity<?> setStudentsTeachingSubj(@PathVariable Integer teachSubj_id,
			@PathVariable Integer students_id) {
		
		StudentEntity student = studentRepository.findById(students_id).orElse(null);
		TeacherSubject teacherSubject = teacherSubjectRepository.findById(teachSubj_id).orElse(null);

		if (teacherSubject == null) {
	        logger.error("There is no teaching subject found with " + teachSubj_id);
			return new ResponseEntity<RESTError>(new RESTError(1, "No teaching subject with " + teachSubj_id + " ID found"),
					HttpStatus.NOT_FOUND);
		}

		if (student == null) {
	        logger.error("There is no student found with " + students_id);
			return new ResponseEntity<RESTError>(new RESTError(2, "No student with " + students_id + " ID found"),
					HttpStatus.NOT_FOUND);
		}

		Set<TeacherSubject> teacherSubjects = student.getTeacherSubjects();
		
		teacherSubjects.add(teacherSubject);
		student.setTeacherSubjects(teacherSubjects);
        logger.info("Setting students teaching subject");

		studentRepository.save(student);
        logger.info("Saving student to the database");

		return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.PUT, value = "/updateStudent/{id}")
	public ResponseEntity<?> updateStudent(@RequestBody UserDTO updatedUser, BindingResult result, @PathVariable Integer id) {

		if (result.hasErrors()) {
	        logger.info("Validating users input parameters");
			return new ResponseEntity<>(ErrorMessageHelper.createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} else {
	        logger.info("Validating if the users password matches the confirming password");
			userValidator.validate(updatedUser, result);
		}
		
		StudentEntity student = studentRepository.findById(id).orElse(null);

		if (student == null) {
	        logger.error("There is no teacher found with " + id);
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}

		student.setFirstName(updatedUser.getFirstName());
		student.setLastName(updatedUser.getLastName());
		student.setUsername(updatedUser.getUsername());
		student.setEmail(updatedUser.getEmail());

		studentRepository.save(student);
        logger.info("Saving student to the database");

		return new ResponseEntity<StudentEntity>(student, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "deleteStudent/by-id/{id}")
	public ResponseEntity<?> deleteStudentByID(@PathVariable Integer id) {
		Optional<StudentEntity> student = studentRepository.findById(id);

		if (student.isPresent()) {
			studentRepository.delete(student.get());
	        logger.info("Deleting the student from the database");
			return new ResponseEntity<>("Student with ID " + id + " has been successfully deleted.", HttpStatus.OK);
		} else {
	        logger.error("There is no student found with " + id);
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}
	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "deleteStudent/by-username/{username}")
	public ResponseEntity<?> deleteStudentByUsername(@PathVariable String username) {
		Optional<StudentEntity> student = studentRepository.findByUsername(username);

		if (student.isPresent()) {
			
			for (GradeEntity grade : student.get().getGrades()) {
				gradeRepository.delete(grade);
			}
			
			studentRepository.delete(student.get());
	        logger.info("Deleting the student from the database");
			return new ResponseEntity<>("Student with " + username + " username has been successfully deleted.",
					HttpStatus.OK);
		} else {
	        logger.error("There is no student found with " + username);
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}

	}

	@Secured("ROLE_ADMIN")
	@JsonView(Views.Admin.class)
	@RequestMapping(method = RequestMethod.DELETE, value = "deleteStudent/parents_id/{parents_id}/students_id/{students_id}")
	public ResponseEntity<?> deleteStudentsParent(@PathVariable Integer parents_id, @PathVariable Integer students_id) {
		StudentEntity student = studentRepository.findById(students_id).orElse(null);

		if (student == null) {
	        logger.error("No student found with id " + students_id);
			return new ResponseEntity<RESTError>(new RESTError(1, "No student found"), HttpStatus.NOT_FOUND);
		}

		ParentEntity parent = student.getParent();

		if (parent == null || !parent.getId().equals(parents_id)) {
	        logger.error("No parent found with id " + parents_id + " for student with id " + students_id);
			return new ResponseEntity<RESTError>(new RESTError(1, "No parent found with id " + parents_id 
					+ " for student with id " + students_id), HttpStatus.NOT_FOUND);
		}
		
		if (student.getParent().equals(parent)) {
			parentRepository.delete(parent);
	        logger.info("Deleting parent with id " + parents_id + " from student with id " + students_id);

		}
		
		studentRepository.save(student);
        logger.info("Saving student");
		
		return new ResponseEntity<>("Parent with id " + parents_id + " was successfully removed from "
				+ "student with id " + students_id, HttpStatus.OK);
	}
	
	//EASTER EGG REST ENDPOINT
	@Secured("ROLE_STUDENT")
	@RequestMapping(method = RequestMethod.POST, value = "/dositejeva")
	public String zahtevZaDositejevuStipendiju () {
		return "Uspešno ste poslali zahtev za Dositejevu stipendiju!\n "
				+ "Udjite na link da vidite status: https://i.kym-cdn.com/photos/images/newsfeed/001/499/826/2f0.png ";
	}

}

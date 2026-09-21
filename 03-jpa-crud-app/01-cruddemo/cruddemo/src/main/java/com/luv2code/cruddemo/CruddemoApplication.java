package com.luv2code.cruddemo;

import com.luv2code.cruddemo.dao.StudentDAO;
import com.luv2code.cruddemo.entity.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CruddemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);


		}
	@Bean
	public CommandLineRunner commandLineRunner(StudentDAO studentDAO) {

		return runner -> {
//			createStudent(studentDAO);
			createMultipleStudents(studentDAO);
//			queryForStudents(studentDAO);
//			readStudent(studentDAO);
//			findByLastName(studentDAO);
//			updateStudent(studentDAO);
//			removeStudent(studentDAO);

		};
	}

	private void createMultipleStudents(StudentDAO studentDAO) {
		Student student1 = new Student("Kailong", "emma", "kemma@ex.com");
		Student student2 = new Student("Kailong2", "emma", "kemma@ex.com");
		Student student3 = new Student("Kailong3", "emma", "kemma@ex.com");

		studentDAO.save(student1);
		studentDAO.save(student2);
		studentDAO.save(student3);


	}

	private void createStudent(StudentDAO studentDAO) {
		//create the student object
		System.out.println("creating the new student...");
		Student tempStudent = new Student("Paul", "Doe", "nathan@terrasure.com");

		//save the student object
		System.out.println("Saving the student...");
		studentDAO.save(tempStudent);

		//display the student
		System.out.println("Saved student. Generated id: "+ tempStudent.getId());
	}


	public void readStudent(StudentDAO studentDAO) {
		// create student object
		System.out.println("creating a student obj...");
		Student tempStudent = new Student("nathan", "sodja", "sodjanathan@gmail.com");

		//save the student
		System.out.println("saving a student obj...");
		studentDAO.save(tempStudent);

		//display id of student
		int theId = tempStudent.getId();
		System.out.println("saved student. generated id: " + theId);

		//retrieve student by id
		System.out.println("reading student obj..." + theId);
		Student myStudent = studentDAO.findById(theId);


	}

	public void queryForStudents(StudentDAO studentDAO) {
		// get a list of students
		List<Student> theStudents = studentDAO.findAll();

		// display list of students
		for (Student tempStudent : theStudents) {
			System.out.println(tempStudent);
		}
	}

	public void findByLastName(StudentDAO studentDAO) {
		List<Student> theStudents = studentDAO.findByLastName("Doe");
		System.out.println("students with surname doe:");
		for (Student tempStudent : theStudents) {
			System.out.println(tempStudent);
		}
	}

	public void updateStudent(StudentDAO studentDAO) {
		// get student with id
		int studentId = 1;
		System.out.println("updating student with id " + studentId);
		Student myStudent = studentDAO.findById(studentId);
		// change student field
		System.out.println("updating student with id " + studentId);
		myStudent.setFirstName("Paul");
		myStudent.setLastName("Simon");
		// update
		studentDAO.update(myStudent);

		// display new record
		System.out.println("updated student with id " + studentId + " with last name " + myStudent.getLastName() );
	}

	public void removeStudent(StudentDAO studentDAO) {
		int studentId = 2;
		System.out.println("removing student with id " + studentId);

		Student myStudent = studentDAO.findById(studentId);
		System.out.println("removing student with id " + studentId);

		studentDAO.delete(studentId);

		System.out.println("removed student with id " + studentId);
	}
}



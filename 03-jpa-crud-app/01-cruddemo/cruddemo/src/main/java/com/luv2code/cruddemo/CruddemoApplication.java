package com.luv2code.cruddemo;

import com.luv2code.cruddemo.dao.StudentDAO;
import com.luv2code.cruddemo.entity.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CruddemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);


		}
	@Bean
	public CommandLineRunner commandLineRunner(StudentDAO studentDAO) {

		return runner -> {
			createStudent(studentDAO);
			createMultipleStudents(studentDAO);
		};
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

		//display student
	}
}



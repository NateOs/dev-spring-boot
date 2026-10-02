package com.terracom.springboot.cruddemo.dao;

import com.terracom.springboot.cruddemo.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "members") // this allows you to rename the path from /employees to /members
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}

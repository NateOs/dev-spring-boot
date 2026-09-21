package com.terracom.springboot.cruddemo.dao;

import com.terracom.springboot.cruddemo.entity.Employee;

import java.util.List;

public interface EmployeeDAO {
    List<Employee> findAll();
}

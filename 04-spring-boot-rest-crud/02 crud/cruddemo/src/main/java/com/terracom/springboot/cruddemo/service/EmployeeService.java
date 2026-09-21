package com.terracom.springboot.cruddemo.service;

import com.terracom.springboot.cruddemo.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> findAll();
}

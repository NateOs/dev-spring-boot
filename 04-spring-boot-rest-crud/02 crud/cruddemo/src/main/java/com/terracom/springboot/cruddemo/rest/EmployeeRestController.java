package com.terracom.springboot.cruddemo.rest;

import com.terracom.springboot.cruddemo.entity.Employee;
import com.terracom.springboot.cruddemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {
    // quick
    private EmployeeService employeeService;
    private JsonMapper jsonMapper;

    @Autowired
    public EmployeeRestController(EmployeeService theEmployeeService,  JsonMapper theJsonMapper) {
        employeeService = theEmployeeService;
        jsonMapper = theJsonMapper;
    }

    // expose /employees and return a list of employees
    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    // find a single by id
    @GetMapping("/employees/{employeeId}")
    public Employee findById(@PathVariable int employeeId) {

        Optional<Employee> result = employeeService.findById(employeeId);

        if (result.isEmpty()){
            throw new RuntimeException("Employee not found - "+employeeId);
        }
        return result.get();
    }

    // add new employee
    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee) {
            // also just in case they pass an id in JSOn.. set id to 0
        theEmployee.setId(0);

        return employeeService.save(theEmployee);
        }

    // update employee
    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee theEmployee) {

        return employeeService.save(theEmployee);
    }

    // partial update for employee
    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId, @RequestBody Map<String, Object> patchPayload){

        Optional<Employee> tempEmployee = employeeService.findById(employeeId);

        if (tempEmployee.isEmpty()){
            throw new RuntimeException("Employee not found - "+employeeId);
        }

        // throw exception if request body contains "id" key

        if( patchPayload.containsKey("id")){
            throw new RuntimeException("Employee id is not allowed in request body - " + employeeId);
        }

        Employee patchedEmployee = jsonMapper.updateValue(tempEmployee.get(), patchPayload);

        Employee dbEmployee = employeeService.save(patchedEmployee);

        return dbEmployee;
    }

    // delete employee by id
    @DeleteMapping("/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId) {
        Optional<Employee> theEmployee = employeeService.findById(employeeId);

        if (theEmployee == null){
            throw new RuntimeException("Employee not found - "+employeeId);
        }

        employeeService.deleteById(employeeId);

        return "Deleted employee id - " + employeeId;
    }

}

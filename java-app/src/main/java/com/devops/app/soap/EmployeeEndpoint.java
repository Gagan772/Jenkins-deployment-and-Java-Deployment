package com.devops.app.soap;

import com.devops.employee.*;
import com.devops.app.entity.Employee;
import com.devops.app.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
@RequiredArgsConstructor
public class EmployeeEndpoint {

    private static final String NAMESPACE_URI = "http://devops.com/employee";

    private final EmployeeService employeeService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEmployeeRequest")
    @ResponsePayload
    public GetEmployeeResponse getEmployee(@RequestPayload GetEmployeeRequest request) {
        GetEmployeeResponse response = new GetEmployeeResponse();
        Employee employee = employeeService.getEmployee(request.getId());
        
        com.devops.employee.Employee soapEmployee = new com.devops.employee.Employee();
        soapEmployee.setId(employee.getId());
        soapEmployee.setName(employee.getName());
        soapEmployee.setDepartment(employee.getDepartment());
        soapEmployee.setSalary(employee.getSalary());
        
        response.setEmployee(soapEmployee);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createEmployeeRequest")
    @ResponsePayload
    public CreateEmployeeResponse createEmployee(@RequestPayload CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setDepartment(request.getDepartment());
        employee.setSalary(request.getSalary());
        
        Employee savedEmployee = employeeService.createEmployee(employee);
        
        CreateEmployeeResponse response = new CreateEmployeeResponse();
        com.devops.employee.Employee soapEmployee = new com.devops.employee.Employee();
        soapEmployee.setId(savedEmployee.getId());
        soapEmployee.setName(savedEmployee.getName());
        soapEmployee.setDepartment(savedEmployee.getDepartment());
        soapEmployee.setSalary(savedEmployee.getSalary());
        
        response.setEmployee(soapEmployee);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listEmployeesRequest")
    @ResponsePayload
    public ListEmployeesResponse listEmployees(@RequestPayload ListEmployeesRequest request) {
        ListEmployeesResponse response = new ListEmployeesResponse();
        List<Employee> employees = employeeService.listEmployees();
        
        for (Employee emp : employees) {
            com.devops.employee.Employee soapEmployee = new com.devops.employee.Employee();
            soapEmployee.setId(emp.getId());
            soapEmployee.setName(emp.getName());
            soapEmployee.setDepartment(emp.getDepartment());
            soapEmployee.setSalary(emp.getSalary());
            response.getEmployees().add(soapEmployee);
        }
        
        return response;
    }
}

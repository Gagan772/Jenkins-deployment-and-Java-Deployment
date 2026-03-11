package com.devops.app;

import com.devops.app.dto.CustomerDTO;
import com.devops.app.entity.Customer;
import com.devops.app.repository.CustomerRepository;
import com.devops.app.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCustomers() {
        Customer customer1 = new Customer(1L, "John Doe", "john@example.com", "555-1234");
        Customer customer2 = new Customer(2L, "Jane Smith", "jane@example.com", "555-5678");
        
        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));
        
        List<CustomerDTO> customers = customerService.getAllCustomers();
        
        assertEquals(2, customers.size());
        assertEquals("John Doe", customers.get(0).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com", "555-1234");
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        
        CustomerDTO result = customerService.getCustomerById(1L);
        
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateCustomer() {
        CustomerDTO customerDTO = new CustomerDTO(null, "New Customer", "new@example.com", "555-0000");
        Customer customer = new Customer(1L, "New Customer", "new@example.com", "555-0000");
        
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        
        CustomerDTO result = customerService.createCustomer(customerDTO);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Customer", result.getName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        
        customerService.deleteCustomer(1L);
        
        verify(customerRepository, times(1)).deleteById(1L);
    }
}

package com.example.ssedemo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ssedemo.domain.Customer;
import com.example.ssedemo.store.CustomerRepository;

import reactor.core.publisher.Flux;

@RestController
public class CustomerController {
	
	private final CustomerRepository customerRepository;
	
	
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}


	@GetMapping("/customer")
	public Flux<Customer> findAll() {
		
		return customerRepository.findAll().log(); 
	}

}

package com.example.ssedemo.domain;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@ToString
public class Customer {
	
	@Id
    private Long id;
    private final String firstName;
    private final String lastName;	

}

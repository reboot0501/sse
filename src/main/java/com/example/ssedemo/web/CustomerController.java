package com.example.ssedemo.web;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ssedemo.domain.Customer;
import com.example.ssedemo.store.CustomerRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RestController
public class CustomerController {
	
	private final CustomerRepository customerRepository;
	private final Sinks.Many<Customer> customerSink;
	
	public CustomerController(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
		this.customerSink = Sinks.many().multicast().onBackpressureBuffer();
	}

	@GetMapping("/flux")
	public Flux<Integer> flux() {
		return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1L)).log();
	}
	
	@GetMapping(value = "/fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Integer> fluxstream() {
		return Flux.just(1, 2, 3, 4, 5).delayElements(Duration.ofSeconds(1L)).log();
	}

	@GetMapping("/customer")
	public Flux<Customer> findAll() {
		return this.customerRepository.findAll().log(); 
	}
	
	@GetMapping(value = "/customerstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<Customer> findAllStream() {
		return this.customerRepository.findAll().delayElements(Duration.ofSeconds(1L)).log(); 
	}
	
	@GetMapping("/customer/{id}")
	public Mono<Customer> findById(@PathVariable Long id) {
		return this.customerRepository.findById(id).log(); 
	}
	
	@GetMapping(value = "/customer/sse") // 생략가능 produces = MediaType.TEXT_EVENT_STREAM_VALUE
	public Flux<ServerSentEvent<Customer>> findAllSSE() {
		return this.customerSink.asFlux().map(c -> ServerSentEvent.builder(c).build())
				.doOnCancel(() -> {
			this.customerSink.asFlux().blockLast();
		}); 
	}
	
	@PostMapping("/customer")
	public Mono<Customer> save(@RequestBody Customer customer) {
		return this.customerRepository.save(customer).doOnNext(c -> {
			this.customerSink.tryEmitNext(c);
		}); 
	}

}

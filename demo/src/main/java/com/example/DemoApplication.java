package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

@RestController
class Hello {

	private final Greeting greeting;

	public Hello(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello")
	public Greeting greet() {
		return greeting;
	}

}

@RestController
class Hello2 {

	private final Greeting greeting;

	public Hello2(@Qualifier("defaultGreeting") Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello2")
	public Greeting greet() {
		return greeting;
	}

}

@Component("defaultGreeting")
class Greeting {

	public String message = "Hello world!";

	public Greeting() { }

	public Greeting(String message) {
		this.message = message;
	}
}

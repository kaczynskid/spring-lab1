package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean @Primary
	public Greeting defaultGreeting() {
		return new Greeting("Default hello!");
	}

	@Bean
	public Greeting specialGreeting() {
		return new Greeting("Special hello!");
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

	public Hello2(@Qualifier("specialGreeting") Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping("/hello2")
	public Greeting greet() {
		return greeting;
	}

}

class Greeting {

	public String message = "Hello world!";

	public Greeting(String message) {
		this.message = message;
	}
}

@Component
class BeanLoggingPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(BeanLoggingPostProcessor.class);

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		LOG.info("Created bean {} of type {}.", beanName, bean.getClass());
		return bean;
	}
}

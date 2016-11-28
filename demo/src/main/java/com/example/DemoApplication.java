package com.example;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DemoApplication.class);
		Map<String, Object> defaultProps = new HashMap<>();
		defaultProps.put("server.port", "9000");
		application.setDefaultProperties(defaultProps);
		application.run(args);
	}
}

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "greeting")
class Greetings {

	private String defaultMsg;
	private String specialMsg;

}

// FIXME split configs
@Configuration
class DefaultGreetingsConfig {

	@Autowired Greetings greetings;

	@Bean
	@Profile("special")
	public Greeting specialGreeting() {
		return new Greeting(greetings.getSpecialMsg());
	}

	@Bean
	@ConditionalOnMissingBean(Greeting.class)
	public Greeting defaultGreeting() {
		return new Greeting(greetings.getDefaultMsg());
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

@Data
@NoArgsConstructor
@AllArgsConstructor
class Greeting {

	private String message = "Hello world!";

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

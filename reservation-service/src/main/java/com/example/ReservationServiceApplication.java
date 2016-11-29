package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
}

@RestController
class ReservationsController {

	@GetMapping("/list")
	List<Reservation> list() {
		return Stream.of("Tomek:PLSQL", "Tomek:PLSQL", "Stanisław:PLSQL",
				"Grzegorz:C++", "Rafał:C++", "Andrzej:C++", "Tomek:C++",
				"Marek:Java", "Artur:OracleForms", "Jędrek:OracleForms")
				.map(entry -> entry.split(":"))
				.map(entry -> new Reservation(entry[0], entry[1]))
				.collect(Collectors.toList());
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	private String name;

	private String lang;

}

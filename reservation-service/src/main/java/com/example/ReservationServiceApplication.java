package com.example;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationServiceApplication.class, args);
	}
}

@RestController
@RequestMapping("/reservations")
class ReservationsController {

	List<Reservation> reservations = Stream.of(
			"Tomek:PLSQL", "Tomasz:PLSQL", "Stanisław:PLSQL",
			"Grzegorz:C++", "Rafał:C++", "Andrzej:C++", "Tom:C++",
			"Marek:Java", "Artur:OracleForms", "Jędrek:OracleForms")
			.map(entry -> entry.split(":"))
			.map(entry -> new Reservation(entry[0], entry[1]))
			.collect(Collectors.toList());

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	List<Reservation> list() {
		return reservations;
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	void create(@RequestBody Reservation reservation) {
		reservations.add(reservation);
	}

	@GetMapping(path = "/{name}", produces = APPLICATION_JSON_VALUE)
	Reservation get(@PathVariable("name") String name) {
		return findOne(name);
	}

	@PutMapping(path = "/{name}", consumes = APPLICATION_JSON_VALUE)
	void update(@PathVariable("name") String name, @RequestBody Reservation reservation) {
		Reservation existing = findOne(name);
		if (existing != null) {
			existing.setName(reservation.getName());
			existing.setLang(reservation.getLang());
		}
	}

	@DeleteMapping("/{name}")
	void delete(@PathVariable("name") String name) {
		Reservation reservation = findOne(name);
		if (reservation != null) {
			reservations.remove(reservation);
		}
	}

	private Reservation findOne(String name) {
		for (Reservation reservation : reservations) {
			if (reservation.getName().equals(name)) {
				return reservation;
			}
		}
		return null;
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	private String name;

	private String lang;

}

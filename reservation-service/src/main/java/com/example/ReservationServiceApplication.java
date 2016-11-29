package com.example;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

	private final ReservationsService reservations;

	public ReservationsController(ReservationsService reservations) {
		this.reservations = reservations;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	List<Reservation> list() {
		return reservations.findAll();
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(CREATED)
	void create(@RequestBody Reservation reservation) {
		reservations.create(reservation);
	}

	@GetMapping(path = "/{name}", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<?> get(@PathVariable("name") String name) {
		Optional<Reservation> reservation = reservations.findOne(name);
		if (reservation.isPresent()) {
			return ResponseEntity.ok(reservation.get());
		} else {
			return ResponseEntity.status(NOT_FOUND).build();
		}
	}

	@PutMapping(path = "/{name}", consumes = APPLICATION_JSON_VALUE)
	void update(@PathVariable("name") String name, @RequestBody Reservation reservation) {
		reservations.update(name, reservation);
	}

	@DeleteMapping("/{name}")
	@ResponseStatus(NO_CONTENT)
	void delete(@PathVariable("name") String name) {
		reservations.delete(name);
	}

	@ExceptionHandler(ReservationNotFound.class)
	void handleReservationNotFound(ReservationNotFound ex, HttpServletResponse response) throws IOException {
		response.sendError(NOT_FOUND.value(), ex.getMessage());
	}
}

@Component
class ReservationsService {

	private List<Reservation> reservations = Stream.of(
			"Tomek:PLSQL", "Tomasz:PLSQL", "Stanisław:PLSQL",
			"Grzegorz:C++", "Rafał:C++", "Andrzej:C++", "Tom:C++",
			"Marek:Java", "Artur:OracleForms", "Jędrek:OracleForms")
			.map(entry -> entry.split(":"))
			.map(entry -> new Reservation(entry[0], entry[1]))
			.collect(Collectors.toList());

	List<Reservation> findAll() {
		return reservations;
	}

	Optional<Reservation> findOne(String name) {
		for (Reservation reservation : reservations) {
			if (reservation.getName().equals(name)) {
				return Optional.of(reservation);
			}
		}
		return Optional.empty();
	}

	Reservation create(Reservation reservation) {
		findOne(reservation.getName())
			.ifPresent(existing -> {
				throw new ReservationAlreadyExists(existing.getName());
			});
		reservations.add(reservation);
		return reservation;
	}

	Reservation update(String name, Reservation reservation) {
		return findOne(name)
			.map(existing -> {
				existing.setName(reservation.getName());
				existing.setLang(reservation.getLang());
				return existing;
			})
			.orElseThrow(() -> new ReservationNotFound(name));
	}

	void delete(String name) {
		findOne(name)
			.ifPresent(existing -> {
				reservations.remove(existing);
			});
	}
}

class ReservationNotFound extends RuntimeException {
	public ReservationNotFound(String name) {
		super("Reservation for name '" + name + "' not found!");
	}
}

@ResponseStatus(CONFLICT)
class ReservationAlreadyExists extends RuntimeException {
	public ReservationAlreadyExists(String name) {
		super("Reservation for name '" + name + "' already exists!");
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	private String name;

	private String lang;

}

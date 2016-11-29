package com.example;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
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

@Slf4j
@Configuration
@EnableAspectJAutoProxy
class ServiceConfig {

	@Bean
	ReservationsService reservationsService(ReservationsRepository repository) {
		ReservationsService target = new ReservationsServiceImpl(repository);
		return (ReservationsService) Proxy.newProxyInstance(
			Thread.currentThread().getContextClassLoader(),
			new Class[] { ReservationsService.class },
			(Object proxy, Method method, Object[] args) -> {
				log.info("BEFORE method {}", method.getName());
				Object result = method.invoke(target, args);
				log.info("AFTER method {}. RETURNED {}", method.getName(), result);
				return result;
			}
		);
	}
}

@Slf4j
@Aspect
@Component
class MonitorAspect {

	@Pointcut("execution(* com.example.ReservationsService.*(..))")
	private void anyServiceOperation() {}

	@Around("anyServiceOperation()")
	public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		String name = joinPoint.getSignature().getName();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(name);
		Object result = joinPoint.proceed();
		stopWatch.stop();
		log.info("MONITOR method {} took {}ms", name, stopWatch.getLastTaskInfo().getTimeMillis());
		return result;
	}
}

interface ReservationsService {

	List<Reservation> findAll();

	Optional<Reservation> findOne(String name);

	Reservation create(Reservation reservation);

	Reservation update(String name, Reservation reservation);

	void delete(String name);
}

class ReservationsServiceImpl implements ReservationsService {

	private final ReservationsRepository reservations;

	ReservationsServiceImpl(ReservationsRepository reservations) {
		this.reservations = reservations;
	}

	public List<Reservation> findAll() {
		return reservations.findAll();
	}

	public Optional<Reservation> findOne(String name) {
		return reservations.findOne(name);
	}

	public Reservation create(Reservation reservation) {
		findOne(reservation.getName())
			.ifPresent(existing -> {
				throw new ReservationAlreadyExists(existing.getName());
			});
		reservations.create(reservation);
		return reservation;
	}

	public Reservation update(String name, Reservation reservation) {
		return findOne(name)
			.map(existing -> {
				reservations.update(name, reservation);
				return existing;
			})
			.orElseThrow(() -> new ReservationNotFound(name));
	}

	public void delete(String name) {
		reservations.delete(name);
	}
}

class ReservationNotFound extends RuntimeException {
	ReservationNotFound(String name) {
		super("Reservation for name '" + name + "' not found!");
	}
}

@ResponseStatus(CONFLICT)
class ReservationAlreadyExists extends RuntimeException {
	ReservationAlreadyExists(String name) {
		super("Reservation for name '" + name + "' already exists!");
	}
}

@Component
class ReservationsRepository {

	private final RowMapper<Reservation> mapper = (ResultSet rs, int rowNum) ->
		new Reservation(
			rs.getString("name"),
			rs.getString("lang")
		);

	private final JdbcTemplate jdbc;

	public ReservationsRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	List<Reservation> findAll() {
		return jdbc.query(
			"select * from reservations",
			mapper);
	}

	Optional<Reservation> findOne(String name) {
		return jdbc.query(
			"select * from reservations r where r.name = ?",
			mapper,
			name
		).stream().findFirst();
	}

	void create(Reservation reservation) {
		jdbc.update(
			"insert into reservations values(?, ?)",
			reservation.getName(), reservation.getLang());
	}

	void update(String name, Reservation reservation) {
		jdbc.update(
			"update reservations set name=?, lang=? where name=?",
			reservation.getName(), reservation.getLang(), name);
	}

	void delete(String name) {
		jdbc.update(
			"delete from reservations where name=?",
			name);
	}
}

@Component
class ReservationsInitializer implements ApplicationRunner {

	@Autowired ReservationsRepository reservations;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Stream.of(
			"Tomek:PLSQL", "Tomasz:PLSQL", "Stanisław:PLSQL",
			"Grzegorz:C++", "Rafał:C++", "Andrzej:C++", "Tom:C++",
			"Marek:Java", "Artur:OracleForms", "Jędrek:OracleForms")
			.map(entry -> entry.split(":"))
			.map(entry -> new Reservation(entry[0], entry[1]))
			.filter(r -> !reservations.findOne(r.getName()).isPresent())
			.forEach(r-> reservations.create(r));

	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	private String name;

	private String lang;

}

package com.example;

import static com.example.QReservation.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;

@RepositoryRestResource
public interface ReservationsRepository extends JpaRepository<Reservation, Long>,
		QueryDslPredicateExecutor<Reservation>, LegacyReservationsRepository {

	class Spec {

		static BooleanExpression withName(String name) {
			return name == null ? null : reservation.name.equalsIgnoreCase(name);
		}

		static BooleanExpression withLang(String lang) {
			return lang == null ? null : reservation.lang.equalsIgnoreCase(lang);
		}
	}

	default Optional<Reservation> findById(Long id) {
		return Optional.ofNullable(findOne(id));
	}

	Optional<Reservation> findByName(@Param("name") String name);

	@Override
	List<Reservation> findAll(Predicate predicate);

	@Override
	@RestResource(exported = false)
	void delete(Long id);
}

@Slf4j
@Component
@RepositoryEventHandler
class ReservationEventHandler {

	private final CounterService counter;

	public ReservationEventHandler(CounterService counter) {
		this.counter = counter;
	}

	@HandleAfterCreate
	public void create(Reservation reservation) {
		log.info("Created reservation for {}.", reservation.getName());
		counter.increment("count");
		counter.increment("create");
	}

	@HandleAfterSave
	public void save(Reservation reservation) {
		log.info("Updated reservation for {}.", reservation.getName());
		counter.increment("save");
	}

	@HandleAfterDelete
	public void delete(Reservation reservation) {
		log.info("Removed reservation for {}.", reservation.getName());
		counter.decrement("count");
		counter.increment("delete");
	}
}

package com.example;

import static com.example.QReservation.*;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
interface ReservationsRepository extends JpaRepository<Reservation, Long>,
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

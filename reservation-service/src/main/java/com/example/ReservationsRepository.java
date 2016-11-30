package com.example;

import static com.example.QReservation.reservation;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

interface ReservationsRepository extends JpaRepository<Reservation, Long>,
		QueryDslPredicateExecutor<Reservation> {

	class Spec {

		static BooleanExpression withName(String name) {
			return name == null ? null : reservation.name.equalsIgnoreCase(name);
		}

		static BooleanExpression withLang(String lang) {
			return lang == null ? null : reservation.lang.equalsIgnoreCase(lang);
		}
	}


	Optional<Reservation> findByName(String name);

	@Query("from Reservation where lang = :lang")
	List<Reservation> findByLang(@Param("lang") String lang);

	@Override
	List<Reservation> findAll(Predicate predicate);
}

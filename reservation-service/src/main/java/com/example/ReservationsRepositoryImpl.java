package com.example;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ReservationsRepositoryImpl implements LegacyReservationsRepository {

	@PersistenceContext EntityManager jpa;

	@Override
	public List<Reservation> findByLang(String lang) {
		return jpa.createQuery("from Reservation where lang = :lang").getResultList();
	}
}

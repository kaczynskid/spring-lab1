package com.example;

import java.util.List;

public interface LegacyReservationsRepository {

	List<Reservation> findByLang(String lang);

}

package com.example;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

public class ReservationsServiceImplTest {

	ReservationsRepository repository = Mockito.mock(ReservationsRepository.class);
	ReservationsServiceImpl reservations = new ReservationsServiceImpl(repository);

	@Test
	public void should_not_allow_to_change_name_to_existing_one() throws Exception {
		// given
		Long id = 5L;
		Reservation reservation = new Reservation(id, "Jan", "Java");
		when(repository.findOne(id)).thenReturn(reservation);
		when(repository.findByName("Jan")).thenReturn(Optional.of(reservation));

		// when
		Throwable thrown = catchThrowable(() -> reservations.update(id, reservation));

		// then
		assertThat(thrown).isInstanceOf(ReservationAlreadyExists.class);
		assertThat(thrown.getMessage()).isEqualTo("Reservation for name 'Jan' already exists!");
	}
}

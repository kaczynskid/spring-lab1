package com.example;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest
public class ReservationsControllerTest {

//	@TestConfiguration
//	static class TestConfig {
//		@Bean
//		ReservationsService service() {
//			return Mockito.mock(ReservationsService.class);
//		}
//	}

	@MockBean ReservationsService service;
	@Autowired MockMvc mvc;

	@Test
	public void should_return_404_when_not_found() throws Exception {
		// given
		Long id = 5L;
		when(service.findOne(id)).thenReturn(Optional.empty());

		// when
		mvc.perform(get("/custom-reservations/{id}", id))

		// then
			.andExpect(status().isNotFound());
	}

	@Test
	public void should_return_200_when_found() throws Exception {
		// given
		Long id = 5L;
		Reservation reservation = new Reservation(id, "Jan", "Java");
		when(service.findOne(id)).thenReturn(Optional.of(reservation));

		// when
		mvc.perform(get("/custom-reservations/{id}", id))

		// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("@.id").value("5"))
			.andExpect(jsonPath("@.name").value("Jan"))
			.andExpect(jsonPath("@.lang").value("Java"));
	}
}

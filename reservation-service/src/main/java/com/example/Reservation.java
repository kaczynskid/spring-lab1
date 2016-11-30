package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
class Reservation {

	@Id @GeneratedValue
	private Long id;

	private String name;

	private String lang;

	Reservation(String name, String lang) {
		this.name = name;
		this.lang = lang;
	}
}

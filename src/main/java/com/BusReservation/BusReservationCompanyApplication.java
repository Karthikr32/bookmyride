package com.BusReservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BusReservationCompanyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BusReservationCompanyApplication.class, args);
	}
}

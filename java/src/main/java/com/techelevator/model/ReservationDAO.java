package com.techelevator.model;

import java.time.LocalDate;

public interface ReservationDAO {
	
	
	//Returns reservation by Id
	public Reservation getReservationById(int reservationId);

	public void createReservationInDatabase(Reservation newReservation);
}
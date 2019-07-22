package com.techelevator.model.jdbc;

import java.time.LocalDate;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.Reservation;
import com.techelevator.model.ReservationDAO;
import com.techelevator.model.Site;

public class JDBCReservationDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Reservation getReservationById(int reservationId) {
		Reservation foundReservation = new Reservation();
		
		String SqlReservationId = "SELECT reservation_id, site_id, name, from_date, to_date, create_date " + 
									" FROM reservation WHERE reservation_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(SqlReservationId, reservationId);
		
		while(result.next()) {
			foundReservation = populateReservation(result);
		}
		
		
		return foundReservation;
	}

	@Override
	public void createReservationInDatabase(Reservation newReservation) {
		
		String sqlCreateReservation = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date) "
									+ "VALUES (?,?,?,?,?) RETURNING reservation_id";
		
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlCreateReservation, newReservation.getSiteId(),
				newReservation.getName(), newReservation.getStartDate(), newReservation.getEndDate(), newReservation.getCreateDate());
		if (result.next()) {
			newReservation.setReservationId(result.getInt("reservation_id"));
		}
		 
	}

	public Reservation populateReservation(SqlRowSet result) {
		Reservation thisReservation = new Reservation();
		
		thisReservation.setReservationId(result.getInt("reservation_id"));
		thisReservation.setSiteId(result.getInt("site_id"));
		thisReservation.setName(result.getString("name"));
		thisReservation.setStartDate(result.getDate("from_date").toLocalDate());
		thisReservation.setEndDate(result.getDate("to_date").toLocalDate());
		thisReservation.setCreateDate(result.getDate("create_date").toLocalDate());
		
		return thisReservation;
	}
}

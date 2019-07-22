package com.techelevator.model.jdbc;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.Campground;
import com.techelevator.model.CampgroundDAO;

public class JDBCCampgroundDAO implements CampgroundDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public JDBCCampgroundDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public  List<Campground> getAllCampgrounds(int parkId){
		List<Campground> campgroundList = new ArrayList<Campground>();
		
		String SqlAllCampground = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, "
								+ "daily_fee FROM campground WHERE park_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(SqlAllCampground, parkId);
		
		while(result.next()) {
			campgroundList.add(populateCampground(result));
		}
		
		return campgroundList;
	}

	@Override
	public Campground getCampgroundById(int campgroundId) {
		Campground foundCampground = new Campground();
		String SqlCampgroundId = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee"
								+ " FROM campground WHERE campground_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(SqlCampgroundId, campgroundId);
		
		if(result.next()) {
			foundCampground = populateCampground(result);
		}
		
		return foundCampground;
	}
	
	
	public Campground populateCampground(SqlRowSet results) {
		Campground thisCampground = new Campground();
		
		thisCampground.setCampgroundId(results.getInt("campground_id"));
		thisCampground.setParkId(results.getInt("park_id"));
		thisCampground.setName(results.getString("name"));
		
		int number = Integer.parseInt(results.getString("open_from_mm"));
		thisCampground.setOpenMonth(number);
		
		number = Integer.parseInt(results.getString("open_to_mm"));
		thisCampground.setCloseMonth(number);
		
		thisCampground.setDailyfee(results.getBigDecimal("daily_fee"));
		
		return thisCampground;
		
	}
	
}

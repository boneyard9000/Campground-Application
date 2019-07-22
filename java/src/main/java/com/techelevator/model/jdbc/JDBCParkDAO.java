package com.techelevator.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.Park;
import com.techelevator.model.ParkDAO;

public class JDBCParkDAO implements ParkDAO {

	
	private JdbcTemplate jdbcTemplate;

	public JDBCParkDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public List<Park> getAllParks(){
		List<Park> parkList = new ArrayList<Park>();
		
		String SqlGetAllParks = "SELECT park_id, name, location, establish_date, area, visitors, description FROM park ORDER BY name";
		SqlRowSet result = jdbcTemplate.queryForRowSet(SqlGetAllParks);
		
		while(result.next()) {
			parkList.add(populatePark(result));
		}
		
		return parkList;
	}
	
	
	
	private Park populatePark(SqlRowSet result) {
		Park thisPark = new Park();
		
		thisPark.setParkId(result.getInt("park_id"));
		thisPark.setName(result.getString("name"));
		thisPark.setLocation(result.getString("location"));
		thisPark.setEstablishDate(result.getDate("establish_date").toLocalDate());
		thisPark.setArea(result.getInt("area"));
		thisPark.setVisitors(result.getInt("visitors"));
		
		String str = result.getString("description");
		String updatedString = str.replaceAll("(.{60})", "$1\n");
		thisPark.setDescription(updatedString);
		
		
		return thisPark;
	}
}

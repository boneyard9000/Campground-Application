package com.techelevator.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.model.Site;
import com.techelevator.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO{

	private JdbcTemplate jdbcTemplate;
	
	public JDBCSiteDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Site> getAllSites(int campgroundId) {
		List<Site> siteList = new ArrayList<Site>();

		String SqlGetSites = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " + 
								"FROM site WHERE campground_id = ? " + 
								"ORDER BY max_occupancy DESC " + 
								"LIMIT 5";
		
		SqlRowSet result = jdbcTemplate.queryForRowSet(SqlGetSites, campgroundId);
		
		while(result.next()) {
			siteList.add(populateSite(result));
		}
		
		return siteList;
	}

	@Override
	public Site getSiteById(int siteId) {
		
		Site foundSite = new Site();
		String SqlFindSite = "SELECT * FROM site WHERE site_id = ?";
		
		SqlRowSet result = jdbcTemplate.queryForRowSet(SqlFindSite, siteId);
		
		while(result.next()) {
			foundSite = populateSite(result);
		}
		
		
		return foundSite;
	}
	
	
	
	public Site populateSite(SqlRowSet result) {
		Site thisSite = new Site();
		
		thisSite.setSiteId(result.getInt("site_id"));
		thisSite.setCampgroundId(result.getInt("campground_id"));
		thisSite.setSiteNumber(result.getInt("site_number"));
		thisSite.setMaxOccupancy(result.getInt("max_occupancy"));
		thisSite.setMaxRVLength(result.getInt("max_rv_length"));
		thisSite.setUtilities(result.getBoolean("utilities"));
		
		return thisSite;
	}
	
	//checks database for existing resevations and returns any reservations that overlap.  based on dates given. 
	//if database returns nothing there are no conflicts, site can be reserved
	@Override
	public boolean checkSiteAvailable(int siteId, LocalDate fromDate, LocalDate toDate) {
		boolean isAvailable = false;
		String fromDateString = fromDate.toString();
		String toDateString = toDate.toString();
		String sqlFindAvailableSites = "SELECT * FROM reservation " + 
										"WHERE site_id = ? AND (" + 
										" (?::date <= from_date AND ?::date >= from_date) " + 
										"OR (?::date <= to_date AND ?::date >= to_date) " + 
										")";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlFindAvailableSites, siteId, fromDate, toDate, fromDate, toDate);
	
		if (!result.next()) {
		
			isAvailable = true;
		}
		
		return isAvailable;
	}
	
	@Override
	public List<Site> allAvailableSites(int campgroundId, LocalDate fromDate, LocalDate toDate) {
		List<Site> openSites = new ArrayList<Site>();
		String fromDateString = fromDate.toString();
		String toDateString = toDate.toString();
		String sqlFindAvailableSites = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " +
										"FROM site WHERE campground_id = ? " +
										"AND (site_id NOT IN (" + 
										"SELECT site_id FROM reservation " + 
										"WHERE  (?::date <= from_date AND ?::date >= from_date) " + 
										"OR (?::date <= to_date AND ?::date >= to_date) " + 
										"))  ORDER BY max_occupancy DESC LIMIT 5";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlFindAvailableSites, campgroundId, fromDate, toDate, fromDate, toDate);
		while (result.next()) {
			Site s = populateSite(result);
			openSites.add(s);
		}
		
		return openSites;
	}

}

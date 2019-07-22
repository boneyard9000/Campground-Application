package com.techelevator.model;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {
	
	
	//Returns available Sites
	public List<Site> getAllSites(int campgroundId);
	
	
	//Returns one Site based on ID
	public Site getSiteById(int siteId);
	

	
	//check if a site is available
	public boolean checkSiteAvailable(int siteId, LocalDate fromDate, LocalDate toDate);
	
	public List<Site> allAvailableSites(int campgroundId, LocalDate fromDate, LocalDate toDate);
}

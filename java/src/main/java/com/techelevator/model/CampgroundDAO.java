package com.techelevator.model;

import java.time.LocalDate;
import java.util.List;

public interface CampgroundDAO {

	//Returns all Campgrounds in a list
	public List<Campground> getAllCampgrounds(int parkId);
	
	//Returns one campground based on id
	public Campground getCampgroundById(int campgroundId);
	
}

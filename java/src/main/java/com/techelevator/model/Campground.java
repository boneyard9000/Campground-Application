package com.techelevator.model;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.time.LocalDate;

public class Campground {

	private int campgroundId;
	private int parkId;
	private String name;
	private int openMonth;
	private int closeMonth;
	private BigDecimal dailyfee;
	
	public int getCampgroundId() {
		return campgroundId;
	}
	
	public void setCampgroundId(int campgroundId) {
		this.campgroundId = campgroundId;
	}
	
	public int getParkId() {
		return parkId;
	}
	
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getOpenMonth() {
		return openMonth;
	}
	
	public void setOpenMonth(int openMonth) {
		this.openMonth = openMonth;
	}
	
	public int getCloseMonth() {
		return closeMonth;
	}
	
	public void setCloseMonth(int closeMonth) {
		this.closeMonth = closeMonth;
	}
	
	public BigDecimal getDailyfee() {
		return dailyfee;
	}
	
	public void setDailyfee(BigDecimal dailyfee) {
		this.dailyfee = dailyfee;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static void main(String[] args) {
		Campground testCamp = new Campground();
		testCamp.setCloseMonth(5);
		System.out.println(testCamp.getCloseMonth());
	
	}
	
	public String getMonth(int month) {
	    return new DateFormatSymbols().getMonths()[month-1];
	}
}

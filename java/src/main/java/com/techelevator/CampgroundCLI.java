package com.techelevator;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.model.Campground;
import com.techelevator.model.CampgroundDAO;
import com.techelevator.model.Park;
import com.techelevator.model.ParkDAO;
import com.techelevator.model.Reservation;
import com.techelevator.model.ReservationDAO;
import com.techelevator.model.Site;
import com.techelevator.model.SiteDAO;
import com.techelevator.model.jdbc.JDBCCampgroundDAO;
import com.techelevator.model.jdbc.JDBCParkDAO;
import com.techelevator.model.jdbc.JDBCReservationDAO;
import com.techelevator.model.jdbc.JDBCSiteDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_PARKS = "Display All Parks";
	private static final String MAIN_MENU_OPTION_CHOOSE_PARK = "Choose A Park";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";
	private static final String MAIN_MENU_OPTION_DISPLAY_RESERVATION = "Display Your Current Reservations";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_PARKS, MAIN_MENU_OPTION_CHOOSE_PARK,
			MAIN_MENU_OPTION_DISPLAY_RESERVATION, MAIN_MENU_OPTION_EXIT };

	private static final String SUB_MENU_OPTION_1 = "Acadia";
	private static final String SUB_MENU_OPTION_2 = "Arches";
	private static final String SUB_MENU_OPTION_3 = "Cuyahoga Valley";
	private static final String[] SUB_MENU_OPTIONS = { SUB_MENU_OPTION_1, SUB_MENU_OPTION_2, SUB_MENU_OPTION_3 };

	private String[] subMenuOptions = new String[3];

	Scanner inputScanner = new Scanner(System.in);
	boolean stillUsingCLI = true;
	private Menu menu;
	private CampgroundDAO campgroundDAO;
	private ParkDAO parkDAO;
	private ReservationDAO reservationDAO;
	private SiteDAO siteDAO;



	private Map<Integer, Park> parkMap = new LinkedHashMap<Integer, Park>();
	private Map<Integer, Campground> campgroundMap = new LinkedHashMap<Integer, Campground>();
	private Map<Integer, Site> campsiteMap = new LinkedHashMap<Integer, Site>();
	private Map<Integer, Reservation> reservationMap = new HashMap<Integer, Reservation>();
	private Map<Integer, Site> openSiteMap = new HashMap<Integer, Site>();
	private Map<Integer, Site> availableSitesMap = new HashMap<Integer, Site>();

	private int parkChoice;
	private int campgroundChoice;
	private int campsiteChoice;

	String reservationName;
	Random rand = new Random();
	LocalDate desiredStart;
	LocalDate desiredEnd;
	
	NumberFormat currency = NumberFormat.getCurrencyInstance();


	public static void main(String[] args) {

		CampgroundCLI application = new CampgroundCLI();
		application.run();
	}

	public CampgroundCLI() {
		this.menu = new Menu(System.in, System.out);

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		campgroundDAO = new JDBCCampgroundDAO(dataSource);
		parkDAO = new JDBCParkDAO(dataSource);
		reservationDAO = new JDBCReservationDAO(dataSource);
		siteDAO = new JDBCSiteDAO(dataSource);

	}

	public void run() {
		
		

		while (stillUsingCLI) {

			System.out.println("- MAIN MENU -");
			List<Park> parkList = parkDAO.getAllParks();
			parkMap = createParkMap(parkList);
			LocalDate currentDate;
			// display park options
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_PARKS)) {
				handleDisplayParksOption();
			}

			
			// displays park with full info then redirects to main menu
			else if (choice.equals(MAIN_MENU_OPTION_CHOOSE_PARK)) {
				boolean stillUsingParkMenu = true;
				while (stillUsingParkMenu) {
				printParks(parkMap);
				parkChoice = menu.getUserChoiceAsInt();

				// enter choose park sub menu
				//validate choice
				while (!parkMap.containsKey(parkChoice) && parkChoice != 0) {
					System.out.println("Sorry, invalid selection.  Please choose another.");
					parkChoice = menu.getUserChoiceAsInt();
				}
				if (parkMap.containsKey(parkChoice)) {

					System.out.println("- " + parkMap.get(parkChoice).getName() + " -");

					// enter choose campground sub menu
					System.out.println();
					System.out.println("Choose a campground to check availability");
					System.out.println();
					
					printCampgrounds(campgroundMap);
					campgroundChoice = menu.getUserChoiceAsInt();

					boolean usingCampgroundMenu = true;
					while (!campgroundMap.containsKey(campgroundChoice) && campgroundChoice != 0) {
						System.out.println("Sorry, invalid selection.  Please choose another");
						campgroundChoice = menu.getUserChoiceAsInt();

					}
					if (campgroundChoice == 0) {
						usingCampgroundMenu = false;
					}
					else if (campgroundMap.containsKey(campgroundChoice) ) {
						
						System.out.println("- " + campgroundMap.get(campgroundChoice).getName() + " -");

						//enter pick dates sub menu
						printOpenSites();
						
						campsiteChoice = menu.getUserChoiceAsInt();
						while (!checkCampsiteChoice(campsiteChoice)) {
							System.out.println("Sorry, invalid entry.  Please make another Selection");
							campsiteChoice = menu.getUserChoiceAsInt();
						}

						//enter create reservation
						if (checkCampsiteChoice(campsiteChoice)) {

						System.out.println("What name should the reservation be under?");
						reservationName = menu.getUserInputString();

						currentDate = LocalDate.now();

						Reservation customerReservation = setNewReservation(reservationName,
								availableSitesMap.get(campsiteChoice).getSiteId(), desiredStart, desiredEnd, currentDate);
						addReservation(customerReservation);
						boolean confirm = confirmReservation();
						if (!confirm) {
							System.out.println("Reservation has been canceled.  Returning to main menu");
						}
						else {
						System.out.println("Your site has been Reserved.  Thank you " + reservationName);
						System.out.println("Your reservation number is " + customerReservation.getReservationId());
						System.out.println("Redirecting to main menu");
						System.out.println();
						}
						stillUsingParkMenu = false;

						}
					}

					else {
						System.out.println("That campground is not open during that time.  Please make another selection");
						printOpenSites();
					}

				}
				else if (parkChoice == 0) {
					stillUsingParkMenu = false;
				}
				}
			}
			
			// enter confirmation code to get back your reservation
			else if (choice.equals(MAIN_MENU_OPTION_DISPLAY_RESERVATION)) {
				boolean stillUsing = true;
				while (stillUsing) {
				System.out.println("Please enter your confirmation code to check your reservation or choose 0 to return to previous menu");
				int userCode = menu.getUserChoiceAsInt();
				Reservation r = reservationDAO.getReservationById(userCode);

				if (r.getReservationId() != 0) {
					System.out.format("%-20s%-20s%-35s%-20s%-20s%-20s\n\n", "Reservation Number", "Site ID",
							"Reservation Name", "Start Date", "End Date", "Date Created");
					System.out.format("%-20s%-20s%-35s%-20s%-20s%-20s\n\n", userCode, r.getSiteId(), r.getName(),
							r.getStartDate(), r.getEndDate(), r.getCreateDate());
					stillUsing = false;
				} 
				
				else if (parkChoice == 0) {
					stillUsing = false;
				}
				
				else {
					System.out.println("That is not a valid confirmation code");
				}
				}

			} else if (choice.equals(MAIN_MENU_OPTION_EXIT)) {
				System.out.println("Have a great day!");
				stillUsingCLI = false;
			}
			
		}

	}
	


	//conatins all parks
	private Map<Integer, Park> createParkMap(List<Park> parkList) {
		for (int i = 0; i < parkList.size(); i++) {
			parkMap.put(i + 1, parkList.get(i));
		}
		return parkMap;
	}
	
	//contains all parks for the chosen campground
	private Map<Integer, Campground> createCampgroundMap(List<Campground> campgroundList) {
		for (int i = 0; i < campgroundList.size(); i++) {
			campgroundMap.put(i + 1, campgroundList.get(i));
		}
		return campgroundMap;
	}

	//main menu option 1
	private void printParks(Map<Integer, Park> parkMap) {
		System.out.println();
		System.out.println("Please select a park or choose 0 to return to previous menu");
		System.out.println();

		System.out.println("0) Back to previous menu");
		for (Integer i : parkMap.keySet()) {

			System.out.println(i.toString() + ") " + parkMap.get(i).toString());
		}
		System.out.println();
	}

	//prints campgrounds with full info
	private void printCampgrounds(Map<Integer, Campground> campgroundMap) {
		System.out.format("%-5s%-25s%-25s%-25s%-25s\n", "", "Name", "Opens", "Closes", "Daily Fee");
		campgroundMap = createCampgroundMap(
				campgroundDAO.getAllCampgrounds(parkMap.get(parkChoice).getParkId()));
		
		System.out.println("0)   Return to previous menu");
		for (Integer i : campgroundMap.keySet()) {
			Campground c = campgroundMap.get(i);
			System.out.format("%-5s%-25s%-25s%-25s%-25s\n", (i + ") "), c.getName(), Month.of(c.getOpenMonth()).name(),
					Month.of(c.getCloseMonth()).name(), currency.format(c.getDailyfee()));
		}
	}



	private LocalDate getDateFromUser() {
		LocalDate date = null;

		while (date == null) {
		System.out.println("Please Enter Date:  MM/DD/YYYY");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		
		String dateString = menu.getUserInputString();
		
		try {
		date = LocalDate.parse(dateString, formatter);
		System.out.println("date: " + date);
		}
		catch(DateTimeParseException e) {
			System.out.println("Invalid Date");
		}
		}
		return date;
		
	}

	public boolean checkCampsiteAvailability(LocalDate desiredStart, LocalDate desiredEnd, Campground chosenCamp) {
		boolean isAvailable = false;
		int openMonth = campgroundMap.get(campgroundChoice).getOpenMonth();
		int closeMonth = campgroundMap.get(campgroundChoice).getCloseMonth();

		if ((desiredStart.getMonthValue() > openMonth) && (desiredEnd.getMonthValue() < closeMonth)) {
			System.out.println("Campsite is available");
			isAvailable = true;
		}
		return isAvailable;

	}
		//ask if customer wants to confirm reservation and returns boolean.  redirected to main menu if they decline
	private boolean confirmReservation() {
		boolean confirmReservation = false;
		System.out.println();
		System.out.println("Would you like to confirm this reservation?  Y/N");
		System.out.println();
		System.out.println("Y) ");
		System.out.println("N)");
		String choice = menu.getUserInputString().toUpperCase();
		while (!choice.equals("Y") && !choice.equals("N")) {
			System.out.println("Invalid entry.  Please choose Y or N");
			System.out.println();
			System.out.println("Y) ");
			System.out.println("N)");
			choice = menu.getUserInputString().toUpperCase();
		}
		if (choice.equals("Y")){
			confirmReservation = true;
		}
		return confirmReservation;
	}
	
	//creates new reservation
	private Reservation setNewReservation(String reservationName, int siteId, LocalDate fromDate, LocalDate toDate,
			LocalDate createDate) {

		Reservation newReservation = new Reservation();
		newReservation.setCreateDate(createDate);
		newReservation.setName(reservationName);
		newReservation.setSiteId(siteId);
		newReservation.setStartDate(fromDate);
		newReservation.setEndDate(toDate);

		return newReservation;
	}
	
	//add reservation to database after its confirmed
	private void addReservation(Reservation newReservation) {
		reservationDAO.createReservationInDatabase(newReservation);
		int reservationNumber = rand.nextInt();
		reservationMap.put(reservationNumber, newReservation);
	}

	//prints park and full info 
	private void printMoreParkInfo(int selection, Map<Integer, Park> parkmap) {
		Park p = parkMap.get(selection);
		System.out.println(p.getName() + " National Park");
		System.out.println();
		System.out.format("%-20s%-20s\n", "Location: ", p.getLocation());
		System.out.format("%-20s%-20s\n", "Established: ", p.getEstablishDate());
		System.out.format("%-20s%-20s\n", "Area: ", (p.getArea() + " Acres"));
		System.out.format("%-20s%-20s\n", "Annual Visitors: ", p.getVisitors());
		System.out.println();
		System.out.println(p.getDescription());
	}

	//main menu option #2.  displays parks and asks user to decide which.  pick 0 to exit
	private void handleDisplayParksOption() {
		boolean stillUsing = true;
		while (stillUsing) {
		printParks(parkMap);
		System.out.println();
		// choose which park you want info about

		System.out.println();
		int selection = menu.getUserChoiceAsInt();
		while (!parkMap.containsKey(selection) && selection != 0) {
			System.out.println("Sorry, invalid selection.  Please choose another.");
			selection = menu.getUserChoiceAsInt();
		}
		if (parkMap.containsKey(selection)) {
			printMoreParkInfo(selection, parkMap);
			System.out.println();
		}
		else if (selection == 0) {
			stillUsing = false;
		}
		
		}
	}

	private int calculateLengthOfStay(LocalDate desiredStart, LocalDate desiredEnd) {
		int lengthOfStay = (int) ChronoUnit.DAYS.between(desiredStart, desiredEnd);
		return lengthOfStay;
	}

	private BigDecimal calculateTotalCost(int campgroundId, int lengthOfStay) {
		Campground c = campgroundDAO.getCampgroundById(campgroundId);
		BigDecimal dailyCost = c.getDailyfee();
		BigDecimal finalCost = dailyCost.multiply(new BigDecimal(lengthOfStay));
		return finalCost;
	}

	private String printAccessibility(int siteId) {
		String answer = "No";
		Site s = siteDAO.getSiteById(siteId);

		if (s.getAccessible() == true) {
			answer = "Yes";
		}
		return answer;
	}

	private String printUtilities(int siteId) {
		String answer = "No";
		Site s = siteDAO.getSiteById(siteId);

		if (s.hasUtilities() == true) {
			answer = "Yes";
		}
		return answer;
	}

	private void askForDateRange() { 
		System.out.println("When would you like your reservation to start?");
		desiredStart = getDateFromUser();
		// enter end date
		System.out.println("Please enter your desired end date.");
		desiredEnd = getDateFromUser();
		while (desiredEnd.isBefore(desiredStart) || desiredStart.isBefore(LocalDate.now())) {
			if (desiredEnd.isBefore(desiredStart)) {
			System.out.println("Your end date cannot come before the start of your reservation.");
			System.out.println();
		askForDateRange();
			}
			else if (desiredStart.isBefore(LocalDate.now())){
				System.out.println("Your reservation cannot start in the past.");
				System.out.println();
			askForDateRange();
			}
		}
	}


	private Map<Integer, Site> createOpenSiteMap(List<Site> openSiteList) {
		for (int i = 0; i < openSiteList.size(); i++) {
			openSiteMap.put(i + 1, openSiteList.get(i));
		}

		return openSiteMap;
	}
	
	//checks user entered date range against database for availability and prints info
	private void printOpenSites() {
		
		askForDateRange();
		
		List<Site> availableSites = siteDAO.allAvailableSites(campgroundMap.get(campgroundChoice).getCampgroundId(), desiredStart, desiredEnd);
		availableSitesMap = createOpenSiteMap(availableSites);
		while (availableSitesMap.size() == 0) {
			System.out.println("Sorry, there are no sites open during that time.  Please choose another time frame");
			askForDateRange();
			askForDateRange();
			
			availableSites = siteDAO.allAvailableSites(campgroundMap.get(campgroundChoice).getCampgroundId(), desiredStart, desiredEnd);
			availableSitesMap = createOpenSiteMap(availableSites);

		}
		System.out.println("Here are the available sites in that date range");
		System.out.println();
		
		System.out.format("%-10s%-15s%-15s%-15s%-15s%-15s%-15s\n", "", "Site No.", "Max Occupancy", "Accessible",
				"Max RV Length", "Has Utilities", "Total Cost");
		System.out.println();
		for (Integer i : availableSitesMap.keySet()) {
			Site s = availableSitesMap.get(i);
			System.out.format("%-10s%-15s%-15s%-15s%-15s%-15s%-15s\n", i.toString() + ") ", ("# " + s.getSiteNumber()),
					s.getMaxOccupancy(), printAccessibility(s.getSiteId()), s.getMaxRVLength(),
					printUtilities(s.getSiteId()), currency.format(calculateTotalCost(campgroundChoice, calculateLengthOfStay(desiredStart, desiredEnd))));
		}
		System.out.println();
		System.out.println("Choose a campsite");
		System.out.println();

	}
	
	private boolean checkCampsiteChoice(int campsiteChoice) {
		boolean choiceValid = false;
		if (availableSitesMap.containsKey(campsiteChoice)) {
			choiceValid = true;
		}
		return choiceValid;
	}
	

}

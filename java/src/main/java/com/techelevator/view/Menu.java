package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Menu {
	private PrintWriter out;
	private Scanner in;

	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will
			// be null
		}
		if (choice == null) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}

	public void displayParksAsOptions(List<String> options) {
		out.println();

		for (int i = 0; i < options.size(); i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options.get(i));
		}

		out.print("\nPlease choose an option >>> ");
		out.flush();

	}


	public int getUserChoiceAsInt() {
		String userInput = "";
		int userInputInt = -1; 
		while (userInputInt == -1) {
			userInput = in.nextLine();

		try {
			userInputInt = Integer.valueOf(userInput);
		} catch (NumberFormatException e) {
			System.out.println("Invalid selection, please make another choice");
			System.out.println();
			
		}
		}

		return userInputInt;
	}
	
	public String getUserInputString() {
		out.println();
		return in.nextLine();
	}
	
	public LocalDate getDateFromUser() {
		LocalDate date = null;

		while (date == null) {
		System.out.println("Please Enter Date:  MM/DD/YYYY");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		
		String dateString = in.next();
		
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

}

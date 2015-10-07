package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import models.Task;

public class MainParser {
	public static ArrayList<Task> ParseCommand(String input) {
		//StringFinder strFinder = new StringFinder(input);
		//if (strFinder.containsAll("at", "on")
		if (input.startsWith("add")) {
			// add
			ArrayList<String> dates = ParseDates(input);
			String event = input.split("by")[0].trim().replace("add ", "");
			String date = dates.get(0);
			switch (dates.size()) {
			case 1: //Deadline Task
				
				break;
			case 2: //Event
				String event = input.split("at")[0].trim().replace("add ", "");
				String date = dates.get(0);
				String startTime = dates.get(1);
				String endTime = dates.get(2);
				
				
			}
		} else if (input.startsWith("update")) {
			// update
		} else if (input.startsWith("delete")) {
			// delete
		} else if (input.startsWith("search")) {
			// search
		} else {
			// undo
		}
	}

	public static ArrayList<String> ParseDates(String input) {
		List<Date> parsedInput = new PrettyTimeParser().parse(input);
		ArrayList<String> dates = new ArrayList<String>();

		if (!parsedInput.isEmpty()) {
			dates.add(new SimpleDateFormat("EEE, d MMM yyyy").format(parsedInput.get(0)));
			if (parsedInput.size() > 1) {
				for (Date d : parsedInput) {
					dates.add(new SimpleDateFormat("h:mm a").format(d));
				}
			} else if (parsedInput.size() == 1) {
				dates.add(new SimpleDateFormat("h:mm a").format(d));
			}
		}
		return dates;
	}

	public static
}

package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import models.Event;
import models.Task;

public class MainParser {
	public static ArrayList<Task> ParseCommand(String input) {
		//StringFinder strFinder = new StringFinder(input);
		//if (strFinder.containsAll("at", "on")
		String event = "";
		String date = "";

		if (input.startsWith("update")) {
			// update
		} else if (input.startsWith("delete")) {
			// delete
		} else if (input.startsWith("search")) {
			// search
		} else {
			// add
			ArrayList<String> dates = ParseDates(input);
			switch (dates.size()) {
			case 1: //Deadline Task
				event = input.split("by")[0].trim();
				date = dates.get(0);
				break;
			case 2: //Event
				event = input.split("at")[0].trim();
				date = dates.get(0);
				String startTime = dates.get(1);
				String endTime = dates.get(2);
				Event e = new Event(startTime, endTime, event, false);
				break;
			}
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
			}
		}
		return dates;
	}

	//public static
}

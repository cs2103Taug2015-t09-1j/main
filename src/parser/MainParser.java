package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.nlp.*;

public class MainParser {
	public static ArrayList<String> ParseCommand(String input) {
		//StringFinder strFinder = new StringFinder(input);
		//if (strFinder.containsAll("at", "on")
		ArrayList<String> cmdContents = new ArrayList<String>();
		String taskDesc = "";
		String date = "";

		if (input.startsWith("update")) {
			// update
			cmdContents.add("update");
		} else if (input.startsWith("delete")) {
			// delete
			cmdContents.add("delete");
			cmdContents.add(input.split("delete ")[1].trim());
		} else if (input.startsWith("search")) {
			// search
			cmdContents.add("search");
			cmdContents.add(input.split("search ")[1].trim());
		} else {
			// add
			ArrayList<String> dates = ParseDates(input);
			cmdContents.add("add");
			switch (dates.size()) {
			case 1: //Deadline Task
				taskDesc = input.split(" by ")[0].trim();
				cmdContents.add("deadline");
				date = dates.get(0);
				break;
			case 2: //Event
				taskDesc = input.split(" at ")[0].trim();
				date = dates.get(0);
				String startTime = dates.get(1);
				String endTime = dates.get(2);
				cmdContents.add("event");
				cmdContents.add(startTime);
				cmdContents.add(endTime);
				break;
			}

			cmdContents.add(taskDesc);
			cmdContents.add(date);
		}

		return cmdContents;
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
}

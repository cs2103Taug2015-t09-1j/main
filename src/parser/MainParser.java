package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.ParsedObject;
import models.Task;
import models.Commands;
import models.Commands.ADD_TYPE;
import models.Commands.COMMAND_TYPE;

public class MainParser {
	private static MainParser parser = null;
	protected final static int TYPE_SINGLE_DATE_EVENT = 1;
	protected final static int TYPE_DOUBLE_DATE_EVENT = 2;
	protected final static int TYPE_DEADLINE = 3;
	protected final static int TYPE_FLOATING = 4;
	protected final static int TYPE_INVALID = 5;

	private MainParser() { }

	public static MainParser getInstance() {
		if (parser == null) {
			parser = new MainParser();
		}
		return parser;
	}
/*
	public Task test(String input) {
		ParsedObject pObj = new ParsedObject();
		ArrayList<String> commandInfo = new ArrayList<String>();
		List<DateGroup> parsedInput = new PrettyTimeParser().parseSyntax(input);
		pObj.setCommand(input);

		if (input.startsWith("update")) {
			// update
			pObj.setCommandType("update");
		} else if (input.startsWith("delete")) {
			// delete
			pObj.setCommandType("delete");
			commandInfo.add(input.split("delete ")[1].trim());
		} else if (input.startsWith("search")) {
			// search
			pObj.setCommandType("search");
			commandInfo.add(input.split("search ")[1].trim());
		} else {
			// add
			switch (determineTaskType(input)) {
			case SINGLE_DATE_EVENT:
				return new Event(parsedInput.get(0).getDates().get(0), parsedInput.get(0).getDates().get(0), getTaskDesc(input), false);
			case DOUBLE_DATE_EVENT:
				return new Event(parsedInput.get(0).getDates().get(0), parsedInput.get(0).getDates().get(1), getTaskDesc(input), false);
			case DEADLINE_TASK:
				return new DeadlineTask(parsedInput.get(0).getDates().get(0), getTaskDesc(input), false);
				//addTaskDesc(input, commandInfo);
				//addDates(commandInfo, parsedInput);
				//break;
			case FLOATING_TASK:
				return new FloatingTask(getTaskDesc(input), false);
				//pObj.setCommandType("add floating");
				//commandInfo.add(input);
				//break;
			default:
				//pObj.setCommandType("invalid");
			}
			System.out.println(commandInfo);
		}
		return null;
	}*/

	public ParsedObject parseCommand(String input) {
		ParsedObject pObj = new ParsedObject();
		ArrayList<String> commandInfo = new ArrayList<String>();
		List<DateGroup> parsedInput = new PrettyTimeParser().parseSyntax(input);
		pObj.setCommand(input);

		if (input.startsWith("update")) {
			// update
			pObj.setCommandType("update");
		} else if (input.startsWith("delete")) {
			// delete
			pObj.setCommandType("delete");
			commandInfo.add(input.split("delete ")[1].trim());
		} else if (input.startsWith("search")) {
			// search
			pObj.setCommandType("search");
			commandInfo.add(input.split("search ")[1].trim());
		} else {
			// add
			switch (determineTaskType(input, pObj, parsedInput)) {
			case TYPE_SINGLE_DATE_EVENT:
			case TYPE_DOUBLE_DATE_EVENT:
			case TYPE_DEADLINE:
				addTaskDesc(input, commandInfo);
				addDates(commandInfo, parsedInput);
				break;
			case TYPE_FLOATING:
				pObj.setCommandType("add floating");
				commandInfo.add(input);
				break;
			default:
				pObj.setCommandType("invalid");
			}
			System.out.println(commandInfo);
		}

		return pObj;
	}

	private void addDates(ArrayList<String> commandInfo, List<DateGroup> parsedInput) {
		List<Date> dates = parsedInput.get(0).getDates();
		for (int i = 0; i < dates.size(); i++) {
			commandInfo.add(dates.get(i).toString());
		}
	}

	private String getTaskDesc(String input) {
		input = input.replaceAll("\\s?" + new PrettyTimeParser().parseSyntax(input).get(0).getText(), "");
		input = input.toLowerCase().replaceAll("((due by)|between|due|by|from|on|at)\\s\\d*", "");
		input = input.replaceAll("((due by)|between|due|by|from|on|at)$", "");
		input = input.replaceAll("^((due by)|between|due|by|from|on|at)\\s", "");
		return input.trim();
	}

	private void addTaskDesc(String input, ArrayList<String> commandInfo) {
		input = input.replaceAll("\\s?" + new PrettyTimeParser().parseSyntax(input).get(0).getText(), "");
		input = input.toLowerCase().replaceAll("((due by)|between|due|by|from|on|at)\\s\\d", "");
		input = input.replaceAll("((due by)|between|due|by|from|on|at)$", "");
		input = input.replaceAll("^((due by)|between|due|by|from|on|at)\\s", "");
		commandInfo.add(input.trim());
	}

	private int determineTaskType(String input, ParsedObject pObj, List<DateGroup> parsedInput) {
		/*System.out.println("Size: " + parsedInput.size());
		System.out.println("Is Recurring: " + parsedInput.get(0).isRecurring());
		System.out.println("Recurring Interval: " + parsedInput.get(0).getRecurInterval());
		System.out.println("Recurs Until: " + parsedInput.get(0).getRecursUntil());
		System.out.println("List of Dates: " + parsedInput.get(0).getDates());
		System.out.println("Line: " + parsedInput.get(0).getLine());
		System.out.println("Position: " + parsedInput.get(0).getPosition());
		System.out.println("Text: " + parsedInput.get(0).getText());
		input = input.replace(parsedInput.get(0).getText(), "");
		System.out.println("Remove Date/Time: " + input);*/

		if (!parsedInput.isEmpty()) {
			switch (parsedInput.get(0).getDates().size()) {
			case 1:
				if (input.contains("by") || input.contains("due")) {
					System.out.println("Deadline Task");
					pObj.setCommandType("add deadline");
					return TYPE_DEADLINE;
				} else {
					System.out.println("Single Date Event");
					pObj.setCommandType("add single date event");
					return TYPE_SINGLE_DATE_EVENT;
				}
			case 2:
				System.out.println("Double Date Event");
				pObj.setCommandType("add double date event");
				return TYPE_DOUBLE_DATE_EVENT;
			default:
				System.out.println("Invalid");
				return TYPE_INVALID;
			}
		} else {
			System.out.println("Floating Task");
			return TYPE_FLOATING;
			/*datesString.add(new SimpleDateFormat("EEE, d MMM yyyy").format(dates.get(0))); // Date
			datesString.add(new SimpleDateFormat("h:mm a").format(dates.get(0))); // Time
			datesString.add(new SimpleDateFormat("EEE, d MMM yyyy").format(dates.get(0))); // Date
			datesString.add(new SimpleDateFormat("h:mm a").format(dates.get(0))); // Time*/
		}
	}

	public List<DateGroup> getParsedInput(String input) {
		return new PrettyTimeParser().parseSyntax(input);
	}

	public COMMAND_TYPE determineCommandType(String input) {
		if (input.startsWith("update")) {
			// update
			return COMMAND_TYPE.UPDATE;
		} else if (input.startsWith("delete")) {
			// delete
			return COMMAND_TYPE.DELETE;
		} else if (input.startsWith("search")) {
			// search
			return COMMAND_TYPE.SEARCH;
		} else {
			// add
			return COMMAND_TYPE.ADD;
		}
	}

	public Task getTask(String input) {
		List<DateGroup> parsedInput = new PrettyTimeParser().parseSyntax(input);
		if (!parsedInput.isEmpty()) {
			switch (parsedInput.get(0).getDates().size()) {
			case 1:
				if (input.contains("by") || input.contains("due")) {
					System.out.println("Deadline Task");
					return new DeadlineTask(parsedInput.get(0).getDates().get(0), getTaskDesc(input), false);
				} else {
					System.out.println("Single Date Event");
					return new Event(parsedInput.get(0).getDates().get(0), parsedInput.get(0).getDates().get(0), getTaskDesc(input), false);
				}
			case 2:
				System.out.println("Double Date Event");
				return new Event(parsedInput.get(0).getDates().get(0), parsedInput.get(0).getDates().get(0), getTaskDesc(input), false);
			default:
				System.out.println("Invalid");
				return null;
			}
		} else {
			System.out.println("Floating Task");
			return new FloatingTask(getTaskDesc(input), false);
		}
	}
}

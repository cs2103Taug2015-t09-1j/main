package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.ParsedObject;
import models.Task;
import models.Commands.*;

public class MainParser {
	private static MainParser parser = null;
	private final PrettyTimeParser ptParser = new PrettyTimeParser();
	private String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m"};
	private String[] deleteCmdList = {"delete", "del", "/d", "remove", "rm", "/r"};
	private String[] searchCmdList = {"search", "/s", "find", "/f"};
	private String[] displayCmdList = {"display", "/dp", "show", "/sw"};

	private MainParser() {}

	public static MainParser getInstance() {
		if (parser == null) {
			parser = new MainParser();
		}
		return parser;
	}

	public COMMAND_TYPE determineCommandType(String input) {
		if (isCommand(input, updateCmdList)) {
			return COMMAND_TYPE.UPDATE;
		} else if (isCommand(input, deleteCmdList)) {
			return COMMAND_TYPE.DELETE;
		} else if (isCommand(input, searchCmdList)) {
			return COMMAND_TYPE.SEARCH;
		} else if (isCommand(input, displayCmdList)) {
			return COMMAND_TYPE.DISPLAY;
		} else {
			return COMMAND_TYPE.ADD;
		}
	}

	private boolean isCommand(String input, String[] commandList) {
		for(int i = 0; i < commandList.length; i++) {
			if (input.startsWith(commandList[i])) {
				return true;
			}
		}
		return false;
	}

	private String removeCommandWord(String input, String[] commandList) {
		for(int i = 0; i < commandList.length; i++) {
			if (input.startsWith(commandList[i])) {
				input = input.split(commandList[i])[1];
			}
		}
		return input;
	}

	public List<DateGroup> parseDates(String input) {
		return ptParser.parseSyntax(input);
	}

	private String getTaskDesc(String input) {
		String test = input.split("\\+")[1];
		String date = new PrettyTimeParser().parseSyntax(test).get(0).getText();
		input = input.toLowerCase().replaceAll("((due by)|due|by|before|from|on|at)\\s*" + date, "");
		input = input.replaceAll("((due by)|due|by|from|on|at)\\s*$", "");
		input = input.replaceAll("^\\s*((due by)|due|by|from|on|at)", "");
		input = input.replaceAll("^\\s+|\\s+$", "");
		return input.trim();
	}

	public ParsedObject getAddParsedObject(String input) {
		List<DateGroup> parsedInput = parseDates(input);
		Vector<Task> tasks = new Vector<Task>();

		if (!parsedInput.isEmpty()) {
			switch (parsedInput.get(0).getDates().size()) {
			case 1:
				if (input.contains("by") || input.contains("due") || input.contains("before")) {
					// Deadline Task
					tasks.add(new DeadlineTask(parsedInput.get(0).getDates().get(0), getTaskDesc(input), false));
					return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DEADLINE_TASK, tasks);
				} else {
					// Single Date Event
					tasks.add(new Event(parsedInput.get(0).getDates().get(0), parsedInput.get(0).getDates().get(0), getTaskDesc(input), false));
					return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.SINGLE_DATE_EVENT, tasks);
				}
			case 2:
				// Double Date Event
				tasks.add(new Event(parsedInput.get(0).getDates().get(0), parsedInput.get(0).getDates().get(1), getTaskDesc(input), false));
				return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DOUBLE_DATE_EVENT, tasks);
			default:
				// Invalid
				return new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		} else {
			// Floating Task
			tasks.add(new FloatingTask(getTaskDesc(input), false));
			return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.FLOATING_TASK, tasks);
		}
	}

	public String[] getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		if (cmdType == COMMAND_TYPE.SEARCH || cmdType == COMMAND_TYPE.DISPLAY) {
			pattern = "\\.+|,+|:+|;+|/+|\\\\+|\\|+";
		} else {
			pattern = "\\s+|\\.+|,+|:+|;+|/+|\\\\+|\\|+";
		}

		String[] paramArray = input.split(pattern);
		for (int i = 0; i < paramArray.length; i++) {
			paramArray[i] = paramArray[i].replaceAll("^\\s+|\\s+$", "");
		}

		return paramArray;
	}

	public ParsedObject getDeleteParsedObject(String input) {
		String params = removeCommandWord(input, deleteCmdList);

		Vector<Integer> taskIDs = new Vector<Integer>();
		String[] taskIDArray = getCommandParameters(params, COMMAND_TYPE.DELETE);
		for (String idStr : taskIDArray) {
			if (!idStr.trim().isEmpty()) {
				try {
					int taskID = Integer.parseInt(idStr);
					taskIDs.add(taskID);
				} catch (NumberFormatException e) {
					System.out.println("Error");
					// Catch exception and continue parsing the rest
				}
			}
		}

		return new ParsedObject(COMMAND_TYPE.DELETE, null, taskIDs);
	}

	public ParsedObject getDisplayParsedObject(String input) {
		String params = removeCommandWord(input, displayCmdList);

		Vector<String> columns = new Vector<String>();
		String[] colArray = getCommandParameters(params, COMMAND_TYPE.DISPLAY);
		for (String colName : colArray) {
			if (!colName.trim().isEmpty()) {
				columns.add(colName);
			}
		}

		return new ParsedObject(COMMAND_TYPE.DISPLAY, null, columns);
	}

	public ParsedObject getSearchParsedObject(String input) {
		String params = removeCommandWord(input, searchCmdList);

		Vector<String> searchTerms = new Vector<String>();
		String[] termArray = getCommandParameters(params, COMMAND_TYPE.SEARCH);
		for (String term : termArray) {
			if (!term.trim().isEmpty()) {
				searchTerms.add(term);
			}
		}

		return new ParsedObject(COMMAND_TYPE.SEARCH, null, searchTerms);
	}

	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, searchCmdList);

		Vector<String> searchTerms = new Vector<String>();
		String[] termArray = getCommandParameters(params, COMMAND_TYPE.SEARCH);
		for (String term : termArray) {
			if (!term.trim().isEmpty()) {
				searchTerms.add(term);
			}
		}

		return new ParsedObject(COMMAND_TYPE.SEARCH, null, searchTerms);
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
	}

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

	private int determineTaskType(String input, ParsedObject pObj, List<DateGroup> parsedInput) {
		System.out.println("Size: " + parsedInput.size());
		System.out.println("Is Recurring: " + parsedInput.get(0).isRecurring());
		System.out.println("Recurring Interval: " + parsedInput.get(0).getRecurInterval());
		System.out.println("Recurs Until: " + parsedInput.get(0).getRecursUntil());
		System.out.println("List of Dates: " + parsedInput.get(0).getDates());
		System.out.println("Line: " + parsedInput.get(0).getLine());
		System.out.println("Position: " + parsedInput.get(0).getPosition());
		System.out.println("Text: " + parsedInput.get(0).getText());
		input = input.replace(parsedInput.get(0).getText(), "");
		System.out.println("Remove Date/Time: " + input);

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

			datesString.add(new SimpleDateFormat("EEE, d MMM yyyy").format(dates.get(0))); // Date
			datesString.add(new SimpleDateFormat("h:mm a").format(dates.get(0))); // Time
			datesString.add(new SimpleDateFormat("EEE, d MMM yyyy").format(dates.get(0))); // Date
			datesString.add(new SimpleDateFormat("h:mm a").format(dates.get(0))); // Time
		}
	}*/
}

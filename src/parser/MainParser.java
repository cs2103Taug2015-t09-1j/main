package parser;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Arrays;

import logic.Logic;
import models.Deadline;
import models.Event;
import models.Todo;
import models.ParsedObject;
import models.Task;
import storage.Storage;
import models.Commands.*;

public class MainParser {
	private static MainParser parser = null;
	private final PrettyTimeParser ptParser = new PrettyTimeParser();
	private final Logger logger = Logger.getLogger(MainParser.class.getName());
	private String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m"};
	private String[] deleteCmdList = {"delete", "del", "/d", "remove", "rm", "/r"};
	private String[] searchCmdList = {"search", "/s", "find", "/f"};
	private String[] displayCmdList = {"display", "/dp", "show", "/sw"};
	private String[] doneCmdList = {"is done", "done"};

	private MainParser() {}

	public static MainParser getInstance() {
		if (parser == null) {
			parser = new MainParser();
		}
		return parser;
	}

	public COMMAND_TYPE determineCommandType(String input) {
		if (input.trim().isEmpty()) {
			return COMMAND_TYPE.INVALID;
		} else {
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
				input = input.split("^\\s*" + commandList[i] + "\\s*")[1];
			}
		}
		return input;
	}

	public String formatDate(Date d, String format) {
		return new SimpleDateFormat(format).format(d);
	}

	public List<Date> parseDates(String input) {
		if (input.contains("+")) {
			input = input.split("\\+")[1];
		}
		return ptParser.parseSyntax(input).get(0).getDates();
	}

	private String getTaskDesc(String input) {
		List<DateGroup> temp = ptParser.parseSyntax(input);
		String date = temp.get(0).getText();
		input = input.toLowerCase().replaceAll("\\++((due by)|due|by|before|from|on|at)\\s*" + date, "");
		input = input.replaceAll("\\s*" + date + "\\s*", "");
		input = input.replaceAll("\\s+((due by)|due|by|from|on|at)\\s*((due by)|due|by|from|on|at)*\\s*$", "");
		input = input.replaceAll("^\\s*((due by)|due|by|from|on|at)\\s*((due by)|due|by|from|on|at)", "");
		input = input.replaceAll("^\\s+|\\s+$", "");
		return input.trim();
	}

	public ParsedObject getAddParsedObject(String input) {
		List<Date> parsedInput = parseDates(input);
		ArrayList<Task> tasks = new ArrayList<Task>();

		if (!parsedInput.isEmpty()) {
			switch (parsedInput.size()) {
				case 1:
					if (input.contains("by") || input.contains("due") || input.contains("before")) {
						// Deadline Task
						Date deadlineDate = setTime(parsedInput.get(0), 23, 59, 59, 999);
						tasks.add(new Deadline(deadlineDate, getTaskDesc(input), false));
						return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DEADLINE, tasks);
					} else {
						// Single Date Event
						tasks.add(new Event(parsedInput.get(0), parsedInput.get(0), getTaskDesc(input), false));
						return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.SINGLE_DATE_EVENT, tasks);
					}
				case 2:
					// Double Date Event
					tasks.add(new Event(parsedInput.get(0), parsedInput.get(1), getTaskDesc(input), false));
					return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DOUBLE_DATE_EVENT, tasks);
				default:
					// Invalid
					return new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		} else {
			// Floating Task
			tasks.add(new Todo(input.trim(), false));
			return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.TODO, tasks);
		}
	}

	private Date setTime(Date d, int hours, int minutes, int seconds, int milliseconds) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.set(Calendar.HOUR_OF_DAY, hours);
		cal.set(Calendar.MINUTE, minutes);
		cal.set(Calendar.SECOND, seconds);
		cal.set(Calendar.MILLISECOND, milliseconds);
		return cal.getTime();
	}

	public ArrayList<String> getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		String[] paramArray;
		/*if (cmdType == COMMAND_TYPE.SEARCH || cmdType == COMMAND_TYPE.DISPLAY) {
			pattern = "\\.+|,+|:+|;+|/+|\\\\+|\\|+";
		} else {
			if (input.contains("to") || input.contains("-")) {
				pattern = "\\-+|to";
			} else {
				pattern = "\\s+|\\.+|,+|:+|;+|/+|\\\\+|\\|+";
			}
		}*/
		switch (cmdType) {
		case SEARCH:
		case DISPLAY:
			pattern = "\\.+|,+|:+|;+|/+|\\\\+|\\|+";
			paramArray = input.split(pattern);
			break;
		case DELETE:
			if (input.contains("to") || input.contains("-")) {
				pattern = "\\-+|to";
			} else {
				pattern = "\\s+|\\.+|,+|:+|;+|/+|\\\\+|\\|+";
			}
			paramArray = input.split(pattern);
			break;
		case UPDATE:
			paramArray = input.split("\\s+", 3);
			break;
		default:
			pattern = "\\s+";
			paramArray = input.split(pattern);
		}

		ArrayList<String> paramList = new ArrayList<String>(Arrays.asList(paramArray));

		for (int i = 0; i < paramList.size(); i++) {
			paramList.set(i, paramList.get(i).replaceAll("^\\s+|\\s+$", ""));
		}

		return paramList;
	}

	public ParsedObject getDeleteParsedObject(String input) {
		String params = removeCommandWord(input, deleteCmdList);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);

		if (input.contains("to") || input.contains("-")) {
			int fromID = parseInteger(taskIDList.get(0));
			int toID = parseInteger(taskIDList.get(1));

			for (int i = fromID; i < toID; i++) {
				if (Storage.getTaskByID(i) != null) {
					taskIDs.add(i);
				}
			}
		} else {
			taskIDs.add(parseInteger(taskIDList.get(0)));
		}

		return new ParsedObject(COMMAND_TYPE.DELETE, null, taskIDs);
	}
/*
	public ParsedObject getIsDoneParsedObject(String input) {
		String params = removeCommandWord(input, deleteCmdList);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);

		if (input.contains("to") || input.contains("-")) {
			int fromID = parseInteger(taskIDList.get(0));
			int toID = parseInteger(taskIDList.get(1));

			for (int i = fromID; i < toID; i++) {
				if (Storage.getTaskByID(i) != null) {
					taskIDs.add(i);
				}
			}
		} else {
			taskIDs.add(parseInteger(taskIDList.get(0)));
		}

		return new ParsedObject(COMMAND_TYPE.DELETE, null, taskIDs);
	}
*/
	public ParsedObject getDisplayParsedObject(String input) {
		String params = removeCommandWord(input, displayCmdList);

		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> colArray = getCommandParameters(params, COMMAND_TYPE.DISPLAY);
		for (String colName : colArray) {
			if (!colName.trim().isEmpty()) {
				columns.add(colName);
			}
		}

		return new ParsedObject(COMMAND_TYPE.DISPLAY, null, columns);
	}

	public ParsedObject getSearchParsedObject(String input) {
		String params = removeCommandWord(input, searchCmdList);

		ArrayList<String> searchTerms = new ArrayList<String>();
		ArrayList<String> termArray = getCommandParameters(params, COMMAND_TYPE.SEARCH);
		for (String term : termArray) {
			if (!term.trim().isEmpty()) {
				searchTerms.add(term);
			}
		}

		return new ParsedObject(COMMAND_TYPE.SEARCH, null, searchTerms);
	}

	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, updateCmdList);
		ArrayList<String> paramsList = getCommandParameters(params, COMMAND_TYPE.UPDATE);
		return new ParsedObject(COMMAND_TYPE.UPDATE, null, paramsList);
	}

	public int parseInteger(String intString) {
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			//throw new NumberFormatException();
		}
		return 0;
	}
}

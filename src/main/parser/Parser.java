package main.parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Arrays;

import main.model.EnumTypes.COMMAND_TYPE;
import main.model.EnumTypes.TASK_TYPE;
import main.model.ParsedObject;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;

public class Parser {
	private static Parser parser = null;
	private final PrettyTimeParser ptParser = new PrettyTimeParser();
	private final Logger logger = Logger.getLogger(Parser.class.getName());
	private String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m"};
	private String[] deleteCmdList = {"delete", "del", "/d", "remove", "rm", "/r"};
	private String[] doneCmdList = {"done", "complete"};
	private String[] undoneCmdList = {"!done", "undone", "incomplete"};
	private String[] undoCmdList = {"undo", "/un"};
	private String[] redoCmdList = {"redo", "/re"};
	private String[] exitCmdList = {"exit", "/e", "quit", "/q"};
	private String[] displayCmdList = {"display", "show", "/sh", "view", "/v"};

	private final String UPDATE_REGEX = "^\\d+\\s+\\d+\\s+(\\w*|\\d*|\\s*)+";
	private final String DELETE_REGEX = "^\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)";
	private final String DISPLAY_REGEX = "^(\\w|\\d|\\s)+";
	private final String DONE_UNDONE_REGEX= "^\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)";
	private final String UNDO_REDO_REGEX = "^\\d+\\s*$";

	private Parser() {}

	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
		}
		return parser;
	}

	public COMMAND_TYPE determineCommandType(String input) {
		if (input.trim().isEmpty()) {
			return COMMAND_TYPE.INVALID;
		}

		if (isCommand(input, updateCmdList)) {
			if (hasValidParameters(removeCommandWord(input, updateCmdList), UPDATE_REGEX)) {
				return COMMAND_TYPE.UPDATE;
			}
		} else if (isCommand(input, deleteCmdList)) {
			if (hasValidParameters(removeCommandWord(input, deleteCmdList), DELETE_REGEX)) {
				return COMMAND_TYPE.DELETE;
			}
		} else if (isCommand(input, displayCmdList)) {
			if (hasValidParameters(removeCommandWord(input, displayCmdList), DISPLAY_REGEX)) {
				return COMMAND_TYPE.DISPLAY;
			}
		} else if (isCommand(input, doneCmdList)) {
			if (hasValidParameters(removeCommandWord(input, doneCmdList), DONE_UNDONE_REGEX)) {
				return COMMAND_TYPE.DONE;
			}
		} else if (isCommand(input, undoneCmdList)) {
			if (hasValidParameters(removeCommandWord(input, undoneCmdList), DONE_UNDONE_REGEX)) {
				return COMMAND_TYPE.UNDONE;
			}
		} else if (isCommand(input, undoCmdList)) {
			input = removeCommandWord(input, undoCmdList);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.isEmpty()) {
				return COMMAND_TYPE.UNDO;
			}
		} else if (isCommand(input, redoCmdList)) {
			input = removeCommandWord(input, redoCmdList);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.isEmpty()) {
				return COMMAND_TYPE.REDO;
			}
		} else if (isCommand(input, exitCmdList)) {
			if (removeCommandWord(input, exitCmdList).isEmpty()) {
				return COMMAND_TYPE.EXIT;
			}
		} else {
			return COMMAND_TYPE.ADD;
		}

		return COMMAND_TYPE.INVALID;
	}

	private boolean isCommand(String input, String[] commandList) {
		for(int i = 0; i < commandList.length; i++) {
			Pattern pattern = Pattern.compile("(?ui)^" + commandList[i] + "\\s*");
			Matcher matcher = pattern.matcher(input);
	        if (matcher.find()) {
        		return true;
	        }
		}
		return false;
	}

	private boolean hasValidParameters(String input, String regex) {
		Pattern pattern = Pattern.compile("(?ui)" + regex);
		Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
        	return true;
        }
		return false;
	}

	private String removeCommandWord(String input, String[] commandList) {
		String regexStr;
		for(int i = 0; i < commandList.length; i++) {
			regexStr = "(?ui)^" + commandList[i] + "\\s*";
			Pattern pattern = Pattern.compile("(?ui)^" + commandList[i] + "\\s*");
			Matcher matcher = pattern.matcher(input);
			if (matcher.find()) {
				return input.replaceAll(regexStr, "");
			}
		}
		return null;
	}

	public String formatDate(Date d, String format) {
		return new SimpleDateFormat(format).format(d);
	}

	public List<Date> getDateList(String input) {
		String[] keywords = {"yesterday", "today", "tomorrow", "day", "month", "year", "week", "before", "after", "next", "last", "ago"};
		List<DateGroup> dGroup = parseDates(input);
		List<Date> dates = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		if (dGroup != null) {
			switch (dGroup.size()) {
				case 1:
					return dGroup.get(0).getDates();
				case 2:
					Calendar tempDate = Calendar.getInstance();
					Calendar tempTime = Calendar.getInstance();
					for (int i = 0; i < keywords.length; i++) {
						if (dGroup.get(0).getText().contains(keywords[i])) {
							tempDate.setTime(dGroup.get(0).getDates().get(0));
							tempTime.setTime(dGroup.get(1).getDates().get(0));
							break;
						} else if (dGroup.get(1).getText().contains(keywords[i])) {
							tempDate.setTime(dGroup.get(1).getDates().get(0));
							tempTime.setTime(dGroup.get(0).getDates().get(0));
							break;
						}
					}
					cal.set(tempDate.get(Calendar.YEAR),
							tempDate.get(Calendar.MONTH),
							tempDate.get(Calendar.DATE),
							tempTime.get(Calendar.HOUR_OF_DAY),
							tempTime.get(Calendar.MINUTE),
							tempTime.get(Calendar.SECOND));
					dates.add(cal.getTime());
					return dates;
				default:
					return null;
			}
		} else {
			return null;
		}
	}

	public List<DateGroup> parseDates(String input) {
		List<DateGroup> dGroup = ptParser.parseSyntax(input);
		if (!dGroup.isEmpty()) {
			return dGroup;
		} else {
			return null;
		}
	}

	private String getTaskDesc(String input) {
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			input = matcher.group().replace("\"", "");
		} else {
			List<DateGroup> dateGroup = ptParser.parseSyntax(input);
			for (int i = 0; i < dateGroup.size(); i++) {
				String date = dateGroup.get(i).getText();
				input = input.replaceAll("(?ui)\\s*((due on)|(due by)|due|by|before|from|on|at)\\s*" + date, "");
				input = input.replaceAll("(?ui)\\s*" + date + "\\s*", " ");
			}
			input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|from|on|at)\\s*((due on)|(due by)|due|by|from|on|at)*\\s*$", "");
			input = input.replaceAll("^(?ui)\\s*((due on)|(due by)|due|by|from|on|at)\\s*((due on)|(due by)|due|by|from|on|at)", "");
		}

		input = input.replaceAll("^\\s+|\\s+$", "");
		return input;
	}

	public ArrayList<String> getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		String[] paramArray;

		switch (cmdType) {
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

	public ParsedObject getDisplayParsedObject(String input) {
		ParsedObject obj;
		input = removeCommandWord(input, displayCmdList);
		List<Date> parsedInput = getDateList(input);
		if (parsedInput == null) {
			if (input.matches("(?ui)^\\s*all\\s*$")) {
				obj = new ParsedObject(COMMAND_TYPE.DISPLAY_ALL, null, null);
			} else {
				obj = new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		} else {
			ArrayList<Date> dates = new ArrayList<Date>(parsedInput);
			switch (dates.size()) {
				case 1:
					obj = new ParsedObject(COMMAND_TYPE.DISPLAY_ON, null, dates);
					break;
				case 2:
					obj = new ParsedObject(COMMAND_TYPE.DISPLAY_BETWEEN, null, dates);
					break;
				default:
					obj = new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		}

		return obj;
	}

	public ParsedObject getAddParsedObject(String input) {
		List<Date> parsedInput = getDateList(input);
		ArrayList<Task> tasks = new ArrayList<Task>();
		ParsedObject obj;
		if (parsedInput != null) {
			switch (parsedInput.size()) {
				case 1:
					if (input.contains("by") || input.contains("due") || input.contains("before")) {
						tasks.add(new Deadline(parsedInput.get(0), getTaskDesc(input), false));
						obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DEADLINE, tasks);
					} else {
						tasks.add(new Event(parsedInput.get(0), parsedInput.get(0), getTaskDesc(input), false));
						obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.SINGLE_DATE_EVENT, tasks);
					}
					break;
				case 2:
					tasks.add(new Event(parsedInput.get(0), parsedInput.get(1), getTaskDesc(input), false));
					obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DOUBLE_DATE_EVENT, tasks);
					break;
				default:
					obj = new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		} else {
			tasks.add(new Todo(input.trim(), false));
			obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.TODO, tasks);
		}
		return obj;
	}

	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, updateCmdList);
		ArrayList<String> paramsList = getCommandParameters(params, COMMAND_TYPE.UPDATE);
		ParsedObject obj = new ParsedObject(COMMAND_TYPE.UPDATE, null, paramsList);
		return obj;
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

	public ParsedObject getDeleteParsedObject(String input) {
		String params = removeCommandWord(input, deleteCmdList);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);
		ParsedObject obj;

		if (input.contains("to") || input.contains("-")) {
			int fromID;
			int toID;
			try {
				fromID = parseInteger(taskIDList.get(0));
				toID = parseInteger(taskIDList.get(1));
			} catch (Exception e) {
				fromID = 0;
				toID = 0;
			}

			for (int i = fromID; i <= toID; i++) {
				taskIDs.add(i);
			}
		} else if (input.matches("(?ui)^\\s*all\\s*$")) {
			taskIDs.add(-1);
		} else {
			for (int i = 0; i < taskIDList.size(); i++) {
				try {
					taskIDs.add(parseInteger(taskIDList.get(i)));
				} catch (Exception e) {
					// Not number exception
				}

			}
		}
		obj = new ParsedObject(COMMAND_TYPE.DELETE, null, taskIDs);
		return obj;
	}

	public ParsedObject getChangeStatusParsedObject(String input, boolean newStatus) {
		String params;
		if (newStatus) {
			params = removeCommandWord(input, doneCmdList);
		} else {
			params = removeCommandWord(input, undoneCmdList);
		}
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);
		ParsedObject obj;

		if (input.contains("to") || input.contains("-")) {
			int fromID;
			int toID;
			try {
				fromID = parseInteger(taskIDList.get(0));
				toID = parseInteger(taskIDList.get(1));
			} catch (Exception e) {
				fromID = 0;
				toID = 0;
			}

			for (int i = fromID; i <= toID; i++) {
				taskIDs.add(i);
			}
		} else {
			for (int i = 0; i < taskIDList.size(); i++) {
				try {
					taskIDs.add(parseInteger(taskIDList.get(i)));
				} catch (Exception e) {
					// Not number exception
				}

			}
		}
		obj = new ParsedObject(newStatus ? COMMAND_TYPE.DONE : COMMAND_TYPE.UNDONE, null, taskIDs);
		return obj;
	}


	public ParsedObject getUndoParsedObject(String input) {
		String params = removeCommandWord(input, undoCmdList);
		ArrayList<Integer> numOfExec = new ArrayList<Integer>();
		if (params.isEmpty()) {
			numOfExec.add(1);
		} else {
			numOfExec.add(parseInteger(params));
		}

		return new ParsedObject(COMMAND_TYPE.UNDO, null, numOfExec);
	}

	public ParsedObject getRedoParsedObject(String input) {
		String params = removeCommandWord(input, redoCmdList);
		ArrayList<Integer> numOfExec = new ArrayList<Integer>();
		if (params.isEmpty()) {
			numOfExec.add(1);
		} else {
			numOfExec.add(parseInteger(params));
		}

		return new ParsedObject(COMMAND_TYPE.REDO, null, numOfExec);
	}
}

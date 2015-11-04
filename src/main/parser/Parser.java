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

	public List<Date> parseDateGroups(String input) {
		String timeRegexPattern = "(((\\d+\\s+(minutes|min|seconds|sec|hours))|[0-9](am|pm|a.m.|p.m.)|1[0-2](am|pm|a.m.|p.m.))|(0[0-9]|1[0-9]|2[0-3])\\:?([0-5][0-9]))\\:?([0-5][0-9])?(am|pm|a.m.|p.m.|h|\\shours)?";
		Pattern timePattern = Pattern.compile("(?ui)" + timeRegexPattern);
		input = input.replaceAll("until", "till");
		List<DateGroup> dGroups = getDateGroups(input);
		List<Date> dates = new ArrayList<Date>();
		Calendar temp1 = Calendar.getInstance();
		Calendar temp2 = Calendar.getInstance();
		Calendar temp3 = Calendar.getInstance();
		DateGroup firstGroup;
		DateGroup secondGroup;
		DateGroup thirdGroup;
		List<Date> firstGroupDates;
		List<Date> secondGroupDates;
		List<Date> thirdGroupDates;
		Matcher matcher1;
		Matcher matcher2;
		Matcher matcher3;

		if (dGroups != null) {
			switch (dGroups.size()) {
				case 1:
					// lunch with john at 1pm tomorrow
					// lunch in 5 minutes with john
					firstGroup = dGroups.get(0);
					firstGroupDates = firstGroup.getDates();
					matcher1 = timePattern.matcher(firstGroup.getText());
					if (matcher1.find()) {
						return firstGroupDates;
					} else {
						if (firstGroupDates.size() == 1) {
							temp1.setTime(firstGroupDates.get(0));
							setDateTime(temp1, -1, -1, -1, 0, 0, 0);
							dates.add(temp1.getTime());
						} else {
							for (int i = 0; i < firstGroupDates.size(); i++) {
								dates.add(firstGroupDates.get(i));
							}
						}
					}
					break;
				case 2:
					// at 1pm lunch with john tomorrow
					// tomorrow lunch with john at 1pm
					// lunch from 1pm with john to 2pm tomorrow
					// lunch from 1pm tomorrow with john to 2pm
					// lunch from 1pm tomorrow with john to 2pm tomorrow
					// sleep from 1pm to 2pm tomorrow
					firstGroup = dGroups.get(0);
					secondGroup = dGroups.get(1);
					firstGroupDates = firstGroup.getDates();
					secondGroupDates = secondGroup.getDates();

					matcher1 = timePattern.matcher(firstGroup.getText());
					matcher2 = timePattern.matcher(secondGroup.getText());

					if (firstGroupDates.size() == 1 && secondGroupDates.size() == 1) {
						temp1.setTime(firstGroupDates.get(0));
						temp2.setTime(secondGroupDates.get(0));
						if (matcher1.find() && !matcher2.find()) {
							setDateTime(temp1, temp2.get(Calendar.YEAR), temp2.get(Calendar.MONTH), temp2.get(Calendar.DATE), -1, -1, -1);
							dates.add(temp1.getTime());
						} else if (!matcher1.find() && matcher2.find()) {
							setDateTime(temp2, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);
							dates.add(temp2.getTime());
						} else {
							if (temp1.compareTo(temp2) < 0) {
								dates.add(temp1.getTime());
								dates.add(temp2.getTime());
							} else if (temp1.compareTo(temp2) > 0){
								if (temp1.get(Calendar.HOUR_OF_DAY) < temp2.get(Calendar.HOUR_OF_DAY)) {
									setDateTime(temp2, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);
								}
								dates.add(temp1.getTime());
								dates.add(temp2.getTime());
							} else {
								dates.add(temp1.getTime());
							}
						}
					} else {
						// lunch from 1 to 2pm with john tomorrow
						// from 1 to 2pm lunch tomorrow with john
						if (firstGroupDates.size() == 2 && secondGroupDates.size() == 1) {
							temp1.setTime(firstGroupDates.get(0));
							temp2.setTime(firstGroupDates.get(1));
							temp3.setTime(secondGroupDates.get(0));
						} else if (firstGroupDates.size() == 1 && secondGroupDates.size() == 2) {
							// lunch tomorrow with john from 1 to 2pm
							matcher1 = timePattern.matcher(secondGroup.getText());
							matcher2 = timePattern.matcher(firstGroup.getText());
							temp1.setTime(secondGroupDates.get(0));
							temp2.setTime(secondGroupDates.get(1));
							temp3.setTime(firstGroupDates.get(0));
						}

						if (matcher1.find() && !matcher2.find()) {
							setDateTime(temp1, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);
							setDateTime(temp2, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);

							if (temp1.compareTo(temp2) < 0) {
								dates.add(temp1.getTime());
								dates.add(temp2.getTime());
							} else if (temp1.compareTo(temp2) > 0){
								dates.add(temp2.getTime());
								dates.add(temp1.getTime());
							} else {
								dates.add(temp1.getTime());
							}
						}
					}
					break;
				case 3:
					// next saturday lunch from 1pm with john to 2pm
					// 1pm lunch to 2pm with john next saturday
					// 11-11 sleep from 1pm at home to 2pm
					firstGroup = dGroups.get(0);
					secondGroup = dGroups.get(1);
					thirdGroup = dGroups.get(2);
					firstGroupDates = firstGroup.getDates();
					secondGroupDates = secondGroup.getDates();
					thirdGroupDates = thirdGroup.getDates();

					matcher1 = timePattern.matcher(firstGroup.getText());
					matcher2 = timePattern.matcher(secondGroup.getText());
					matcher3 = timePattern.matcher(thirdGroup.getText());
					temp1.setTime(firstGroupDates.get(0));
					temp2.setTime(secondGroupDates.get(0));
					temp3.setTime(thirdGroupDates.get(0));

					if (matcher1.find() && matcher2.find() && !matcher3.find()) {
						setDateTime(temp1, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);
						setDateTime(temp2, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);

						if (temp1.compareTo(temp2) < 0) {
							dates.add(temp1.getTime());
							dates.add(temp2.getTime());
						} else if (temp1.compareTo(temp2) > 0){
							dates.add(temp2.getTime());
							dates.add(temp1.getTime());
						} else {
							dates.add(temp1.getTime());
						}
					} else if (matcher1.find() && !matcher2.find() && matcher3.find()) {
						setDateTime(temp1, temp2.get(Calendar.YEAR), temp2.get(Calendar.MONTH), temp2.get(Calendar.DATE), -1, -1, -1);
						setDateTime(temp3, temp2.get(Calendar.YEAR), temp2.get(Calendar.MONTH), temp2.get(Calendar.DATE), -1, -1, -1);

						if (temp1.compareTo(temp3) < 0) {
							dates.add(temp1.getTime());
							dates.add(temp3.getTime());
						} else if (temp1.compareTo(temp3) > 0){
							dates.add(temp3.getTime());
							dates.add(temp1.getTime());
						} else {
							dates.add(temp1.getTime());
						}
					} else {
						setDateTime(temp2, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);
						setDateTime(temp3, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);

						if (temp2.compareTo(temp3) < 0) {
							dates.add(temp2.getTime());
							dates.add(temp3.getTime());
						} else if (temp2.compareTo(temp3) > 0){
							dates.add(temp3.getTime());
							dates.add(temp2.getTime());
						} else {
							dates.add(temp2.getTime());
						}
					}
					break;
				default:
					return null;
			}
			return dates;
		} else {
			return null;
		}
	}

	public List<DateGroup> getDateGroups(String input) {
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
		String date = "";
		if (matcher.find()) {
			input = matcher.group().replace("\"", "");
		} else {
			List<DateGroup> dateGroup = ptParser.parseSyntax(input);
			for (int i = 0; i < dateGroup.size(); i++) {
				date = dateGroup.get(i).getText();
				input = input.replaceAll("(?ui)\\s*((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s*" + date, "");
				input = input.replaceAll("(?ui)\\s*" + date + "\\s*", " ");
			}
			//Pattern splitPattern = Pattern.compile("\\s+((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s+");
			//String[] excessWords = splitPattern.split(date);
			Pattern splitPattern = Pattern.compile("\\s");
			String[] excessWords = splitPattern.split(date);
			for (String word : excessWords) {
				input = input.replaceAll(word, "");
			}
			input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)*\\s*$", "");
			input = input.replaceAll("^(?ui)\\s*((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)", "");
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
		List<Date> parsedInput = parseDateGroups(input);
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
		List<Date> parsedInput = parseDateGroups(input);
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

	public Calendar setDateTime(Calendar cal, int year, int month, int date, int hours, int minutes, int seconds) {
		if (year != -1) {
			cal.set(Calendar.YEAR, year);
		}
		if (month != -1) {
			cal.set(Calendar.MONTH, month);
		}
		if (date != -1) {
			cal.set(Calendar.DATE, date);
		}
		if (hours != -1) {
			cal.set(Calendar.HOUR_OF_DAY, hours);
		}
		if (minutes != -1) {
			cal.set(Calendar.MINUTE, minutes);
		}
		if (seconds != -1) {
			cal.set(Calendar.SECOND, seconds);
		}
		return cal;
	}
}

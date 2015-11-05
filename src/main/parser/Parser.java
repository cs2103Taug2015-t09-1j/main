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

import main.model.EnumTypes;
import main.model.EnumTypes.CATEGORY;
import main.model.EnumTypes.COMMAND_TYPE;
import main.model.EnumTypes.PARAM_TYPE;
import main.model.EnumTypes.TASK_TYPE;
import main.model.ParsedObject;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;

/**
 * @@author Dalton
 *
 */
public class Parser {
	private static Parser parser = null;
	private static PrettyTimeParser ptParser;
	private final Logger logger = Logger.getLogger(Parser.class.getName());
	private final String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m", "change"};
	private final String[] deleteCmdList = {"delete", "del", "/d", "remove", "/r"};
	private final String[] doneCmdList = {"done", "complete"};
	private final String[] undoneCmdList = {"!done", "undone", "incomplete"};
	private final String[] undoCmdList = {"undo", "back"};
	private final String[] redoCmdList = {"redo", "forward"};
	private final String[] exitCmdList = {"exit", "quit", "/q"};
	private final String[] displayCmdList = {"display", "show", "/sh", "view", "/v"};

	private final String UPDATE_REGEX = "^((\\d+\\s+\\d+\\s+(\\w*|\\d*|\\s*)+)|(\\d+\\s\\d+\\s\\w+\\s\\w+\\s\\d+\\s\\d+:\\d+:\\d+\\s\\w+\\s\\d+))";
	private final String DELETE_REGEX = "^(\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)|\\s*all\\s*)";
	private final String DISPLAY_REGEX = "^(\\w|\\d|\\s|!|-|,|to|between)+";
	private final String DONE_UNDONE_REGEX= "^!?\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)";
	private final String UNDO_REDO_REGEX = "^\\d+\\s*$";

	private final String timeRegexPattern = "(((\\d+\\s+(minutes|min|seconds|sec|hours))|[0-9](am|pm|a.m.|p.m.)?|1[0-2](am|pm|a.m.|p.m.)?)|"
							+ "(0[0-9]|1[0-9]|2[0-3])\\:?([0-5][0-9]))\\:?([0-5][0-9])?(am|pm|a.m.|p.m.|h|\\shours)?";

	private Parser() {}

	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
			ptParser = new PrettyTimeParser();
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
			input = removeCommandWord(input, displayCmdList);
			if (hasValidParameters(input, DISPLAY_REGEX) || input.trim().isEmpty()) {
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
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.trim().isEmpty()) {
				return COMMAND_TYPE.UNDO;
			}
		} else if (isCommand(input, redoCmdList)) {
			input = removeCommandWord(input, redoCmdList);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.trim().isEmpty()) {
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
		Pattern timePattern = Pattern.compile("(?ui)" + timeRegexPattern);
		Matcher matcher = Pattern.compile("(?ui)from\\s*" + timeRegexPattern).matcher(input);
		if (matcher.find()) {
			input = input.substring(0, matcher.start()) +  input.substring(matcher.start()+5, input.length());
		}
		input = input.replaceAll("until", "till");
		//input = input.replaceAll("until", "till");
		//input = input.replaceAll("(?ui)\\d+(?!(pm|am))", "");
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
			/*for (int i = 0; i < dGroups.size(); i++) {
				if (dGroups.get(i).getText().matches("(?ui)\\d+(?!(pm|am))")) {
					dGroups.remove(i);
				}
			}*/

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
							setDateTime(temp1, -1, -1, -1, 23, 59, 0);
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
							// from 1pm lunch with john to 2pm tomorrow
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
						} else if (!matcher1.find() && matcher2.find()) {
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
						} else {
							return null;
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
			Matcher fromMatcher = Pattern.compile("(?ui)from\\s*" + timeRegexPattern).matcher(input);
			if (fromMatcher.find()) {
				input = input.substring(0, fromMatcher.start()) +  input.substring(fromMatcher.start()+5, input.length());
			}
			input = input.replaceAll("until", "till");
			List<DateGroup> dateGroup = ptParser.parseSyntax(input);
			/*for (int i = 0; i < dateGroup.size(); i++) {
				if (dateGroup.get(i).getText().matches("\\d+(?!(pm|am))")) {
					dateGroup.remove(i);
				}
			}*/
			for (int i = 0; i < dateGroup.size(); i++) {
				date = dateGroup.get(i).getText();
				input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|before|till|to|from|on|at)\\s+" + date, "");
				input = input.replaceAll("(?ui)\\s*" + date + "\\s*", " ");
			}
			//Pattern splitPattern = Pattern.compile("\\s+((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s+");
			//String[] excessWords = splitPattern.split(date);
			Pattern splitPattern = Pattern.compile("\\s");
			String[] excessWords = splitPattern.split(date);
			for (String word : excessWords) {
				input = input.replaceAll("\\s+((due on)|(due by)|due|by|before|till|to|from|on|at)?\\s+" + word + "\\b", "");
			}
			//input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|before|till|to|from|on|at)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)*\\s*$", "");
			input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|before|till|to|from|on|at)*\\s*$", "");
			//input = input.replaceAll("^(?ui)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)", "");
			input = input.replaceAll("(?ui)^\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)\\s*", "");
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
		case DISPLAY:
			paramArray = input.split("(,| and | to )");
			break;
		case UPDATE:
			paramArray = input.split("\\s+", 3);
			break;
		default:
			paramArray = input.split("\\s+");
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
		ArrayList<CATEGORY> categories = new ArrayList<CATEGORY>();
		if (parsedInput == null) {
			if (input.matches("(?ui)^\\s*all\\s*$") || input.trim().isEmpty()) {
				categories.add(CATEGORY.ALL);
			} else if (input.matches("(?ui)^\\s*expire(?:d)?\\s*.*?")) {
				categories.add(CATEGORY.EXPIRED);
				input = input.replaceFirst(("(?ui)^\\s*expire(?:d)?\\s*"), "");
				if (!input.trim().isEmpty()) {
					if (input.matches("(?ui)^(and|but)?\\s*(complete(?:d)?|done)\\s*$")) {
						categories.add(CATEGORY.COMPLETED);
					} else if(input.matches("(?ui)^(and|but)?\\s*(not |un|in|non-|!)complete(?:d)?|(not |un|!)done\\s*$")) {
						categories.add(CATEGORY.INCOMPLETED);
					} else {
						return new ParsedObject(COMMAND_TYPE.INVALID);
					}
				}
			} else if (input.matches("(?ui)^\\s*((!|non-|not |un)expire(?:d)?)\\s*.*?")) {
				categories.add(CATEGORY.NONEXPIRED);
				input = input.replaceFirst(("(?ui)^\\s*((!|non-|not |un)expire(?:d)?)\\s*"), "");
				if (!input.trim().isEmpty()) {
					if (input.matches("(?ui)^(and|but)?\\s*(complete(?:d)?|done)\\s*$")) {
						categories.add(CATEGORY.COMPLETED);
					} else if(input.matches("(?ui)(and|but)?^\\s*(not |un|in|non-|!)complete(?:d)?|(not |un|!)done\\s*$")) {
						categories.add(CATEGORY.INCOMPLETED);
					} else {
						return new ParsedObject(COMMAND_TYPE.INVALID);
					}
				}
			} else if (input.matches("(?ui)^\\s*(complete(?:d)?|done)\\s*.*?")) {
				categories.add(CATEGORY.COMPLETED);
				input = input.replaceFirst(("(?ui)^\\s*(complete(?:d)?|done)\\s*"), "");
				if (!input.trim().isEmpty()) {
					if (input.matches("(?ui)^(and|but)?\\s*expire(?:d)?\\s*$")) {
						categories.add(CATEGORY.EXPIRED);
					} else if(input.matches("(?ui)^(and|but)?\\s*(!|non-|not |un)expire(?:d)?\\s*$")) {
						categories.add(CATEGORY.NONEXPIRED);
					} else {
						return new ParsedObject(COMMAND_TYPE.INVALID);
					}
				}
			} else if (input.matches("(?ui)^\\s*(not |un|in|non-|!)complete(?:d)?|(not |un|!)done\\s*.*?")) {
				categories.add(CATEGORY.INCOMPLETED);
				input = input.replaceFirst(("(?ui)^\\s*(not |un|in|non-|!)complete(?:d)?|undone|!done|not done)\\s*"), "");
				if (!input.trim().isEmpty()) {
					if (input.matches("(?ui)^(and|but)?\\s*expire(?:d)?\\s*$")) {
						categories.add(CATEGORY.EXPIRED);
					} else if(input.matches("(?ui)^(and|but)?\\s*(not |un|non-|!)expire(?:d)?\\s*$")) {
						categories.add(CATEGORY.NONEXPIRED);
					} else {
						return new ParsedObject(COMMAND_TYPE.INVALID);
					}
				}
			} else {
				return new ParsedObject(COMMAND_TYPE.INVALID);
			}
			obj = new ParsedObject(COMMAND_TYPE.DISPLAY, PARAM_TYPE.CATEGORY, categories);
		} else {
			//ArrayList<Date> dates = new ArrayList<Date>(parsedInput);
			ArrayList<Date> dates = new ArrayList<Date>();
			if (input.trim().isEmpty()) {
				obj = new ParsedObject(COMMAND_TYPE.INVALID);
			} else if (input.toLowerCase().contains(" to ")) {
				COMMAND_TYPE cmdType;
				if (input.startsWith("time")) {
					input = input.toLowerCase().replace("time", "");
					cmdType = COMMAND_TYPE.DISPLAY_BETWEEN;
				} else {
					cmdType = COMMAND_TYPE.DISPLAY_ON_BETWEEN;
				}

				ArrayList<String> dateStrings = getCommandParameters(input, EnumTypes.COMMAND_TYPE.DISPLAY);
				for (int i = 0; i < dateStrings.size(); i++) {
					List<DateGroup> dateGroups = getDateGroups(dateStrings.get(i));
					if (dateGroups != null && dateGroups.size() > 0) {
						if (dateGroups.get(0).getDates().size() == 1) {
							dates.add(dateGroups.get(0).getDates().get(0));
						}
					}
				}

				obj = new ParsedObject(cmdType, PARAM_TYPE.DATE, dates);
			} else {
				ArrayList<String> dateStrings = getCommandParameters(input, EnumTypes.COMMAND_TYPE.DISPLAY);
				dates.clear();
				for (int i = 0; i < dateStrings.size(); i++) {
					List<DateGroup> dateGroups = getDateGroups(dateStrings.get(i));
					if (dateGroups != null && dateGroups.size() > 0) {
						if (dateGroups.get(0).getDates().size() == 1) {
							dates.add(dateGroups.get(0).getDates().get(0));
						}
					}
				}
				obj = new ParsedObject(COMMAND_TYPE.DISPLAY_ON, PARAM_TYPE.DATE, dates);
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
						obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.DEADLINE, tasks);
					} else {
						tasks.add(new Event(parsedInput.get(0), parsedInput.get(0), getTaskDesc(input), false));
						obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.SINGLE_DATE_EVENT, tasks);
					}
					break;
				case 2:
					tasks.add(new Event(parsedInput.get(0), parsedInput.get(1), getTaskDesc(input), false));
					obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.DOUBLE_DATE_EVENT, tasks);
					break;
				default:
					obj = new ParsedObject(COMMAND_TYPE.INVALID);
			}
		} else {
			tasks.add(new Todo(input.trim(), false));
			obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.TODO, tasks);
		}
		return obj;
	}

	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, updateCmdList);
		ArrayList<String> paramsList = getCommandParameters(params, COMMAND_TYPE.UPDATE);
		ParsedObject obj = new ParsedObject(COMMAND_TYPE.UPDATE, PARAM_TYPE.STRING, paramsList);
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
		input = removeCommandWord(input, deleteCmdList);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(input, COMMAND_TYPE.DELETE);
		ArrayList<CATEGORY> categories = new ArrayList<CATEGORY>();

		if (input.matches("(?ui)^\\s*all\\s*$")) {
			categories.add(CATEGORY.ALL);
			return new ParsedObject(COMMAND_TYPE.DELETE, PARAM_TYPE.CATEGORY, categories);
		}

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

		return new ParsedObject(COMMAND_TYPE.DELETE, PARAM_TYPE.ID, taskIDs);
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
		obj = new ParsedObject(newStatus ? COMMAND_TYPE.DONE : COMMAND_TYPE.UNDONE, PARAM_TYPE.ID, taskIDs);
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

		return new ParsedObject(COMMAND_TYPE.UNDO, PARAM_TYPE.INTEGER, numOfExec);
	}

	public ParsedObject getRedoParsedObject(String input) {
		String params = removeCommandWord(input, redoCmdList);
		ArrayList<Integer> numOfExec = new ArrayList<Integer>();
		if (params.isEmpty()) {
			numOfExec.add(1);
		} else {
			numOfExec.add(parseInteger(params));
		}

		return new ParsedObject(COMMAND_TYPE.REDO, PARAM_TYPE.INTEGER, numOfExec);
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

	public String[] getCommandKeywordList(String command) {
		switch (command) {
			case "update":
				return updateCmdList;
			case "delete":
				return deleteCmdList;
			case "done":
				return doneCmdList;
			case "undone":
				return undoneCmdList;
			case "undo":
				return undoCmdList;
			case "redo":
				return redoCmdList;
			case "exit":
				return exitCmdList;
			case "display":
				return displayCmdList;
			default:
				return null;
		}
	}
}

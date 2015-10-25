package main.parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Arrays;

import main.logic.UndoRedo;
import main.model.ParsedObject;
import main.model.EnumTypes.*;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.storage.Storage;

public class Parser {
	private static Parser parser = null;
	private final PrettyTimeParser ptParser = new PrettyTimeParser();
	private final Logger logger = Logger.getLogger(Parser.class.getName());
	private String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m"};
	private String[] deleteCmdList = {"delete", "del", "/d", "remove", "rm", "/r"};
	private String[] doneCmdList = {"done", "complete"};
	private String[] undoneCmdList = {"undone", "incomplete"};
	private String[] undoCmdList = {"undo", "/un"};
	private String[] redoCmdList = {"redo", "/re"};
	private String[] exitCmdList = {"exit", "/e", "quit", "/q"};
	//private String[] searchCmdList = {"search", "/s", "find", "/f"};
	//private String[] displayCmdList = {"display", "/dp", "show", "/sw"};
	//private String[] doneCmdList = {"is done", "done"};

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
		} else {
			if (isValidCommand(input, updateCmdList, "\\s+\\d+\\s+\\d+")) {
				return COMMAND_TYPE.UPDATE;
			} else if (isValidCommand(input, deleteCmdList, "\\s+\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)")) {
				return COMMAND_TYPE.DELETE;
			} else if (isValidCommand(input, doneCmdList, "\\s+\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)")) {
				return COMMAND_TYPE.DONE;
			} else if (isValidCommand(input, undoneCmdList, "\\s+\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)")) {
				return COMMAND_TYPE.UNDONE;
			} else if (isValidCommand(input, undoCmdList, "(\\s+\\d+\\s*)*$")) {
				return COMMAND_TYPE.UNDO;
			} else if (isValidCommand(input, redoCmdList, "(\\s+\\d+\\s*)*$")) {
				return COMMAND_TYPE.REDO;
			} else if (isValidCommand(input, exitCmdList, "\\s*$")) {
				return COMMAND_TYPE.EXIT;
			} else {
				return COMMAND_TYPE.ADD;
			}
		}
	}

	private boolean isValidCommand(String input, String[] commandList, String regex) {
		for(int i = 0; i < commandList.length; i++) {
			Pattern pattern = Pattern.compile("(?i)^" + commandList[i] + regex);
			Matcher matcher = pattern.matcher(input);
	        if (matcher.find()) {
	        	return true;
	        }
		}
		return false;
	}

	private String removeCommandWord(String input, String[] commandList) {
		String temp = input;
		for(int i = 0; i < commandList.length; i++) {
			if (!input.equalsIgnoreCase(commandList[i]) && input.startsWith(commandList[i])) {
				input = input.toLowerCase().replaceAll(commandList[i] + "\\s*", "");
				break;
			}
		}

		if (temp.equals(input)) {
			input = "";
		}

		return input;
	}

	public String formatDate(Date d, String format) {
		return new SimpleDateFormat(format).format(d);
	}

	public List<Date> getDateList(String input) {
		if (input.contains("+")) {
			input = input.split("\\+")[1];
		}

		List<DateGroup> dGroup = parseDates(input);
		if (dGroup != null) {
			return dGroup.get(0).getDates();
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
		List<DateGroup> temp = ptParser.parseSyntax(input);
		String date = temp.get(0).getText();
		input = input.toLowerCase().replaceAll("\\++((due by)|due|by|before|from|on|at)\\s*" + date, "");
		input = input.replaceAll("\\s*" + date + "\\s*", "");
		input = input.replaceAll("\\s+((due by)|due|by|from|on|at)\\s*((due by)|due|by|from|on|at)*\\s*$", "");
		input = input.replaceAll("^\\s*((due by)|due|by|from|on|at)\\s*((due by)|due|by|from|on|at)", "");
		input = input.replaceAll("^\\s+|\\s+$", "");
		input = input.replaceAll("^\\+", "");
		return input.trim();
	}

	public ArrayList<String> getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		String[] paramArray;

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
		//UndoRedo.getInstance().addUndoable(obj);
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
*/
}

package parser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Date;

import org.ocpsoft.prettytime.nlp.*;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;
import org.ocpsoft.prettytime.shade.edu.emory.mathcs.backport.java.util.Arrays;

import logic.Logic;
import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.ParsedObject;
import models.Task;
import models.Commands.*;

public class MainParser {
	private static MainParser parser = null;
	private final PrettyTimeParser ptParser = new PrettyTimeParser();
	private final Logger logger = Logger.getLogger(MainParser.class.getName());
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
				input = input.split(commandList[i])[1];
			}
		}
		return input;
	}

	public String formatDate(Date d, String format) {
		return new SimpleDateFormat(format).format(d);
	}

	public List<DateGroup> parseDates(String input) {
		if (input.contains("+")) {
			input = input.split("\\+")[1];
		}
		return ptParser.parseSyntax(input);
	}

	private String getTaskDesc(String input) {
		List<DateGroup> temp = parseDates(input);
		String date = temp.get(0).getText();
		input = input.toLowerCase().replaceAll("\\++((due by)|due|by|before|from|on|at)\\s*" + date, "");
		input = input.replaceAll("\\s*" + date + "\\s*", "");
		input = input.replaceAll("\\s+((due by)|due|by|from|on|at)\\s*((due by)|due|by|from|on|at)*\\s*$", "");
		input = input.replaceAll("^\\s*((due by)|due|by|from|on|at)\\s*((due by)|due|by|from|on|at)", "");
		input = input.replaceAll("^\\s+|\\s+$", "");
		return input.trim();
	}

	public ParsedObject getAddParsedObject(String input) {
		List<DateGroup> parsedInput = parseDates(input);
		ArrayList<Task> tasks = new ArrayList<Task>();

		if (!parsedInput.isEmpty()) {
			switch (parsedInput.get(0).getDates().size()) {
				case 1:
					if (input.contains("by") || input.contains("due") || input.contains("before")) {
						// Deadline Task
						parsedInput.get(0).getDates().get(0).setHours(23);
						parsedInput.get(0).getDates().get(0).setMinutes(59);
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
			tasks.add(new FloatingTask(input.trim(), false));
			return new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.FLOATING_TASK, tasks);
		}
	}

	public ArrayList<String> getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		if (cmdType == COMMAND_TYPE.SEARCH || cmdType == COMMAND_TYPE.DISPLAY) {
			pattern = "\\.+|,+|:+|;+|/+|\\\\+|\\|+";
		} else {
			if (input.contains("to") || input.contains("-")) {
				pattern = "\\-+|to";
			} else {
				pattern = "\\s+|\\.+|,+|:+|;+|/+|\\\\+|\\|+";
			}
		}

		String[] paramArray = input.split(pattern);

		for (int i = 0; i < paramArray.length; i++) {
			paramArray[i] = paramArray[i].replaceAll("^\\s+|\\s+$", "");
		}

		ArrayList<String> paramList = new ArrayList<String>(Arrays.asList(paramArray));

		if (input.contains("to") || input.contains("-")) {
			int fromID = Integer.parseInt(paramList.get(0));
			int toID = Integer.parseInt(paramList.get(1));

			while (toID-1 >= fromID+1) {
				paramList.add(1, toID-1 + "");
				toID--;
			}
		}

		return paramList;
	}

	public ParsedObject getDeleteParsedObject(String input) {
		String params = removeCommandWord(input, deleteCmdList);

		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);
		for (String idStr : taskIDList) {
			if (!idStr.trim().isEmpty()) {
				try {
					int taskID = Integer.parseInt(idStr);
					taskIDs.add(taskID);
				} catch (NumberFormatException e) {
					System.out.println("Error");
					// Catch exception and continue parsing the rest
					logger.log(Level.SEVERE, e.toString(), e);
				}
			}
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

	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, searchCmdList);

		ArrayList<String> searchTerms = new ArrayList<String>();
		ArrayList<String> termArray = getCommandParameters(params, COMMAND_TYPE.UPDATE);
		for (String term : termArray) {
			if (!term.trim().isEmpty()) {
				searchTerms.add(term);
			}
		}

		return new ParsedObject(COMMAND_TYPE.UPDATE, null, searchTerms);
	}
}

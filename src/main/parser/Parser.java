package main.parser;

import java.util.ArrayList;
import java.util.List;

import main.models.Event;
import main.models.Task;
import main.models.Todo;

public class Parser {
	
	public static String EMPTY_STRING = "";
	
	public static String getFirstWord(String command) {
		if (command.trim().isEmpty()) return EMPTY_STRING; 
		return command.trim().split("\\s+")[0];
	}
	
	public static String removeFirstWord(String command) {
		int cutPosition = command.indexOf(" ");
		if (cutPosition == -1) return EMPTY_STRING;
		return command.substring(cutPosition).trim();
	}
	
	public static Task getTask(String command) {
		int startPosition = command.indexOf("from");
		String desc = command;
		if (startPosition == -1) {
			return new Todo(desc, false);
		}
		int endPosition = command.indexOf("to");
		desc = command.substring(0, startPosition).trim();
		String startTime = command.substring(startPosition + 4, endPosition).trim();
		String endTime = command.substring(endPosition + 2).trim();
		return null;//new Event("", startTime, endTime, desc, false);
	}
	
	public static int getTaskId(String command) {
		String idString = getFirstWord(command);
		try {
			return Integer.parseInt(idString);
		} catch (Exception e ){
			return -1;
		}
	}
	
	public static List<Integer> getTaskIds(String command) {
		String[] stringList = command.trim().split("\\s+");
		List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < stringList.length; i++) {
			try {
				ids.add(Integer.parseInt(stringList[i]));
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return ids;
	}
}

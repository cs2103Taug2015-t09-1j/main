package logic;

import java.util.ArrayList;
import java.util.Vector;

import models.*;
import models.Commands.ADD_TYPE;
import models.Commands.COMMAND_TYPE;
import parser.MainParser;

public class Logic {
	private static Logic logic = null;
	private static final MainParser parser = MainParser.getInstance();

	private Logic() { }

	public static Logic getInstance() {
		if (logic == null) {
			logic = new Logic();
		}
		return logic;
	}

	public Vector<Task> processCommand(String input) {
		COMMAND_TYPE cmdType = parser.determineCommandType(input);

		switch (cmdType) {
		case ADD:
			return addTask(input);
		case SEARCH:
			return addTask(input);
		case UPDATE:
			return addTask(input);
		case DELETE:
			return addTask(input);
		case UNDO:
			return addTask(input);
		default:
			return new Vector<Task>();
		}
	}

	private Vector<Task> addTask(String input) {
		//Vector<Task> tasks = Storage.addTask(parser.getTaskType(input));
		return null;
	}

	/*
	private Vector<Task> searchTask(ArrayList<String> cmdContents) {

	}*/


}
package logic;

import java.util.ArrayList;
import java.util.Vector;

import models.*;
import parser.MainParser;

public class Logic {
	public Vector<Task> processCommand(String input) {
		ArrayList<String> cmdContents = MainParser.ParseCommand(input);

		switch (cmdContents.get(0)) {
		case "add":
			return addTask(cmdContents);
		default:
			return new Vector<Task>();
		}
	}

	private Vector<Task> addTask(ArrayList<String> cmdContents) {
		switch (cmdContents.get(1)) {
		case "deadline":
			break;
		case "event":
			Event evt = new Event(cmdContents.get(4), cmdContents.get(2), cmdContents.get(3), cmdContents.get(5), false);
			//Storage.addTask(evt);
			break;
		default:

		}
		return new Vector<Task>();
	}

	private Vector<Task> searchTask(ArrayList<String> cmdContents) {

	}


}
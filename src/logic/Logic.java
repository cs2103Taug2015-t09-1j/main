package logic;

import java.util.ArrayList;
import java.util.Vector;

import models.Task;

public class Logic {
	//private static final Vector<Task> currentList = Storage.getList();

	public static Vector<Task> processLine(String inputLine) {
		if(newList.isSuccessful()) {
			switch (Parser.getCommand(inputLine)) {
			case "add" :
				newList = addEvent(newList, newTask));
				break;

			case "delete" :
				newList = deleteEvent(newTask.getIndex());
				break;

			case "display" :
				newList = displayList();
				break;

			case "undo" :
				newList = undoLastAction();
				break;

			default :
				showError();
			}

			return newList;
		}
	}

	public static void setInput(String newInput) {
		inputLine = newInput;
	}

	private static Vector<Task> addEvent(TaskBook newList, Task newTask) {
		newList.addTask(newTask);
		Storage.setList(newList.getTaskList());
		return newList;
	}

	private static Vector<Task> deleteEvent(TaskBook newList, int index) {
		newList.removeTask(index);
		Storage.setList(newList.getTaskList());
		return newList;
	}

	private static Vector<Task> displayList() {
		return new TaskBook(currentList, Parser.isSuccessful())
	}
	/*
	private static Vector<Task> undoLastAction() {
		String prevCommand = Parser.getPrevCommand();
		String prevTask = Parser.getPrevTask();
		switch (prevCommand) {
		case "add" :
			currentList.remove(prevTask.getIndex());
			break;

		case "delete" :
			currentList.add(prevTask, prevTask.getIndex());
			break;

		case "undo" :


		default :
			break;
		}
	}*/
}
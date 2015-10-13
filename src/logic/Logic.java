package logic;

import parser.MainParser;
import parser.Parser;
import storage.Storage;
import ui.MainGUI;

import java.util.List;
import java.util.Vector;

import models.Commands.*;
import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.ParsedObject;
import models.Task;

public class Logic {

	public static List<Task> tasks;
	private static Logic logic = null;
	private static final MainParser parser = MainParser.getInstance();

	private Logic() { }

	public static Logic getInstance() {
		if (logic == null) {
			logic = new Logic();
		}
		return logic;
	}

	public void processCommand(String input) {
		switch (parser.determineCommandType(input)) {
		case ADD:
			add(input);
			break;
		case DISPLAY:
			display(input);
			break;
		case SEARCH:
			search(input);
			break;
		case UPDATE:
			//update(input);
			break;
		case DELETE:
			delete(input);
			break;
		case UNDO:
			//undo(input);
			break;
		default:
		}
	}

	private void add(String input) {
		// For Debugging
		ParsedObject obj = parser.getAddParsedObject(input);
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		Vector<Task> v = obj.getObjects();
		for (int i = 0; i < v.size(); i++) {
			switch (obj.getTaskType()) {
			case SINGLE_DATE_EVENT:
				Event se = (Event)v.get(i);
				System.out.println(se.getTaskID() + ", " + se.getTaskDesc() + ", " + se.getFromDate() + ", " + se.getToDate());
				break;
			case DOUBLE_DATE_EVENT:
				Event de = (Event)v.get(i);
				System.out.println(de.getTaskID() + ", " + de.getTaskDesc() + ", " + de.getFromDate() + ", " + de.getToDate());
				break;
			case FLOATING_TASK:
				FloatingTask f = (FloatingTask)v.get(i);
				System.out.println(f.getTaskID() + ", " + f.getTaskDesc());
				break;
			case DEADLINE_TASK:
				DeadlineTask d = (DeadlineTask)v.get(i);
				System.out.println(d.getTaskID() + ", " + d.getTaskDesc() + ", " + d.getDate());
				break;
			default:
				// Invalid - return Msg back to UI immediately
			}

		}
		System.out.println();
		// return Storage.add(parser.getAddParsedObject(input));
	}

	private void delete(String input) {
		// For Debugging
		ParsedObject obj = parser.getDeleteParsedObject(input);
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		Vector<Integer> v = obj.getObjects();
		for (int i = 0; i < v.size(); i++) {
			System.out.print(v.get(i));
			if (i < v.size()-1) {
				System.out.print("|");
			}
		}
		System.out.println();
		// return Storage.delete(parser.getDeleteParsedObject(input));
	}

	private void display(String input) {
		// For Debugging
		ParsedObject obj = parser.getDisplayParsedObject(input);
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		Vector<String> v = obj.getObjects();
		for (int i = 0; i < v.size(); i++) {
			System.out.print(v.get(i));
			if (i < v.size()-1) {
				System.out.print("|");
			}
		}
		System.out.println();
		// return Storage.display(parser.getDisplayParsedObject(input));
	}

	private void search(String input) {
		// For Debugging
			ParsedObject obj = parser.getSearchParsedObject(input);
			System.out.println(obj.getCommandType());
			System.out.println(obj.getTaskType());
			Vector<String> v = obj.getObjects();
			for (int i = 0; i < v.size(); i++) {
				System.out.print(v.get(i));
				if (i < v.size()-1) {
					System.out.print("|");
				}
			}
			System.out.println();
			// return Storage.search(parser.getSearchParsedObject(input));
	}

	/*
	// need to be call when the app start
	public static void init() {
		tasks = Storage.getAllTask();
		List<Task> _tasks = tasks;
		int maxId = 0;
		for (Task task: tasks) {
			maxId = Math.max(maxId, task.getTaskID());
		}
		Task.nextId.set(maxId);
	}

	public static void processCommand(String command, MainGUI uiRef) {
		Vector<Task> data = new Vector<>();
		String message = null;

		String commandType = Parser.getFirstWord(command);
		command = Parser.removeFirstWord(command);
		switch (commandType) {
			case "add": data = addTask(command); break;
			case "update": data = updateTask(command); break;
			case "display": data = display(command); break;
			case "delete": data = deleteTask(command); break;
			case "search": data = search(command); break;
			case "done": data = markTask(command, true); break;
			case "undone": data = markTask(command, false); break;
			default: message = "Incorrect format!";
		}

		Storage.saveAllTask(tasks);

		if (message == null) {
			uiRef.updateTables(data, "All");
		}
	}

	private static Vector<Task> addTask(String command) {
		Vector<Task> result = new Vector<>();
		Task newTask = Parser.getTask(command);
		result.addElement(newTask);
		tasks.add(newTask);
		System.out.println(tasks.size());
		return result;
	}

	private static Vector<Task> updateTask(String command) {
		Vector<Task> tasks = new Vector<>();
		int id = Parser.getTaskId(command);
		String desc = Parser.removeFirstWord(command);
		Task task = findTaskById(id);
		if (task == null) {
			return tasks;
		}
		task.setTaskDesc(desc);
		tasks.addElement(task);
		return tasks;
	}

	private static Vector<Task> display(String command) {
		return new Vector<Task>(tasks);
	}

	private static Vector<Task> deleteTask(String command) {
		List<Integer> deletedIds = Parser.getTaskIds(command);
		for (int i = 0; i < tasks.size(); i++) if (deletedIds.contains(tasks.get(i).getTaskID())){
			tasks.remove(i);
		}
		return new Vector<Task>(tasks);
	}

	private static Vector<Task> search(String command) {
		Vector<Task> matchedTasks = new Vector<>();
		for (Task task: tasks) if (task.getTaskDesc().indexOf(command) != -1) {
			matchedTasks.addElement(task);
		}
		return matchedTasks;
	}

	private static Vector<Task> markTask(String command, boolean isDone)  {
		List<Integer> markIds = Parser.getTaskIds(command);
		for (Task task: tasks) if (markIds.contains(task.getTaskID())){
			task.setDone(isDone);
		}
		return new Vector<Task>(tasks);
	}

	private static Task findTaskById(int id) {
		for (Task task: tasks) {
			if (task.getTaskID() == id) return task;
		}
		return null;
	}*/
}
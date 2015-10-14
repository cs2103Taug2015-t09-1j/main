package logic;

import parser.MainParser;
import parser.Parser;
import storage.Storage;
import ui.MainGUI;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.Commands;
import models.Commands.*;
import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.ParsedObject;
import models.Task;

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

	public String processCommand(String input) {
		switch (parser.determineCommandType(input)) {
		case ADD:
			return add(input);
			//break;
		case DISPLAY:
			//display(input);
			break;
		case SEARCH:
			//search(input);
			break;
		case UPDATE:
			//update(input);
			break;
		case DELETE:
			//delete(input);
			break;
		case UNDO:
			//undo(input);
			break;
		default:
		}
		return null;
	}

	public ArrayList<List<Task>> getAllTaskLists() {
		ArrayList<List<Task>> taskLists = new ArrayList<List<Task>>();
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.EVENT));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK));
		return taskLists;
	}

	public ArrayList<Event> getAllEvents() {
		return (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT);
	}

	public ArrayList<FloatingTask> getAllFloatingTasks() {
		return (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK);
	}

	public ArrayList<DeadlineTask> getAllDeadlineTasks() {
		return (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK);
	}

	private String add(String input) {
		// For Debugging
		ParsedObject obj = parser.getAddParsedObject(input);
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		ArrayList<Task> v = obj.getObjects();
		for (int i = 0; i < v.size(); i++) {
			switch (obj.getTaskType()) {
			case SINGLE_DATE_EVENT:
				Event sEvt = (Event)v.get(i);
				System.out.println(sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
				//return Storage.addTask(parser.getAddParsedObject(input), Commands.TASK_TYPE.SINGLE_DATE_EVENT);
				Storage.addTask(sEvt, Commands.TASK_TYPE.EVENT);

				return "<html><b>\"" + sEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event on <b>" + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + "</b> at <b>" + parser.formatDate(sEvt.getFromDate(), "h:mm a") + "</b>.</html>";
				//break;
			case DOUBLE_DATE_EVENT:
				Event dEvt = (Event)v.get(i);
				System.out.println(dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
				Storage.addTask(dEvt, Commands.TASK_TYPE.EVENT);
				return "<html><b>\"" + dEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event from <b>" + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "</b> to <b>" + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + "</b>.</html>";
				//break;
			case FLOATING_TASK:
				FloatingTask flt = (FloatingTask)v.get(i);
				System.out.println(flt.getTaskID() + ", " + flt.getTaskDesc());
				Storage.addTask(flt, Commands.TASK_TYPE.FLOATING_TASK);
				return "<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>";
				//break;
			case DEADLINE_TASK:
				DeadlineTask dt = (DeadlineTask)v.get(i);
				System.out.println(dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
				Storage.addTask(dt, Commands.TASK_TYPE.DEADLINE_TASK);
				return "<html><b>\"" + dt.getTaskDesc() + "\"</b><br/>has been successfully added as a Deadline task that must be completed by <b>" + parser.formatDate(dt.getDate(), "EEE, d MMM yyyy") + "</b>.</html>";
				//break;
			default:
				// Invalid - return Msg back to UI immediately
				return "Add command has failed.";
			}
		}
		System.out.println();
		return "Add command has failed.";
	}

	private void delete(String input) {
		// For Debugging
		ParsedObject obj = parser.getDeleteParsedObject(input);
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		ArrayList<Integer> v = obj.getObjects();
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
		ArrayList<String> v = obj.getObjects();
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
			ArrayList<String> v = obj.getObjects();
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
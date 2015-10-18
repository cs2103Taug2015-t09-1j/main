package logic;

import parser.MainParser;
import storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Commands;
import models.Deadline;
import models.Event;
import models.Todo;
import models.ParsedObject;
import models.Task;

public class Logic extends Observable {
	private static Logic logic = null;
	private static final MainParser parser = MainParser.getInstance();
	private static final LogicObservable observable = LogicObservable.getInstance();
	private final Logger logger = Logger.getLogger(Logic.class.getName());

	private Logic() {}

	public static Logic getInstance() {
		if (logic == null) {
			logic = new Logic();
			Storage.init();
		}
		return logic;
	}

	public String processCommand(String input) {
		switch (parser.determineCommandType(input)) {
			case ADD:
				//return add(input);
				add(input);
				break;
			case DISPLAY:
				//display(input);
				break;
			case SEARCH:
				//search(input);
				break;
			case UPDATE:
				update(input);
				break;
			case DELETE:
				//return delete(input);
				delete(input);
				break;
			case UNDO:
				//undo(input);
				break;
			default:
				observable.updateStatusMsg("Invalid command entered. Please try again.");
		}
		return null;
	}

	private void add(String input) {
		ParsedObject obj = parser.getAddParsedObject(input);
		assert obj != null;
		// For Debugging
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		ArrayList<Task> v = obj.getObjects();
		for (int i = 0; i < v.size(); i++) {
			switch (obj.getTaskType()) {
				case SINGLE_DATE_EVENT:
					Event sEvt = (Event)v.get(i);
					logger.log(Level.FINE, sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
					//System.out.println(sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
					Storage.addTask(sEvt, Commands.TASK_TYPE.EVENT);
					Storage.saveTaskType(Commands.TASK_TYPE.EVENT);
					observable.updateStatusMsg("<html><b>\"" + sEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event on <b>" + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + "</b> at <b>" + parser.formatDate(sEvt.getFromDate(), "h:mm a") + "</b>.</html>");
					observable.updateTables(Commands.TASK_TYPE.EVENT);
					break;
					//return "<html><b>\"" + sEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event on <b>" + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + "</b> at <b>" + parser.formatDate(sEvt.getFromDate(), "h:mm a") + "</b>.</html>";
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)v.get(i);
					logger.log(Level.FINE, dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
					//System.out.println(dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
					Storage.addTask(dEvt, Commands.TASK_TYPE.EVENT);
					Storage.saveTaskType(Commands.TASK_TYPE.EVENT);
					observable.updateStatusMsg("<html><b>\"" + dEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event from <b>" + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "</b> to <b>" + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + "</b>.</html>");
					observable.updateTables(Commands.TASK_TYPE.EVENT);
					break;
					//return "<html><b>\"" + dEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event from <b>" + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "</b> to <b>" + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + "</b>.</html>";
				case TODO:
					Todo flt = (Todo)v.get(i);
					logger.log(Level.FINE, flt.getTaskID() + ", " + flt.getTaskDesc());
					//System.out.println(flt.getTaskID() + ", " + flt.getTaskDesc());
					Storage.addTask(flt, Commands.TASK_TYPE.TODO);
					Storage.saveTaskType(Commands.TASK_TYPE.TODO);
					observable.updateStatusMsg("<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>");
					observable.updateTables(Commands.TASK_TYPE.TODO);
					break;
					//return "<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>";
				case DEADLINE:
					Deadline dt = (Deadline)v.get(i);
					logger.log(Level.FINE, dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					//System.out.println(dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					Storage.addTask(dt, Commands.TASK_TYPE.DEADLINE);
					Storage.saveTaskType(Commands.TASK_TYPE.DEADLINE);
					observable.updateStatusMsg("<html><b>\"" + dt.getTaskDesc() + "\"</b><br/>has been successfully added as a Deadline task that must be completed by <b>" + parser.formatDate(dt.getDate(), "EEE, d MMM yyyy") + "</b>.</html>");
					observable.updateTables(Commands.TASK_TYPE.DEADLINE);
					break;
					//return "<html><b>\"" + dt.getTaskDesc() + "\"</b><br/>has been successfully added as a Deadline task that must be completed by <b>" + parser.formatDate(dt.getDate(), "EEE, d MMM yyyy") + "</b>.</html>";
				default:
					// Invalid - return Msg back to UI immediately
					//return "Add command has failed.";
					observable.updateTables(null);
			}
		}
		System.out.println();
		//return "Add command has failed.";
	}

	private void delete(String input) {
		// For Debugging
		ParsedObject obj = parser.getDeleteParsedObject(input);
		assert obj != null;
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		ArrayList<Integer> v = obj.getObjects();
		String statusMsg = "<html>";
		if (v.size() > 0) {
			statusMsg += "Tasks IDs: ";
			for (int i = 0; i < v.size(); i++) {
				Storage.delete(v.get(i));
				statusMsg += ("<b>" + v.get(i) + "</b>");
				System.out.print(v.get(i));
				if (i < v.size()-1) {
					statusMsg += ", ";
					System.out.print("|");
				}
			}
			Storage.saveAllTask();
			statusMsg += "<br/>have been deleted successfully.</html>";
		} else {
			statusMsg += "TaskIDs are not found.</html>";
		}
		System.out.println();
		observable.updateStatusMsg(statusMsg);
		observable.updateTables(Commands.TASK_TYPE.ALL);
		// return Storage.delete(parser.getDeleteParsedObject(input));
	}

	public void updateTaskStatus(int taskID, boolean status) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		ids.add(taskID);
		Storage.changeStatus(ids, status);
		Storage.saveAllTask();
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

	/*
	public ArrayList<List<Task>> getAllTaskLists() {
		ArrayList<List<Task>> taskLists = new ArrayList<List<Task>>();
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.EVENT));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.TODO));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.DEADLINE));
		return taskLists;
	}

	public ArrayList<Event> getAllEvents() {
		return (Storage.getAllTask(Commands.TASK_TYPE.EVENT).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT) : new ArrayList();
	}

	public ArrayList<Todo> getAllTodos() {
		return (Storage.getAllTask(Commands.TASK_TYPE.TODO).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.TODO) : new ArrayList();
	}

	public ArrayList<Deadline> getAllDeadlineTasks() {
		return (Storage.getAllTask(Commands.TASK_TYPE.DEADLINE).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE) : new ArrayList();
	}

	private ArrayList<Event> searchEvents(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<Event> matches = new ArrayList<Event>();

		for (Event e : getAllEvents()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (e.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(e);
				}
			}
		}
		return matches;
	}

	private ArrayList<Todo> searchTodos(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<Todo> matches = new ArrayList<Todo>();

		for (Todo ft : getAllTodos()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (ft.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(ft);
				}
			}
		}
		return matches;
	}

	private ArrayList<Deadline> searchDeadlineTasks(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<Deadline> matches = new ArrayList<Deadline>();

		for (Deadline dt : getAllDeadlineTasks()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (dt.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(dt);
				}
			}
		}
		return matches;
	}

	private String search(String input) {
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
		return null;
		// return Storage.search(parser.getSearchParsedObject(input));
	}*/

	private void update(String input) {
		ParsedObject obj = parser.getUpdateParsedObject(input);
		ArrayList<String> params = obj.getObjects();
		Task t = Storage.getTaskByID(parser.parseInteger(params.get(0)));
		if (t != null) {
			if (Update.update(obj)) {
				observable.updateStatusMsg("Successfully updated.");
				observable.updateTables(Commands.TASK_TYPE.ALL);
				Storage.saveAllTask();
			} else {
				observable.updateStatusMsg("Invalid column or value entered.");
			}
		}
	}

	public Task getTaskByID(int id) {
		return Storage.getTaskByID(id);
	}
}
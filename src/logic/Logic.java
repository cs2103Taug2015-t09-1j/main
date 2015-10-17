package logic;

import parser.MainParser;
import storage.Storage;
import ui.MessageObserver;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Commands;
import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
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
			case SEARCH:
				//search(input);
			case UPDATE:
				//update(input);
			case DELETE:
				//return delete(input);
				delete(input);
				break;
			case UNDO:
				//undo(input);
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
				case FLOATING_TASK:
					FloatingTask flt = (FloatingTask)v.get(i);
					logger.log(Level.FINE, flt.getTaskID() + ", " + flt.getTaskDesc());
					//System.out.println(flt.getTaskID() + ", " + flt.getTaskDesc());
					Storage.addTask(flt, Commands.TASK_TYPE.FLOATING_TASK);
					Storage.saveTaskType(Commands.TASK_TYPE.FLOATING_TASK);
					observable.updateStatusMsg("<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>");
					observable.updateTables(Commands.TASK_TYPE.FLOATING_TASK);
					break;
					//return "<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>";
				case DEADLINE_TASK:
					DeadlineTask dt = (DeadlineTask)v.get(i);
					logger.log(Level.FINE, dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					//System.out.println(dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					Storage.addTask(dt, Commands.TASK_TYPE.DEADLINE_TASK);
					Storage.saveTaskType(Commands.TASK_TYPE.DEADLINE_TASK);
					observable.updateStatusMsg("<html><b>\"" + dt.getTaskDesc() + "\"</b><br/>has been successfully added as a Deadline task that must be completed by <b>" + parser.formatDate(dt.getDate(), "EEE, d MMM yyyy") + "</b>.</html>");
					observable.updateTables(Commands.TASK_TYPE.DEADLINE_TASK);
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
		String statusMsg = "<html>Tasks IDs: <b>";
		for (int i = 0; i < v.size(); i++) {
			Storage.delete(v.get(i));
			statusMsg += v.get(i);
			System.out.print(v.get(i));
			if (i < v.size()-1) {
				statusMsg += ", ";
				System.out.print("|");
			}
		}
		System.out.println();
		statusMsg += "</b> have been deleted successfully.<br/></html>";
		Storage.saveAllTask();
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

	public ArrayList<List<Task>> getAllTaskLists() {
		ArrayList<List<Task>> taskLists = new ArrayList<List<Task>>();
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.EVENT));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK));
		return taskLists;
	}

	public ArrayList<Event> getAllEvents() {
		return (Storage.getAllTask(Commands.TASK_TYPE.EVENT).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT) : new ArrayList();
	}

	public ArrayList<FloatingTask> getAllFloatingTasks() {
		return (Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK) : new ArrayList();
	}

	public ArrayList<DeadlineTask> getAllDeadlineTasks() {
		return (Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK) : new ArrayList();
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

	private ArrayList<FloatingTask> searchFloatingTasks(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<FloatingTask> matches = new ArrayList<FloatingTask>();

		for (FloatingTask ft : getAllFloatingTasks()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (ft.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(ft);
				}
			}
		}
		return matches;
	}

	private ArrayList<DeadlineTask> searchDeadlineTasks(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<DeadlineTask> matches = new ArrayList<DeadlineTask>();

		for (DeadlineTask dt : getAllDeadlineTasks()) {
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
	}
}
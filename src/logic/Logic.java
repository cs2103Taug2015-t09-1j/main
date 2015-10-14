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

	private Logic() {}

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
		case DISPLAY:
			//display(input);
		case SEARCH:
			//search(input);
		case UPDATE:
			//update(input);
		case DELETE:
			return delete(input);
		case UNDO:
			//undo(input);
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
		return (Storage.getAllTask(Commands.TASK_TYPE.EVENT).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT) : new ArrayList();
	}

	public ArrayList<FloatingTask> getAllFloatingTasks() {
		return (Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.FLOATING_TASK) : new ArrayList();
	}

	public ArrayList<DeadlineTask> getAllDeadlineTasks() {
		return (Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE_TASK) : new ArrayList();
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

	private String delete(String input) {
		// For Debugging
		ParsedObject obj = parser.getDeleteParsedObject(input);
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
		return statusMsg;

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
}
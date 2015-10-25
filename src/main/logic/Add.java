/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.models.Deadline;
import main.models.EnumTypes;
import main.models.Event;
import main.models.ParsedObject;
import main.models.Task;
import main.models.Todo;
import main.models.EnumTypes.TASK_TYPE;
import main.parser.Parser;
import main.storage.Storage;

/**
 * @author Dalton
 *
 */
public class Add extends Command {
	private static final Parser parser = Parser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final UndoRedo undoredo = UndoRedo.getInstance();
	private static final Logger logger = Logger.getLogger(Add.class.getName());
	private static final boolean DEBUG = true;
	private static Add add = null;

	private Add() {}

	public static Add getInstance() {
		if (add == null) {
			add = new Add();
		}
		return add;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		if (DEBUG) {
			System.out.println(obj.getCommandType());
			System.out.println(obj.getTaskType());
		}
		ArrayList<Task> tasks = obj.getObjects();
		if (!tasks.isEmpty()) {
			switch (obj.getTaskType()) {
				case SINGLE_DATE_EVENT:
					Event sEvt = (Event)tasks.get(0);
					storage.addTask(sEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "<html><b>\"" + sEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event on <b>" + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + "</b> at <b>" + parser.formatDate(sEvt.getFromDate(), "h:mm a") + "</b>.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
						System.out.println(sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
					}
					undoredo.addUndoable(obj);
					return true;
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)tasks.get(0);
					storage.addTask(dEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "<html><b>\"" + dEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event from <b>" + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "</b> to <b>" + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + "</b>.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
						System.out.println(dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
					}
					undoredo.addUndoable(obj);
					return true;
				case TODO:
					Todo flt = (Todo)tasks.get(0);
					storage.addTask(flt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);
					taskType = EnumTypes.TASK_TYPE.TODO;
					message = "<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, flt.getTaskID() + ", " + flt.getTaskDesc());
						System.out.println(flt.getTaskID() + ", " + flt.getTaskDesc());
					}
					undoredo.addUndoable(obj);
					return true;
				case DEADLINE:
					Deadline dt = (Deadline)tasks.get(0);
					storage.addTask(dt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					message = "<html><b>\"" + dt.getTaskDesc() + "\"</b><br/>has been successfully added as a Deadline task that must be completed by <b>" + parser.formatDate(dt.getDate(), "EEE, d MMM yyyy") + "</b>.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
						System.out.println(dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					}
					undoredo.addUndoable(obj);
					return true;
				default:
					taskType = EnumTypes.TASK_TYPE.INVALID;
					message = "Add command has failed.";
					return false;
			}
		}
		if (DEBUG) {
			System.out.println();
		}

		taskType = EnumTypes.TASK_TYPE.INVALID;
		message = "Add command has failed.";
		return false;
	}

	/*
	@Override
	public void undo(ParsedObject obj) {
		Task t = null;
		switch (obj.getTaskType()) {
		case SINGLE_DATE_EVENT:
		case DOUBLE_DATE_EVENT:
			t = (Event)obj.getObjects().get(0);
			if (DEBUG) {
				System.out.println("Task ID " + t.getTaskID());
			}
			break;
		case TODO:
			t = (Todo)obj.getObjects().get(0);
			if (DEBUG) {
				System.out.println("Task ID " + t.getTaskID());
			}
			break;
		case DEADLINE:
			t = (Todo)obj.getObjects().get(0);
			if (DEBUG) {
				System.out.println("Task ID " + t.getTaskID());
			}
			break;
		}
		storage.delete(t.getTaskID());
	}

	@Override
	public void redo(ParsedObject obj) {
		ArrayList<Task> temp = obj.getObjects();
		for (int i = 0; i < temp.size(); i++) {
			Task t = temp.get(i);
			if (t instanceof Event) {
				t = (Event)t;
			} else if (t instanceof Todo) {
				t = (Todo)t;
			} else if (t instanceof Deadline) {
				t = (Deadline)t;
			}
			storage.addTask(t);
		}
	}*/
}

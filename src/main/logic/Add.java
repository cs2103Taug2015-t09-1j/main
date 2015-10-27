/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.corba.se.impl.util.Version;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.parser.Parser;
import main.storage.Storage;

/**
 * @author Dalton
 *
 */
public class Add extends Command {
	private static final Parser parser = Parser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final VersionControl vControl = VersionControl.getInstance();
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
					
					addNewTask(sEvt);
					
					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "<html><b>\"" + sEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event on <b>" + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + "</b> at <b>" + parser.formatDate(sEvt.getFromDate(), "h:mm a") + "</b>.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
						System.out.println(sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
					}
					return true;
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)tasks.get(0);
					storage.addTask(dEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);
					
					addNewTask(dEvt);
					
					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "<html><b>\"" + dEvt.getTaskDesc() + "\"</b><br/>has been successfully added as an Event from <b>" + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "</b> to <b>" + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + "</b>.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
						System.out.println(dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
					}
					return true;
				case TODO:
					Todo flt = (Todo)tasks.get(0);
					storage.addTask(flt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);
					
					addNewTask(flt);
					
					taskType = EnumTypes.TASK_TYPE.TODO;
					message = "<html><b>\"" + flt.getTaskDesc() + "\"</b><br/>has been successfully added as a Todo task.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, flt.getTaskID() + ", " + flt.getTaskDesc());
						System.out.println(flt.getTaskID() + ", " + flt.getTaskDesc());
					}
					return true;
				case DEADLINE:
					Deadline dt = (Deadline)tasks.get(0);
					storage.addTask(dt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);
					
					addNewTask(dt);
					
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					message = "<html><b>\"" + dt.getTaskDesc() + "\"</b><br/>has been successfully added as a Deadline task that must be completed by <b>" + parser.formatDate(dt.getDate(), "EEE, d MMM yyyy") + "</b>.</html>";

					if (DEBUG) {
						logger.log(Level.FINE, dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
						System.out.println(dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					}
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
	
	public static boolean undo(Task task) {
		return storage.delete(task.getTaskID());
	}
	
	public static boolean redo(Task task) {
		return storage.addTask(task);
	}
	
	private static void addNewTask(Task task) {
		vControl.addNewData(new VersionModel.AddModel(task));
	}
}

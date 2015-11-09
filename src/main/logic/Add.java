/*
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.parser.Parser;
import main.storage.LogFileHandler;
import main.storage.Storage;

/**
 * The Class Add.
 * Handles the add command based on the ParsedObject returned by the parser
 * after being direct to from the controller.
 *
 * @@author Dalton
 */
public class Add extends Command {
	private static Add add = null;
	private static Storage storage = null;
	private static Parser parser = null;
	private static VersionControl vControl = null;
	private static final Logger logger = Logger.getLogger(Add.class.getName());
	private static final boolean DEBUG = true;

	/**
	 * Instantiates a new add.
	 */
	private Add() {
		storage = Storage.getInstance();
		parser = Parser.getInstance();
		vControl = VersionControl.getInstance();
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	/**
	 * Gets the single instance of Add.
	 *
	 * @return single instance of Add
	 */
	public static Add getInstance() {
		if (add == null) {
			add = new Add();
		}
		return add;
	}

	/**
	 * Executes the Add command
	 *
	 * @param ParsedObject	the ParsedObject containing command information from the Parser
	 * @return 				true if successfully deleted
	 */
	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		assert obj.getObjects() instanceof ArrayList;

		ArrayList<Task> tasks = obj.getObjects();

		if (!tasks.isEmpty()) {
			Task task = tasks.get(0);
			message = "\"" + task.getTaskDesc() + "\" has been successfully added as ";
			switch (obj.getTaskType()) {
				case SINGLE_DATE_EVENT:
					Event sEvt = (Event)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					addNewTask(sEvt);
					setMessage(sEvt, EnumTypes.TASK_TYPE.SINGLE_DATE_EVENT);

					if (DEBUG) {
						logger.log(Level.FINE, "Event Added: {" + sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate() + "}");
					}

					return true;
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					addNewTask(dEvt);
					setMessage(dEvt, EnumTypes.TASK_TYPE.DOUBLE_DATE_EVENT);

					if (DEBUG) {
						logger.log(Level.FINE, "Event Added: {" + dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate() + "}");
					}
					return true;
				case TODO:
					Todo tt = (Todo)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.TODO;
					addNewTask(tt);
					setMessage(tt, taskType);

					if (DEBUG) {
						logger.log(Level.FINE, "Todo Added: {" + tt.getTaskID() + ", " + tt.getTaskDesc() + "}");
					}

					return true;
				case DEADLINE:
					Deadline dt = (Deadline)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					addNewTask(dt);
					setMessage(dt, taskType);

					if (DEBUG) {
						logger.log(Level.FINE, "Deadline Added: {" + dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate() + "}");
					}

					return true;
				default:
					// default case handled outside
			}
		}

		taskType = EnumTypes.TASK_TYPE.INVALID;
		setMessage(null, taskType);
		if (DEBUG) {
			logger.log(Level.SEVERE, "Add Command Failed: " + obj);
		}

		taskType = EnumTypes.TASK_TYPE.INVALID;
		message = "Add command has failed.";

		return false;
	}

	/**
	 * Undo Add.
	 *
	 * @param task	the task
	 * @return 		true, if successful
	 */
	public boolean undo(Task task) {
		return storage.delete(task.getTaskID());
	}

	/**
	 * Redo Add.
	 *
	 * @param task		the task
	 * @return 			true, if successful
	 */
	public boolean redo(Task task) {
		return storage.addTask(task);
	}

	/**
	 * Adds the new task.
	 *
	 * @param task	the task
	 */
	private void addNewTask(Task task) {
		storage.addTask(task);
		storage.saveTaskType(taskType);
		vControl.addNewData(new VersionModel.AddModel(task));
	}

	/**
	 * Sets the feedback message.
	 *
	 * @param task			the task
	 * @param specficType		the specfic type
	 */
	private void setMessage(Task task, EnumTypes.TASK_TYPE specficType) {
		switch (specficType) {
			case SINGLE_DATE_EVENT:
				Event sdEvt = (Event)task;
				message += "an Event on " + parser.formatDate(sdEvt.getFromDate(), "EEE, d MMM yyyy") + " at " + parser.formatDate(sdEvt.getFromDate(), "h:mm a") + ".";
				break;
			case DOUBLE_DATE_EVENT:
				Event ddEvt = (Event)task;
				message += "an Event from " + parser.formatDate(ddEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + " to " + parser.formatDate(ddEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + ".";
				break;
			case DEADLINE:
				Deadline dd = (Deadline)task;
				message += "a Deadline task that must be completed by " + parser.formatDate(dd.getDate(), "h:mm aa") + " on " + parser.formatDate(dd.getDate(), "EEE, d MMM yyyy") + ".";
				break;
			case TODO:
				message += "a Todo task.";
				break;
			default:
				message = "Add command has failed.";
		}
	}
}

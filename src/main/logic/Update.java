/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.parser.Parser;
import main.storage.Storage;

/**
 * @@author Dalton
 *
 */
public class Update extends Command {
	private static Update update = null;
	private static Parser parser = null;
	private static Storage storage = null;
	private static VersionControl vControl = null;
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final boolean DEBUG = true;

	private Update() {
		parser = Parser.getInstance();
		storage = Storage.getInstance();
		vControl = VersionControl.getInstance();
	}

	public static Update getInstance() {
		if (update == null) {
			update = new Update();
		}
		return update;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		assert obj.getObjects() instanceof ArrayList;

		ArrayList<String> params = obj.getObjects();
		Task task = storage.getTaskByID(parser.parseInteger(params.get(0)));

		if (task != null) {
			message = "Task ID " + task.getTaskID() + ": ";
			switch (task.getType()) {
				case EVENT:
					taskType = EnumTypes.TASK_TYPE.EVENT;
					return updateEvent((Event) task, params);
				case TODO:
					taskType = EnumTypes.TASK_TYPE.TODO;
					return updateTodo((Todo) task, params);
				case DEADLINE:
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					return updateDeadline((Deadline) task, params);
			}
		}

		message = "Invalid column or value entered.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	private boolean updateEvent(Event evt, ArrayList<String> params) {
		Task oldEvt = evt.clone();

		switch (params.get(1)) {
			case "2":
				try {
					Date fromDate = parser.parseDateGroups(params.get(2)).get(0);
					message += "\"" + parser.formatDate(evt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "\" has been updated to \""
							+ parser.formatDate(fromDate,  "EEE, d MMM yyyy h:mm a") + "\".";
					evt.setFromDate(fromDate);
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "3":
				try {
					Date toDate = parser.parseDateGroups(params.get(2)).get(0);
					message += "\"" + parser.formatDate(evt.getToDate(),  "EEE, d MMM yyyy h:mm a") + "\" has been updated to \""
							+ parser.formatDate(toDate,  "EEE, d MMM yyyy h:mm a") + "\".";
					evt.setToDate(toDate);
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "4":
				String taskDesc = params.get(2);
				message += "\"" + evt.getTaskDesc() + "\" has been updated to \"" + taskDesc + "\".";
				evt.setTaskDesc(taskDesc);
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}

		storage.updateTask(evt);
		storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);

		addNewUpdateModel(oldEvt, evt);

		return true;
	}

	private boolean updateTodo(Todo t, ArrayList<String> params) {
		Task oldTodo = t.clone();

		switch (params.get(1)) {
			case "2":
				String taskDesc = params.get(2);
				t.setTaskDesc(taskDesc);
				message += "Task Description has been updated to " + taskDesc + ".";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}

		storage.updateTask(t);
		storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);

		addNewUpdateModel(oldTodo, t);

		return true;
	}

	private boolean updateDeadline(Deadline d, ArrayList<String> params) {
		Task oldDeadline = d.clone();

		switch (params.get(1)) {
			case "2":
				try {
					Date deadline = parser.parseDateGroups(params.get(2)).get(0);
					d.setDate(deadline);
					message += "Deadline has been updated to " + parser.formatDate(deadline,  "EEE, d MMM yyyy") + ".";
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "3":
				String taskDesc = params.get(2);
				d.setTaskDesc(taskDesc);
				message += "Task Description has been updated to " + taskDesc + ".";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}

		storage.updateTask(d);
		storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);

		addNewUpdateModel(oldDeadline, d);

		return true;
	}

	// @@author Hiep
	private void addNewUpdateModel(Task oldTask, Task newTask) {
		vControl.addNewData(new VersionModel.UpdateModel(oldTask, newTask));
	}

	// @@author Hiep
	public static boolean undo(Task oldTask) {
		return storage.updateTask(oldTask);
	}

	// @@author Hiep
	public static boolean redo(Task newTask) {
		return storage.updateTask(newTask);
	}
}

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
import main.model.EnumTypes.COMMAND_TYPE;
import main.model.EnumTypes.TASK_TYPE.*;
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
public class Update extends Command {
	private static final Parser parser = Parser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final VersionControl vControl = VersionControl.getInstance();
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final boolean DEBUG = true;
	private static Update update = null;

	private Update() {}

	public static Update getInstance() {
		if (update == null) {
			update = new Update();
		}
		return update;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		ArrayList<String> params = obj.getObjects();
		Task t = storage.getTaskByID(parser.parseInteger(params.get(0)));
		if (t != null) {
			message = "<html>Task ID " + t.getTaskID() + ": ";
			switch (t.getType()) {
				case EVENT:
					taskType = EnumTypes.TASK_TYPE.EVENT;
					return updateEvent((Event) t, params);
				case TODO:
					taskType = EnumTypes.TASK_TYPE.TODO;
					return updateTodo((Todo) t, params);
				case DEADLINE:
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					return updateDeadline((Deadline) t, params);
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
					Date fromDate = parser.getDateList(params.get(2)).get(0);
					evt.setFromDate(fromDate);
					message += "Start Date has been updated to <b>" + parser.formatDate(fromDate,  "EEE, d MMM yyyy") + "</b>.</html>";
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "3":
				try {
					Date toDate = parser.getDateList(params.get(2)).get(0);
					evt.setToDate(toDate);
					message += "End Date has been updated to <b>" + parser.formatDate(toDate,  "EEE, d MMM yyyy") + "</b>.</html>";
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "4":
				String taskDesc = params.get(2);
				evt.setTaskDesc(taskDesc);
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
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
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
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
					Date deadline = parser.getDateList(params.get(2)).get(0);
					d.setDate(deadline);
					message += "Deadline has been updated to <b>" + parser.formatDate(deadline,  "EEE, d MMM yyyy") + "</b>.</html>";
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "3":
				String taskDesc = params.get(2);
				d.setTaskDesc(taskDesc);
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
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
	
	private void addNewUpdateModel(Task oldTask, Task newTask) {
		vControl.addNewData(new VersionModel.UpdateModel(oldTask, newTask));
	}
	
	public static boolean undo(Task oldTask) {
		return storage.updateTask(oldTask);
	}
	
	public static boolean redo(Task newTask) {
		return storage.updateTask(newTask);
	}
}

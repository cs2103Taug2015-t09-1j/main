/**
 *
 */
package logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import models.Deadline;
import models.EnumTypes;
import models.Event;
import models.ParsedObject;
import models.Task;
import models.Todo;
import parser.MainParser;
import storage.Storage;

/**
 * @author Dalton
 *
 */
public class Update extends Command {
	private static final MainParser parser = MainParser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final boolean DEBUG = true;
	private static Update update = Update.getInstance();

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
			if (t instanceof Event) {
				taskType = EnumTypes.TASK_TYPE.EVENT;
				return updateEvent((Event) t, params);
			} else if (t instanceof Todo) {
				taskType = EnumTypes.TASK_TYPE.TODO;
				return updateTodo((Todo) t, params);
			} else if (t instanceof Deadline) {
				taskType = EnumTypes.TASK_TYPE.DEADLINE;
				return updateDeadline((Deadline) t, params);
			}
		}
		message += "Invalid column or value entered.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	private boolean updateEvent(Event evt, ArrayList<String> params) {
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

		storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);
		return true;
	}

	private boolean updateTodo(Todo t, ArrayList<String> params) {
		switch (params.get(1)) {
			case "2":
				String taskDesc = params.get(1);
				t.setTaskDesc(taskDesc);
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}
		storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);
		return true;
	}

	private boolean updateDeadline(Deadline d, ArrayList<String> params) {
		switch (params.get(1)) {
			case "2":
				try {
					Date deadline = parser.getDateList(params.get(1)).get(0);
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
		storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);
		return true;
	}
}

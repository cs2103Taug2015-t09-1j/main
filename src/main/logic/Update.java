/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import main.model.EnumTypes;
import main.model.ParsedObject;
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
	private static final UndoRedo undoredo = UndoRedo.getInstance();
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final boolean DEBUG = true;
	private static Update update = null;
	private static ArrayList<String> backup;
	private boolean isReverseCmd = false;

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
		if (!isReverseCmd) {
			undoredo.resetIsUpdateStatus();
		}
		backup = new ArrayList<String>();
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
		message += "Invalid column or value entered.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	private boolean updateEvent(Event evt, ArrayList<String> params) {
		if (!isReverseCmd) {
			cloneParamsList(params);
		}

		switch (params.get(1)) {
			case "2":
				try {
					Date fromDate = parser.getDateList(params.get(2)).get(0);
					if (!isReverseCmd) {
						backup.set(2, evt.getFromDate().toString());
						backup.add(fromDate.toString());
					}
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
					if (!isReverseCmd) {
						backup.set(2, evt.getToDate().toString());
						backup.add(toDate.toString());
					}
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
				if (!isReverseCmd) {
					backup.set(2, evt.getTaskDesc());
					backup.add(taskDesc);
				}
				evt.setTaskDesc(taskDesc);
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}
		if (!isReverseCmd) {
			undoredo.addUndoable(new ParsedObject(EnumTypes.COMMAND_TYPE.UPDATE, null, backup));
		}
		storage.updateTask(evt);
		storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);
		return true;
	}

	private boolean updateTodo(Todo t, ArrayList<String> params) {
		if (!isReverseCmd) {
			cloneParamsList(params);
		}

		switch (params.get(1)) {
			case "2":
				if (!isReverseCmd) {
					backup.set(2, t.getTaskDesc());
				}
				String taskDesc = params.get(2);
				t.setTaskDesc(taskDesc);
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}
		if (!isReverseCmd) {
			undoredo.addUndoable(new ParsedObject(EnumTypes.COMMAND_TYPE.UPDATE, null, backup));
		}
		
		storage.updateTask(t);
		storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);
		return true;
	}

	private boolean updateDeadline(Deadline d, ArrayList<String> params) {
		if (!isReverseCmd) {
			cloneParamsList(params);
		}

		switch (params.get(1)) {
			case "2":
				if (!isReverseCmd) {
					backup.set(2, d.getDate().toString());
				}
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
				if (!isReverseCmd) {
					backup.set(2, d.getTaskDesc());
				}
				String taskDesc = params.get(2);
				d.setTaskDesc(taskDesc);
				message += "Task Description has been updated to <b>" + taskDesc + "</b>.</html>";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}
		if (!isReverseCmd) {
			undoredo.addUndoable(new ParsedObject(EnumTypes.COMMAND_TYPE.UPDATE, null, backup));
		}
		
		storage.updateTask(d);
		storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);
		return true;
	}

	/**
	 * @param isReverseCmd the isReverseCmd to set
	 */
	public void setReverseCmd(boolean isReverseCmd) {
		this.isReverseCmd = isReverseCmd;
	}

	private void cloneParamsList(ArrayList<String> params) {
		for (String s : params) {
			backup.add(s);
		}
	}
}

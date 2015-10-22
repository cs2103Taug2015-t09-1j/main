/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import main.models.Deadline;
import main.models.EnumTypes;
import main.models.Event;
import main.models.ParsedObject;
import main.models.Task;
import main.models.Todo;
import main.models.EnumTypes.COMMAND_TYPE;
import main.models.EnumTypes.TASK_TYPE;
import main.parser.MainParser;
import main.storage.Storage;

/**
 * @author Dalton
 *
 */
public class Update extends Command {
	private static final MainParser parser = MainParser.getInstance();
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
				String taskDesc = params.get(1);
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

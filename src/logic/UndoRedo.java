/**
 *
 */
package logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.logging.Level;
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
public class UndoRedo extends Command {
	private static UndoRedo undoredo = null;
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final boolean DEBUG = true;
	private Deque<ParsedObject> undoables = new ArrayDeque<ParsedObject>();
	private Deque<ParsedObject> redoables = new ArrayDeque<ParsedObject>();
	private boolean isUndoable = false;
	private boolean isRedoable = false;
	private boolean isUpdateCmd = true;

	private UndoRedo() {}

	public static UndoRedo getInstance() {
		if (undoredo == null) {
			undoredo = new UndoRedo();
		}
		return undoredo;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		int numOfExec = (Integer)obj.getObjects().get(0);

		switch (obj.getCommandType()) {
			case UNDO:
				if (undoables.size() > 0) {
					numOfExec = undo(numOfExec);
					if (numOfExec > 0) {
						message = "<html>The previous " + numOfExec + " commands have been reversed.</html>";
						taskType = EnumTypes.TASK_TYPE.ALL;
						return true;
					} else {
						message = "<html>There are no available tasks to undo.</html>";
						taskType = EnumTypes.TASK_TYPE.INVALID;
						return false;
					}
				} else {
					message = "<html>There are no available tasks to undo.</html>";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
			case REDO:
				if (redoables.size() > 0) {
					numOfExec = redo(numOfExec);
					if (numOfExec > 0) {
						message = "<html>The previous " + numOfExec + " undo commands have been reversed.</html>";
						taskType = EnumTypes.TASK_TYPE.ALL;
						return true;
					} else {
						message = "<html>There are no available tasks to redo.</html>";
						taskType = EnumTypes.TASK_TYPE.INVALID;
						return false;
					}
				} else {
					message = "<html>There are no available tasks to redo.</html>";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
			default:
				message = "<html>There are no available tasks to undo or redo.</html>";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}
	}

	private int undo(int numOfExec) {
		int successCount = 0;
		for (int i = 0; i < numOfExec; i++) {
			ParsedObject obj = getNextUndoable();
			if (obj != null) {
				switch (obj.getCommandType()) {
					case ADD:
						undoAdd(obj);
						successCount++;
						break;
					case DELETE:
						undoDelete(obj);
						successCount++;
						break;
					case UPDATE:
						reverseUpdate(obj);
						successCount++;
						break;
					default:
						message = "<html>There are no available tasks to undo.</html>";
						taskType = EnumTypes.TASK_TYPE.INVALID;
				}
			} else {
				isUndoable = false;
			}
		}
		storage.saveAllTask();
		return successCount;
	}

	private int redo(int numOfExec) {
		int successCount = 0;
		for (int i = 0; i < numOfExec; i++) {
			ParsedObject obj = getNextRedoable();
			if (obj != null) {
				switch (obj.getCommandType()) {
					case ADD:
						redoAdd(obj);
						successCount++;
						break;
					case DELETE:
						redoDelete(obj);
						successCount++;
						break;
					case UPDATE:
						reverseUpdate(obj);
						successCount++;
						break;
					default:
						message = "<html>There are no available tasks to redo.</html>";
						taskType = EnumTypes.TASK_TYPE.INVALID;
				}
			} else {
				isUndoable = false;
			}
		}
		storage.saveAllTask();
		return successCount;
	}

	public void addUndoable(ParsedObject obj) {
		if (obj != null) {
			undoables.push(obj);
			isUndoable = true;
		}
	}

	private ParsedObject getNextUndoable() {
		if (isUndoable) {
			ParsedObject undoable = undoables.pop();
			if (undoables.size() == 0) {
				isUndoable = false;
			}
			addRedoable(undoable);
			return undoable;
		}
		return null;
	}

	private void addRedoable(ParsedObject obj) {
		if (obj != null) {
			redoables.push(obj);
			isRedoable = true;
		}
	}

	private ParsedObject getNextRedoable() {
		if (isRedoable) {
			ParsedObject redoable = redoables.pop();
			if (redoables.size() == 0) {
				isRedoable = false;
			}
			addUndoable(redoable);
			return redoable;
		}
		return null;
	}

	private void undoAdd(ParsedObject obj) {
		Task t = null;
		switch (obj.getTaskType()) {
			case SINGLE_DATE_EVENT:
			case DOUBLE_DATE_EVENT:
				t = (Event)obj.getObjects().get(0);
				break;
			case TODO:
				t = (Todo)obj.getObjects().get(0);
				break;
			case DEADLINE:
				t = (Todo)obj.getObjects().get(0);
				break;
		}

		if (DEBUG) {
			System.out.println("Task ID " + t.getTaskID());
		}
		storage.delete(t.getTaskID());
	}

	private void undoDelete(ParsedObject obj) {
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
	}

	private void redoDelete(ParsedObject obj) {
		ArrayList<Task> temp = obj.getObjects();
		for (int i = 0; i < temp.size(); i++) {
			storage.delete(((Task)temp.get(i)).getTaskID());
		}
	}

	private void redoAdd(ParsedObject obj) {
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
	}

	private void reverseUpdate(ParsedObject obj) {
		if (!isUpdateCmd) {
			ArrayList temp = obj.getObjects();
			Object saved = temp.get(temp.size()-1);
			Object updated = temp.get(2);
			temp.set(2, saved);
			temp.set(temp.size()-1, updated);
		}
		Update.getInstance().setReverseCmd(true);
		Update.getInstance().execute(obj);
		Update.getInstance().setReverseCmd(false);
		isUpdateCmd = false;
	}

	public void resetIsUpdateStatus() {
		isUpdateCmd = true;
	}
}

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
	private static UndoRedo undo = UndoRedo.getInstance();
	private static final MainParser parser = MainParser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final boolean DEBUG = true;
	private Deque<ParsedObject> undoables = new ArrayDeque<ParsedObject>();
	private Deque<ParsedObject> redoables = new ArrayDeque<ParsedObject>();
	private boolean isUndoable = false;
	private boolean isRedoable = false;

	private UndoRedo() {}

	public static UndoRedo getInstance() {
		if (undo == null) {
			undo = new UndoRedo();
		}
		return undo;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;

		if (DEBUG) {
			System.out.println(obj.getCommandType());
			System.out.println(obj.getTaskType());
		}

		switch (obj.getCommandType()) {
			case UNDO:
				return Undo((Integer)obj.getObjects().get(0), getNextUndoable());
			case REDO:
				return Redo((Integer)obj.getObjects().get(0), getNextRedoable());
		}

		message = "<html>There are no available tasks to undo.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	private boolean Undo(int numOfExec, ParsedObject obj) {
		if (numOfExec > 0) {
			if (obj != null) {
				for (int i = 0; i < numOfExec; i++) {
					switch (obj.getCommandType()) {
						case ADD:
							reverseAdd(obj);
							break;
						case DELETE:
							reverseDelete(obj);
							break;
						case UPDATE:
							reverseUpdate(obj);
							break;
						default:
							message = "<html>There are no available tasks to undo.</html>";
							taskType = EnumTypes.TASK_TYPE.INVALID;
							return false;
					}
				}
				storage.saveAllTask();
				message = "<html>The previous " + numOfExec + " commands have been reversed.</html>";
				taskType = EnumTypes.TASK_TYPE.ALL;
				return true;
			} else {
				isUndoable = false;
			}
		}
		message = "<html>There are no available tasks to undo.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	private boolean Redo(int numOfExec, ParsedObject obj) {
		if (numOfExec > 0) {
			if (obj != null) {
				for (int i = 0; i < numOfExec; i++) {
					switch (obj.getCommandType()) {
						case ADD:
							reverseDelete(obj);
							break;
						case DELETE:
							reverseAdd(obj);
							break;
						case UPDATE:
							reverseUpdate(obj);
							break;
						default:
							message = "<html>There are no available tasks to redo.</html>";
							taskType = EnumTypes.TASK_TYPE.INVALID;
							return false;
					}
				}
				storage.saveAllTask();
				message = "<html>The previous " + numOfExec + " undo commands have been reversed.</html>";
				taskType = EnumTypes.TASK_TYPE.ALL;
				return true;
			} else {
				isRedoable = false;
			}
		}
		message = "<html>There are no available tasks to redo.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
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
			addUndoable(redoable);
			return redoable;
		}
		return null;
	}

	private void reverseAdd(ParsedObject obj) {
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

	private void reverseDelete(ParsedObject obj) {
		ArrayList<Task> temp = obj.getObjects();
		for (int i = 0; i < temp.size(); i++) {
			storage.addTask(temp.get(i), obj.getTaskType());
		}
	}

	private void reverseUpdate(ParsedObject obj) {
		Update.getInstance().execute(obj);
	}
}

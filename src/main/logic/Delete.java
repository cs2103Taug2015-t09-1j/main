/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.logging.Logger;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.storage.Storage;

/**
 * @author Dalton
 *
 */
public class Delete extends Command {
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final boolean DEBUG = true;
	private static Delete delete = null;

	private Delete() {}

	public static Delete getInstance() {
		if (delete == null) {
			delete = new Delete();
		}
		return delete;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;

		if (DEBUG) {
			System.out.println(obj.getCommandType());
			System.out.println(obj.getTaskType());
		}

		ArrayList<Integer> taskIDs = obj.getObjects();
		ArrayList<Task> backup = new ArrayList<Task>();
		if (taskIDs.size() > 0) {
			int cnt = 0;
			for (int i = 0; i < taskIDs.size(); i++) {
				Task t = Storage.getInstance().getTaskByID(taskIDs.get(i));
				if (t != null) {
					backup.add(t);
					cnt++;

					storage.delete(taskIDs.get(i));

					if (DEBUG) {
						System.out.print(taskIDs.get(i));
						System.out.print(" | ");
					}
				}
			}
			storage.saveAllTask();
			message = String.format("<html> %d %s been deleted <html>", cnt, cnt > 1 ? "tasks have" : "task has");
			taskType = EnumTypes.TASK_TYPE.ALL;
			UndoRedo.getInstance().addUndoable(new ParsedObject(EnumTypes.COMMAND_TYPE.DELETE, null, backup));
			return true;
		}
		if (DEBUG) {
			System.out.println();
		}
		message = "<html> Invalid task ids. Please try again.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	/*
	@Override
	public void undo(ParsedObject obj) {
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

	@Override
	public void redo(ParsedObject obj) {
		ArrayList<Task> temp = obj.getObjects();
		for (int i = 0; i < temp.size(); i++) {
			storage.delete(((Task)temp.get(i)).getTaskID());
		}
	}*/
}

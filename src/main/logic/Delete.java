/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
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
	private static final VersionControl vControl = VersionControl.getInstance();
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
		List<Task> deletedTasks = new ArrayList<>();
		int cnt = 0;
		for (int i = 0; i < taskIDs.size(); i++) {
			Task t = storage.getTaskByID(taskIDs.get(i));
			if (t != null) {
				cnt++;
				if (storage.delete(taskIDs.get(i))) {
					deletedTasks.add(t);
				}

				if (DEBUG) {
					System.out.print(taskIDs.get(i));
					System.out.print(" | ");
				}
			}
		}
		if (cnt > 0) {
			storage.saveAllTask();
			message = String.format("%d %s been deleted.", cnt, cnt > 1 ? "tasks have" : "task has");
			taskType = EnumTypes.TASK_TYPE.ALL;
			vControl.addNewData(new VersionModel.DeleteModel(deletedTasks));
			return true;
		}
		if (DEBUG) {
			System.out.println();
		}
		message = "Invalid Task ID. Please try again.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	public boolean undo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.addTask(task);
		}
		return true;
	}

	public boolean redo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.delete(task.getTaskID());
		}
		return true;
	}
}

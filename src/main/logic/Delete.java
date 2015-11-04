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
 * @@author Dalton
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

		int cnt = 0;
		List<Integer> taskIDs = new ArrayList<Integer>();
		List<Task> deletedTasks = new ArrayList<Task>();
		if (obj.getParamType() != null) {
			switch (obj.getParamType()) {
			case ID:
				taskIDs = obj.getObjects();
				break;
			case CATEGORY:
				taskIDs = storage.getIdByCategory(obj.getObjects());
				cnt = taskIDs.size()-1;
				break;
			default:
				message = "Invalid Task ID. Please try again.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
			}

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

			if (DEBUG) {
				System.out.println();
			}

			storage.saveAllTask();
			message = String.format("%d %s been deleted.", cnt, cnt > 1 ? "tasks have" : "task has");
			taskType = EnumTypes.TASK_TYPE.ALL;
			vControl.addNewData(new VersionModel.DeleteModel(deletedTasks));
			return true;
		} else {
			message = "Invalid Task ID. Please try again.";
			taskType = EnumTypes.TASK_TYPE.INVALID;
			return false;
		}
	}

	// @@author Hiep
	public boolean undo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.addTask(task);
		}
		return true;
	}

	// @@author Hiep
	public boolean redo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.delete(task.getTaskID());
		}
		return true;
	}
}

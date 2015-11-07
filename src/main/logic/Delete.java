/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.storage.LogFileHandler;
import main.storage.Storage;

/**
 * @@author Dalton
 *
 */
public class Delete extends Command {
	private static Delete delete = null;
	private static Storage storage = null;
	private static VersionControl vControl = null;
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final boolean DEBUG = true;


	private Delete() {
		storage = Storage.getInstance();
		vControl = VersionControl.getInstance();
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	public static Delete getInstance() {
		if (delete == null) {
			delete = new Delete();
		}
		return delete;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		assert obj.getObjects() instanceof ArrayList;

		List<Integer> taskIDs = new ArrayList<Integer>();
		List<Task> deletedTasks = new ArrayList<Task>();
		if (obj.getParamType() != null) {
			switch (obj.getParamType()) {
				case ID:
					taskIDs = obj.getObjects();
					break;
				case CATEGORY:
					taskIDs = storage.getIdByCategory(obj.getObjects());
					break;
				default:
					message = String.format("Invalid parameters for Delete command. Please try again.");
					taskType = EnumTypes.TASK_TYPE.INVALID;

					if (DEBUG) {
						logger.log(Level.SEVERE, "Delete Command Failed." + obj);
					}

					return false;
			}

			deletedTasks = deleteTasks(taskIDs, deletedTasks);

			if (deletedTasks.size() == 0) {
				message = String.format("Invalid parameters for Delete command. Please try again.");
				taskType = EnumTypes.TASK_TYPE.INVALID;

				if (DEBUG) {
					logger.log(Level.SEVERE, "Delete Command Failed." + obj);
				}
				return false;
			}

			if (DEBUG) {
				String debugMsg = "Tasks deleted successfully: {";
				for (Task t : deletedTasks) {
					debugMsg += t.getTaskID() + " ";
				}
				logger.log(Level.FINE, "Tasks deleted successfully: {" + debugMsg + "}");
			}

			storage.saveAllTask();
			vControl.addNewData(new VersionModel.DeleteModel(deletedTasks));
			taskType = EnumTypes.TASK_TYPE.ALL;
			message = String.format("%d %s been successfully deleted.", deletedTasks.size(), deletedTasks.size() > 1 ? "tasks have" : "task has");
			return true;
		}

		message = String.format("Invalid parameters for Delete command. Please try again.");
		taskType = EnumTypes.TASK_TYPE.INVALID;

		if (DEBUG) {
			logger.log(Level.SEVERE, "Delete Command Failed." + obj);
		}

		return false;
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

	// @@author Dalton
	private List<Task> deleteTasks(List<Integer> taskIDs, List<Task> deletedTasks) {
		for (int i = 0; i < taskIDs.size(); i++) {
			Task task = storage.getTaskByID(taskIDs.get(i));
			if (task != null) {
				if (storage.delete(taskIDs.get(i))) {
					deletedTasks.add(task);
				}
			}
		}
		return deletedTasks;
	}
}

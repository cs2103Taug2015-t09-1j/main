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
import main.model.taskModels.Task;
import main.storage.LogFileHandler;
import main.storage.Storage;

/**
 * The Class Delete.
 * Handles the delete command based on the ParsedObject returned by the parser
 * after being direct to from the controller.
 *
 * @@author Dalton
 */
public class Delete extends Command {
	private static Delete delete = null;
	private static Storage storage = null;
	private static VersionControl vControl = null;
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final boolean DEBUG = true;

	/**
	 * Instantiates a new delete.
	 */
	private Delete() {
		storage = Storage.getInstance();
		vControl = VersionControl.getInstance();
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	/**
	 * Gets the single instance of Delete.
	 *
	 * @return single instance of Delete
	 */
	public static Delete getInstance() {
		if (delete == null) {
			delete = new Delete();
		}
		return delete;
	}

	/**
	 * Executes the Delete command
	 *
	 * @param ParsedObject	the ParsedObject containing command information from the Parser
	 * @return 				true if successfully deleted
	 */
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

	/**
	 * Undo Delete.
	 *
	 * @@author Hiep
	 *
	 * @param tasks		the tasks
	 * @return 			true, if successful
	 */
	public boolean undo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.addTask(task);
		}
		return true;
	}

	/**
	 * Redo Delete.
	 *
	 * @@author Hiep
	 *
	 * @param tasks		the tasks
	 * @return 			true, if successful
	 */
	public boolean redo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.delete(task.getTaskID());
		}
		return true;
	}

	/**
	 * Delete all tasks based on the list of task IDs.
	 *
	 * @@author Dalton
	 *
	 * @param taskIDs	the task IDs
	 * @param deletedTasks	the deleted tasks
	 * @return 				the list of deleted tasks
	 */
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

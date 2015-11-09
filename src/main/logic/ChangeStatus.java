/*
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.model.taskModels.Task;
import main.storage.Storage;

/**
 * The Class ChangeStatus.
 * Handles the Done and Undone commands.
 *
 * @@author Hiep
 */
public class ChangeStatus extends Command {
	private static ChangeStatus changeStatus = null;
	private static Storage storage = null;
	private static final VersionControl vControl = VersionControl.getInstance();
	private boolean newStatus = true;

	/**
	 * Instantiates a new change status.
	 *
	 * @param newStatus		the new status
	 */
	private ChangeStatus(boolean newStatus) {
		this.newStatus = newStatus;
		storage = Storage.getInstance();
	}

	/**
	 * Gets the single instance of ChangeStatus.
	 *
	 * @param newStatus		the new status
	 * @return 				single instance of ChangeStatus
	 */
	public static ChangeStatus getInstance(boolean newStatus) {
		if (changeStatus == null) {
			changeStatus = new ChangeStatus(newStatus);
		}
		changeStatus.newStatus = newStatus;
		return changeStatus;
	}

	/**
	 * Gets the single instance of ChangeStatus.
	 *
	 * @return single instance of ChangeStatus
	 */
	public static ChangeStatus getInstance() {
		return getInstance(true);
	}

	/**
	 * Executes the ChangeStatus command
	 *
	 * @param ParsedObject	the ParsedObject containing command information from the Parser
	 * @return 				true if successfully deleted
	 */
	@Override
	public boolean execute(ParsedObject obj) {
		List<Integer> taskIDs = new ArrayList<>();
		switch (obj.getParamType()) {
		case ID:
			taskIDs = obj.getObjects();
			break;
		case CATEGORY:
			taskIDs = storage.getIdByCategory(obj.getObjects());
			break;
		default:
		}

		List<Integer> ids = new ArrayList<>();
		List<Boolean> oldStatuses = new ArrayList<>();

		int cnt = 0;
		for (int i = 0; i < taskIDs.size(); i++) {
			Task t = Storage.getInstance().getTaskByID(taskIDs.get(i));
			if (t != null) {
				cnt++;
				boolean oldStatus = t.isDone();
				t.setDone(newStatus);
				if (storage.updateTask(t)) {
					ids.add(t.getTaskID());
					oldStatuses.add(oldStatus);
				}
			}
		}
		if (cnt > 0) {
			storage.saveAllTask();
			message = String.format("%d %s been marked as %s ", cnt, cnt > 1 ? "tasks have" : "task has", newStatus ? "completed" : "incompleted");
			taskType = EnumTypes.TASK_TYPE.ALL;
			vControl.addNewData(new VersionModel.ChangeStatusModel(ids, oldStatuses, newStatus));
			return true;
		}

		message = "Invalid Task IDs. Please try again.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	/**
	 * Undo ChangeStatus.
	 *
	 * @param ids			the ids
	 * @param oldStatuses	the old statuses
	 * @return 				true, if successful
	 */
	public boolean undo(List<Integer> ids, List<Boolean> oldStatuses) {
		for (int i = 0; i < ids.size(); i++) {
			storage.changeStatus(ids.get(i), oldStatuses.get(i));
		}
		return true;
	}

	/**
	 * Redo ChangeStatus.
	 *
	 * @param ids		the ids
	 * @param newStatus	the new status
	 * @return 			true, if successful
	 */
	public boolean redo(List<Integer> ids, boolean newStatus) {
		for (int i = 0; i < ids.size(); i++) {
			storage.changeStatus(ids.get(i), newStatus);
		}
		return true;
	}
}

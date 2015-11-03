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
public class ChangeStatus extends Command {
	private static final Storage storage = Storage.getInstance();
	private static final VersionControl vControl = VersionControl.getInstance();
	private static final boolean DEBUG = true;
	private static ChangeStatus changeStatus = null;
	private boolean newStatus = true;

	private ChangeStatus(boolean newStatus) {
		this.newStatus = newStatus;
	}

	public static ChangeStatus getInstance(boolean newStatus) {
		if (changeStatus == null) {
			changeStatus = new ChangeStatus(newStatus);
		}
		changeStatus.newStatus = newStatus;
		return changeStatus;
	}

	public static ChangeStatus getInstance() {
		return changeStatus;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		ArrayList<Integer> taskIDs = obj.getObjects();

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

				if (DEBUG) {
					System.out.print(taskIDs.get(i));
					System.out.print(" | ");
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


		message = "<html> Invalid task ids. Please try again.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	public boolean undo(List<Integer> ids, List<Boolean> oldStatuses) {
		for (int i = 0; i < ids.size(); i++) {
			storage.changeStatus(ids.get(i), oldStatuses.get(i));
		}
		return true;
	}

	public boolean redo(List<Integer> ids, boolean newStatus) {
		for (int i = 0; i < ids.size(); i++) {
			storage.changeStatus(ids.get(i), newStatus);
		}
		return true;
	}

}

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
public class ChangeStatus extends Command {
	private static final Storage storage = Storage.getInstance();
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

	@Override
	public boolean execute(ParsedObject obj) {
		ArrayList<Integer> taskIDs = obj.getObjects();
		if (taskIDs.size() > 0) {
			int cnt = 0;
			for (int i = 0; i < taskIDs.size(); i++) {
				Task t = Storage.getInstance().getTaskByID(taskIDs.get(i));
				if (t != null) {
					cnt++;
					
					storage.changeStatus(taskIDs.get(i), newStatus);

					if (DEBUG) {
						System.out.print(taskIDs.get(i));
						System.out.print(" | ");
					}
				}
			}
			
			storage.saveAllTask();
			
			message = String.format("<html> %d %s been marked as %s</html>", cnt, cnt > 1 ? "tasks have" : "task has", newStatus ? "completed" : "incompleted");
			taskType = EnumTypes.TASK_TYPE.ALL;
			return true;
		}
		
		message = "<html> Invalid task ids. Please try again.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}
}

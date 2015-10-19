/**
 *
 */
package logic;

import java.util.ArrayList;
import java.util.logging.Logger;

import models.EnumTypes;
import models.ParsedObject;
import storage.Storage;

/**
 * @author Dalton
 *
 */
public class Delete extends Command {
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final boolean DEBUG = true;
	private static Delete delete = Delete.getInstance();

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
		if (taskIDs.size() > 0) {
			message = "<html>Tasks IDs: ";
			for (int i = 0; i < taskIDs.size(); i++) {
				storage.delete(taskIDs.get(i));
				message += ("<b>" + taskIDs.get(i) + "</b>");

				if (DEBUG) {
					System.out.print(taskIDs.get(i));
				}

				if (i < taskIDs.size()-1) {
					message += ", ";
					if (DEBUG) {
						System.out.print(" | ");
					}
				}
			}
			storage.saveAllTask();
			message += "<br/>have been deleted successfully.</html>";
			taskType = EnumTypes.TASK_TYPE.ALL;
			return true;
		}
		if (DEBUG) {
			System.out.println();
		}
		message += "Invalid Task IDs. Please try again.</html>";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}
}

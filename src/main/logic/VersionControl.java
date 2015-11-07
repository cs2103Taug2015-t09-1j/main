package main.logic;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.storage.Storage;

/**
 * 
 * @author Hiep
 * This class is used to manipulate undo/redo command
 */
public class VersionControl extends Command {
	
	// store necessary data for each function for undo/redo function 
	private List<VersionModel> vList = new ArrayList<>();
	
	// store current position in vList which user is inside
	private int curPosition = -1;

	private static VersionControl instance = null;

	private VersionControl() {
	}

	public static VersionControl getInstance() {
		if (instance == null) {
			instance = new VersionControl();
		}
		return instance;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		int numOfExec = (Integer) obj.getObjects().get(0);
		int count = 0;

		switch (obj.getCommandType()) {
		case UNDO:
			count = undo(numOfExec);
			if (count > 0) {
				message = "The previous " + count + " commands have been reversed.";
			} else {
				message = "There are no available tasks to undo.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
			}
			break;
		case REDO:
			count = redo(numOfExec);
			if (count > 0) {
				message = "The previous " + count + " commands have been reversed.";
			} else {
				message = "There are no available tasks to redo.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
			}
			break;
		}
			
		Storage.getInstance().saveAllTask();
		return true;
	}

	private int undo(int numOfExec) {
		int count = 0;
		while (numOfExec > 0 && curPosition >= 0) {
			VersionModel vModel = vList.get(curPosition);
			switch (vModel.getCmdType()) {
			case ADD:
				if (Add.getInstance().undo(((VersionModel.AddModel) vModel).getTask())) {
					count++;
				}
				break;
			case DELETE:
				if (Delete.getInstance().undo(((VersionModel.DeleteModel) vModel).getTasks())) {
					count++;
				}
				break;
			case UPDATE:
				if (Update.getInstance().undo(((VersionModel.UpdateModel) vModel).getOldTask())) {
					count++;
				}
				break;
			case DONE_UNDONE:
				if (ChangeStatus.getInstance().undo(((VersionModel.ChangeStatusModel) vModel).getIds(),
						((VersionModel.ChangeStatusModel) vModel).getOldStatuses())) {
					count++;
				}
				break;
			default:
				break;
			}
			numOfExec--;
			curPosition--;
		}
		return count;
	}

	private int redo(int numOfExec) {
		int count = 0;
		while (numOfExec > 0 && curPosition + 1 < vList.size()) {
			VersionModel vModel = vList.get(curPosition + 1);
			switch (vModel.getCmdType()) {
			case ADD:
				if (Add.getInstance().redo(((VersionModel.AddModel) vModel).getTask())) {
					count++;
				}
				break;
			case DELETE:
				if (Delete.getInstance().getInstance().redo(((VersionModel.DeleteModel) vModel).getTasks())) {
					count++;
				}
				break;
			case UPDATE:
				if (Update.getInstance().redo(((VersionModel.UpdateModel) vModel).getNewTask())) {
					count++;
				}
				break;
			case DONE_UNDONE:
				if (ChangeStatus.getInstance().redo(((VersionModel.ChangeStatusModel) vModel).getIds(),
						((VersionModel.ChangeStatusModel) vModel).getNewStatus())) {
					count++;
				}
				break;

			default:
				break;
			}
			numOfExec--;
			curPosition++;
		}
		return count;
	}

	public void addNewData(VersionModel vModel) {
		for (int i = vList.size() - 1; i > curPosition; i--) {
			vList.remove(i);
		}
		curPosition++;
		vList.add(vModel);
	}

}

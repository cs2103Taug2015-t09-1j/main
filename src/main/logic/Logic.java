package main.logic;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import main.model.ObserverEvent;
import main.model.ParsedObject;
import main.model.taskModels.Task;
import main.model.EnumTypes.TASK_TYPE;
import main.parser.Parser;
import main.storage.Storage;
import main.ui.MainGUI;

/**
 * @@author Dalton
 *
 */
public class Logic extends Observable implements Observer {
	private static Logic logic = null;
	private static Storage storage = null;
	private static Parser parser = null;
	private final Logger logger = Logger.getLogger(Logic.class.getName());

	private Logic() {}

	private static void initialise() {
		logic = new Logic();
		logic.addObserver(MainGUI.getInstance());

		storage = Storage.getInstance();
		parser = Parser.getInstance();

		logic.updateModelData(TASK_TYPE.DEADLINE, false);
		logic.updateModelData(TASK_TYPE.TODO, false);
		logic.updateModelData(TASK_TYPE.EVENT, true);
	}

	public static Logic getInstance() {
		if (logic == null) {
			initialise();
		}
		return logic;
	}

	public void processCommand(String input) {
		switch (parser.determineCommandType(input)) {
		case ADD:
			processAddCommand(input);
			break;
		case DISPLAY:
			processDisplayCommand(input);
			break;
		case UPDATE:
			processUpdateCommand(input);
			break;
		case DELETE:
			processDeleteCommand(input);
			break;
		case DONE:
			processChangeStatusCommand(input, true);
			break;
		case UNDONE:
			processChangeStatusCommand(input, false);
			break;
		case UNDO:
			processUndoCommand(input);
			break;
		case REDO:
			processRedoCommand(input);
			break;
		case EXIT:
			System.exit(0);
		default:
			updateMessage("Invalid command entered. Please try again.");
		}
	}

	private void processAddCommand(String input) {
		Add addCmd = Add.getInstance();
		if (addCmd.execute(parser.getAddParsedObject(input))) {
			updateModelData(addCmd.getTaskType(), true);
		}
		updateMessage(addCmd.getMessage());
	}

	private void processUpdateCommand(String input) {
		Update updateCmd = Update.getInstance();
		if (updateCmd.execute(parser.getUpdateParsedObject(input))) {
			updateModelData(updateCmd.getTaskType(), true);
		}
		updateMessage(updateCmd.getMessage());
	}

	private void processDeleteCommand(String input) {
		Delete deleteCmd = Delete.getInstance();
		if (deleteCmd.execute(parser.getDeleteParsedObject(input))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(deleteCmd.getMessage());
	}

	private void processChangeStatusCommand(String input, boolean newStatus) {
		ChangeStatus changeStatus = ChangeStatus.getInstance(newStatus);
		if (changeStatus.execute(parser.getChangeStatusParsedObject(input, newStatus))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(changeStatus.getMessage());
	}

	private void processUndoCommand(String input) {
		VersionControl vControl = VersionControl.getInstance();
		if (vControl.execute(parser.getUndoParsedObject(input))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(vControl.getMessage());
	}

	private void processRedoCommand(String input) {
		VersionControl vControl = VersionControl.getInstance();
		if (vControl.execute(parser.getRedoParsedObject(input))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(vControl.getMessage());
	}

	private void processDisplayCommand(String input) {
		Display displayCmd = Display.getInstance(TASK_TYPE.DEADLINE);
		List<List<Task>> temp = displayCmd.process(parser.getDisplayParsedObject(input));
		if (temp != null) {
			updateModelData(TASK_TYPE.DEADLINE, temp.get(0), false);
			updateModelData(TASK_TYPE.EVENT, temp.get(1), false);
		}
		updateMessage(displayCmd.getMessage());
	}

	private void updateModelData(TASK_TYPE type, boolean shouldSwitch) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_TABLE_CODE, new ObserverEvent.ETasks(storage.getAllTask(type), type, shouldSwitch)));
	}

	private void updateModelData(TASK_TYPE type, List<Task> tasks, boolean shouldSwitch) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_TABLE_CODE, new ObserverEvent.ETasks(tasks, type, shouldSwitch)));
	}

	private void updateMessage(String message) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_MESSAGE_CODE, new ObserverEvent.EMessage(message)));
	}

	@Override
	public void update(Observable observable, Object event) {
		ObserverEvent OEvent = (ObserverEvent) event;
		if (OEvent.getCode() == ObserverEvent.CHANGE_USER_INPUT_CODE) {
			ObserverEvent.EInput eInput = (ObserverEvent.EInput) OEvent.getPayload();
			processCommand(eInput.getCommand());
		}

	}
}
package main.logic;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import main.model.ObserverEvent;
import main.model.EnumTypes.TASK_TYPE;
import main.parser.Parser;
import main.storage.Storage;
import main.ui.MainGui;

public class Logic extends Observable implements Observer {
	private static Logic logic = null;
	private static Storage storage = null;
	private static final Parser parser = Parser.getInstance();
	private final Logger logger = Logger.getLogger(Logic.class.getName());

	private Logic() {}
	
	public static void start() {
		Logic logic = Logic.getInstance();
		logic.addObserver(MainGui.getInstance());
		
		Storage.start();
		storage = Storage.getInstance();
		
		logic.updateModelData(TASK_TYPE.DEADLINE, false);
		logic.updateModelData(TASK_TYPE.TODO, false);
		logic.updateModelData(TASK_TYPE.EVENT, true);
	}

	public static Logic getInstance() {
		if (logic == null) {
			logic = new Logic();
		}
		return logic;
	}

	public void processCommand(String input) {
		switch (parser.determineCommandType(input)) {
		case ADD:
			processAddCommand(input);
			break;
		case DISPLAY:
			// display(input);
			break;
		case SEARCH:
			// search(input);
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

	private void updateModelData(TASK_TYPE type, boolean shouldSwitch) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_TABLE_CODE, new ObserverEvent.ETasks(storage.getAllTask(type), type, shouldSwitch)));
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

	/*
	 * public ArrayList<List<Task>> getAllTaskLists() { ArrayList<List<Task>>
	 * taskLists = new ArrayList<List<Task>>();
	 * taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.EVENT));
	 * taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.TODO));
	 * taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.DEADLINE)); return
	 * taskLists; }
	 * 
	 * public ArrayList<Event> getAllEvents() { return
	 * (Storage.getAllTask(Commands.TASK_TYPE.EVENT).size() != 0) ?
	 * (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT) : new
	 * ArrayList(); }
	 * 
	 * public ArrayList<Todo> getAllTodos() { return
	 * (Storage.getAllTask(Commands.TASK_TYPE.TODO).size() != 0) ?
	 * (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.TODO) : new ArrayList();
	 * }
	 * 
	 * public ArrayList<Deadline> getAllDeadlineTasks() { return
	 * (Storage.getAllTask(Commands.TASK_TYPE.DEADLINE).size() != 0) ?
	 * (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE) : new
	 * ArrayList(); }
	 * 
	 * private ArrayList<Event> searchEvents(String input) { ParsedObject obj =
	 * parser.getSearchParsedObject(input); ArrayList<Event> matches = new
	 * ArrayList<Event>();
	 * 
	 * for (Event e : getAllEvents()) { for (String s :
	 * (ArrayList<String>)obj.getObjects()) { if
	 * (e.getTaskDesc().toLowerCase().contains(s)) { matches.add(e); } } }
	 * return matches; }
	 * 
	 * private ArrayList<Todo> searchTodos(String input) { ParsedObject obj =
	 * parser.getSearchParsedObject(input); ArrayList<Todo> matches = new
	 * ArrayList<Todo>();
	 * 
	 * for (Todo ft : getAllTodos()) { for (String s :
	 * (ArrayList<String>)obj.getObjects()) { if
	 * (ft.getTaskDesc().toLowerCase().contains(s)) { matches.add(ft); } } }
	 * return matches; }
	 * 
	 * private ArrayList<Deadline> searchDeadlineTasks(String input) {
	 * ParsedObject obj = parser.getSearchParsedObject(input);
	 * ArrayList<Deadline> matches = new ArrayList<Deadline>();
	 * 
	 * for (Deadline dt : getAllDeadlineTasks()) { for (String s :
	 * (ArrayList<String>)obj.getObjects()) { if
	 * (dt.getTaskDesc().toLowerCase().contains(s)) { matches.add(dt); } } }
	 * return matches; }
	 * 
	 * private String search(String input) { // For Debugging ParsedObject obj =
	 * parser.getSearchParsedObject(input);
	 * System.out.println(obj.getCommandType());
	 * System.out.println(obj.getTaskType()); ArrayList<String> v =
	 * obj.getObjects(); for (int i = 0; i < v.size(); i++) {
	 * System.out.print(v.get(i)); if (i < v.size()-1) { System.out.print("|");
	 * } } System.out.println(); return null; // return
	 * Storage.search(parser.getSearchParsedObject(input)); }
	 */

}
package main.logic;

import java.util.Observable;
import java.util.logging.Logger;

import main.parser.MainParser;

public class Logic extends Observable {
	private static Logic logic = null;
	private static final MainParser parser = MainParser.getInstance();
	private static final LogicObservable observable = LogicObservable.getInstance();
	private final Logger logger = Logger.getLogger(Logic.class.getName());

	private Logic() {}

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
				//display(input);
				break;
			case SEARCH:
				//search(input);
				break;
			case UPDATE:
				processUpdateCommand(input);
				break;
			case DELETE:
				processDeleteCommand(input);
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
				observable.updateStatusMsg("Invalid command entered. Please try again.");
		}
	}

	private void processAddCommand(String input) {
		Add addCmd = Add.getInstance();
		if (addCmd.execute(parser.getAddParsedObject(input))) {
			observable.updateTables(addCmd.getTaskType());
		}
		observable.updateStatusMsg(addCmd.getMessage());
	}

	private void processUpdateCommand(String input) {
		Update updateCmd = Update.getInstance();
		if (updateCmd.execute(parser.getUpdateParsedObject(input))) {
			observable.updateTables(updateCmd.getTaskType());
		}
		observable.updateStatusMsg(updateCmd.getMessage());
	}

	private void processDeleteCommand(String input) {
		Delete deleteCmd = Delete.getInstance();
		if (deleteCmd.execute(parser.getDeleteParsedObject(input))) {
			observable.updateTables(deleteCmd.getTaskType());
		}
		observable.updateStatusMsg(deleteCmd.getMessage());
	}

	private void processUndoCommand(String input) {
		UndoRedo undoCmd = UndoRedo.getInstance();
		if (undoCmd.execute(parser.getUndoParsedObject(input))) {
			observable.updateTables(undoCmd.getTaskType());
		}
		observable.updateStatusMsg(undoCmd.getMessage());
	}

	private void processRedoCommand(String input) {
		UndoRedo redoCmd = UndoRedo.getInstance();
		if (redoCmd.execute(parser.getRedoParsedObject(input))) {
			observable.updateTables(redoCmd.getTaskType());
		}
		observable.updateStatusMsg(redoCmd.getMessage());
	}

	/*public ArrayList<List<Task>> getAllTaskLists() {
		ArrayList<List<Task>> taskLists = new ArrayList<List<Task>>();
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.EVENT));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.TODO));
		taskLists.add(Storage.getAllTask(Commands.TASK_TYPE.DEADLINE));
		return taskLists;
	}

	public ArrayList<Event> getAllEvents() {
		return (Storage.getAllTask(Commands.TASK_TYPE.EVENT).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT) : new ArrayList();
	}

	public ArrayList<Todo> getAllTodos() {
		return (Storage.getAllTask(Commands.TASK_TYPE.TODO).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.TODO) : new ArrayList();
	}

	public ArrayList<Deadline> getAllDeadlineTasks() {
		return (Storage.getAllTask(Commands.TASK_TYPE.DEADLINE).size() != 0) ? (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.DEADLINE) : new ArrayList();
	}

	private ArrayList<Event> searchEvents(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<Event> matches = new ArrayList<Event>();

		for (Event e : getAllEvents()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (e.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(e);
				}
			}
		}
		return matches;
	}

	private ArrayList<Todo> searchTodos(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<Todo> matches = new ArrayList<Todo>();

		for (Todo ft : getAllTodos()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (ft.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(ft);
				}
			}
		}
		return matches;
	}

	private ArrayList<Deadline> searchDeadlineTasks(String input) {
		ParsedObject obj = parser.getSearchParsedObject(input);
		ArrayList<Deadline> matches = new ArrayList<Deadline>();

		for (Deadline dt : getAllDeadlineTasks()) {
			for (String s : (ArrayList<String>)obj.getObjects()) {
				if (dt.getTaskDesc().toLowerCase().contains(s)) {
					matches.add(dt);
				}
			}
		}
		return matches;
	}

	private String search(String input) {
		// For Debugging
		ParsedObject obj = parser.getSearchParsedObject(input);
		System.out.println(obj.getCommandType());
		System.out.println(obj.getTaskType());
		ArrayList<String> v = obj.getObjects();
		for (int i = 0; i < v.size(); i++) {
			System.out.print(v.get(i));
			if (i < v.size()-1) {
				System.out.print("|");
			}
		}
		System.out.println();
		return null;
		// return Storage.search(parser.getSearchParsedObject(input));
	}*/


}
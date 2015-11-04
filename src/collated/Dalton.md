# Dalton
###### main\logic\Add.java
``` java
 *
 */
public class Add extends Command {
	private static final Parser parser = Parser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final VersionControl vControl = VersionControl.getInstance();
	private static final Logger logger = Logger.getLogger(Add.class.getName());
	private static final boolean DEBUG = true;
	private static Add add = null;

	private Add() {}

	public static Add getInstance() {
		if (add == null) {
			add = new Add();
		}
		return add;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		if (DEBUG) {
			System.out.println(obj.getCommandType());
			System.out.println(obj.getTaskType());
		}
		ArrayList<Task> tasks = obj.getObjects();
		if (!tasks.isEmpty()) {
			switch (obj.getTaskType()) {
				case SINGLE_DATE_EVENT:
					Event sEvt = (Event)tasks.get(0);
					storage.addTask(sEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);

					addNewTask(sEvt);

					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "\"" + sEvt.getTaskDesc() + "\" has been successfully added as an Event on " + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + " at " + parser.formatDate(sEvt.getFromDate(), "h:mm a") + ".";

					if (DEBUG) {
						logger.log(Level.FINE, sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
						System.out.println(sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate());
					}
					return true;
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)tasks.get(0);
					storage.addTask(dEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);

					addNewTask(dEvt);

					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "\"" + dEvt.getTaskDesc() + "\" has been successfully added as an Event from " + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + " to " + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + ".";

					if (DEBUG) {
						logger.log(Level.FINE, dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
						System.out.println(dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate());
					}
					return true;
				case TODO:
					Todo flt = (Todo)tasks.get(0);
					storage.addTask(flt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);

					addNewTask(flt);

					taskType = EnumTypes.TASK_TYPE.TODO;
					message = "\"" + flt.getTaskDesc() + "\" has been successfully added as a Todo task.";

					if (DEBUG) {
						logger.log(Level.FINE, flt.getTaskID() + ", " + flt.getTaskDesc());
						System.out.println(flt.getTaskID() + ", " + flt.getTaskDesc());
					}
					return true;
				case DEADLINE:
					Deadline dt = (Deadline)tasks.get(0);
					storage.addTask(dt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);

					addNewTask(dt);

					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					Date d = dt.getDate();
					message = "\"" + dt.getTaskDesc() + "\" has been successfully added as a Deadline task that must be completed by " + parser.formatDate(d, "h:mm aa") + " on " + parser.formatDate(d, "EEE, d MMM yyyy") + ".";

					if (DEBUG) {
						logger.log(Level.FINE, dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
						System.out.println(dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate());
					}
					return true;
				default:
					taskType = EnumTypes.TASK_TYPE.INVALID;
					message = "Add command has failed.";
					return false;
			}
		}
		if (DEBUG) {
			System.out.println();
		}

		taskType = EnumTypes.TASK_TYPE.INVALID;
		message = "Add command has failed.";
		return false;
	}

```
###### main\logic\ChangeDirectory.java
``` java
 *
 */
public class ChangeDirectory extends JFrame {
	private static final Storage storage = Storage.getInstance();

	public ChangeDirectory(JFrame frame) {
		JFileChooser dirChooser = new JFileChooser();
		String title = "Select a directory";

		dirChooser.setCurrentDirectory(new java.io.File("."));
		dirChooser.setDialogTitle(title);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setAcceptAllFileFilterUsed(false);
		dirChooser.setVisible(true);

	    if (dirChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
	    	System.out.println("getCurrentDirectory(): " +  dirChooser.getCurrentDirectory() + storage.getStoreDir());
	    	storage.setStoreDir(dirChooser.getSelectedFile().getPath());
	    } else {
	    	System.out.println("No Selection");
	    }

	    try {
	    	this.dispose();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
}
```
###### main\logic\ChangeStatus.java
``` java
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
		return getInstance(true);
	}

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

```
###### main\logic\Command.java
``` java
 *
 */
public abstract class Command {
	protected EnumTypes.TASK_TYPE taskType;
	protected String message = "";

	public abstract boolean execute(ParsedObject obj);

	public String getMessage() {
		return this.message;
	}

	public EnumTypes.TASK_TYPE getTaskType() {
		return this.taskType;
	}
}
```
###### main\logic\Delete.java
``` java
 *
 */
public class Delete extends Command {
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(Delete.class.getName());
	private static final VersionControl vControl = VersionControl.getInstance();
	private static final boolean DEBUG = true;
	private static Delete delete = null;

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
		List<Task> deletedTasks = new ArrayList<>();
		int cnt = 0;
		for (int i = 0; i < taskIDs.size(); i++) {
			Task t = storage.getTaskByID(taskIDs.get(i));
			if (t != null) {
				cnt++;
				if (storage.delete(taskIDs.get(i))) {
					deletedTasks.add(t);
				}

				if (DEBUG) {
					System.out.print(taskIDs.get(i));
					System.out.print(" | ");
				}
			}
		}
		if (cnt > 0) {
			storage.saveAllTask();
			message = String.format("%d %s been deleted.", cnt, cnt > 1 ? "tasks have" : "task has");
			taskType = EnumTypes.TASK_TYPE.ALL;
			vControl.addNewData(new VersionModel.DeleteModel(deletedTasks));
			return true;
		}
		if (DEBUG) {
			System.out.println();
		}
		message = "Invalid Task ID. Please try again.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

```
###### main\logic\Logic.java
``` java
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
```
###### main\logic\Update.java
``` java
 *
 */
public class Update extends Command {
	private static final Parser parser = Parser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final VersionControl vControl = VersionControl.getInstance();
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final boolean DEBUG = true;
	private static Update update = null;

	private Update() {}

	public static Update getInstance() {
		if (update == null) {
			update = new Update();
		}
		return update;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		ArrayList<String> params = obj.getObjects();
		Task t = storage.getTaskByID(parser.parseInteger(params.get(0)));
		if (t != null) {
			message = "Task ID " + t.getTaskID() + ": ";
			switch (t.getType()) {
				case EVENT:
					taskType = EnumTypes.TASK_TYPE.EVENT;
					return updateEvent((Event) t, params);
				case TODO:
					taskType = EnumTypes.TASK_TYPE.TODO;
					return updateTodo((Todo) t, params);
				case DEADLINE:
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					return updateDeadline((Deadline) t, params);
			}
		}
		message = "Invalid column or value entered.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	private boolean updateEvent(Event evt, ArrayList<String> params) {
		Task oldEvt = evt.clone();
		switch (params.get(1)) {
			case "2":
				try {
					Date fromDate = parser.parseDateGroups(params.get(2)).get(0);
					message += "Previous Start Date \"" + parser.formatDate(evt.getFromDate(),  "EEE, d MMM yyyy") + "\" has been updated to \"" + parser.formatDate(fromDate,  "EEE, d MMM yyyy") + "\".";
					evt.setFromDate(fromDate);
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "3":
				try {
					Date toDate = parser.parseDateGroups(params.get(2)).get(0);
					message += "Previous End Date \"" + parser.formatDate(evt.getToDate(),  "EEE, d MMM yyyy") + "\" has been updated to \"" + parser.formatDate(toDate,  "EEE, d MMM yyyy") + "\".";
					evt.setToDate(toDate);
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "4":
				String taskDesc = params.get(2);
				message += "Previous Task Description \"" + evt.getTaskDesc() + "\" has been updated to \"" + taskDesc + "\".";
				evt.setTaskDesc(taskDesc);
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}
		storage.updateTask(evt);
		storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);

		addNewUpdateModel(oldEvt, evt);

		return true;
	}

	private boolean updateTodo(Todo t, ArrayList<String> params) {

		Task oldTodo = t.clone();

		switch (params.get(1)) {
			case "2":
				String taskDesc = params.get(2);
				t.setTaskDesc(taskDesc);
				message += "Task Description has been updated to " + taskDesc + ".";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}

		storage.updateTask(t);
		storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);

		addNewUpdateModel(oldTodo, t);

		return true;
	}

	private boolean updateDeadline(Deadline d, ArrayList<String> params) {

		Task oldDeadline = d.clone();

		switch (params.get(1)) {
			case "2":
				try {
					Date deadline = parser.parseDateGroups(params.get(2)).get(0);
					d.setDate(deadline);
					message += "Deadline has been updated to " + parser.formatDate(deadline,  "EEE, d MMM yyyy") + ".";
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "3":
				String taskDesc = params.get(2);
				d.setTaskDesc(taskDesc);
				message += "Task Description has been updated to " + taskDesc + ".";
				break;
			default:
				message += "Invalid column or value entered.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
		}

		storage.updateTask(d);
		storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);

		addNewUpdateModel(oldDeadline, d);

		return true;
	}

	private void addNewUpdateModel(Task oldTask, Task newTask) {
		vControl.addNewData(new VersionModel.UpdateModel(oldTask, newTask));
	}

	public static boolean undo(Task oldTask) {
		return storage.updateTask(oldTask);
	}

	public static boolean redo(Task newTask) {
		return storage.updateTask(newTask);
	}
}
```
###### main\model\EnumTypes.java
``` java
 *
 */
public class EnumTypes {
	public static enum COMMAND_TYPE {
		ADD, DELETE, UPDATE, SEARCH, DISPLAY, UNDO, REDO, EXIT, INVALID, DONE, UNDONE, DONE_UNDONE, DISPLAY_ON, DISPLAY_BETWEEN, DISPLAY_ON_BETWEEN, DISPLAY_ALL
	};

	public static enum TASK_TYPE {
		SINGLE_DATE_EVENT, DOUBLE_DATE_EVENT, EVENT, DEADLINE, TODO, ALL, INVALID
	};

	public static enum PARAM_TYPE {
		ID, CATEGORY, TIME
	}

	public static enum CATEGORY {
		COMPLETED, INCOMPLETED, EXPIRED, ALL
	}

}
```
###### main\model\ParsedObject.java
``` java
 *
 */
public class ParsedObject {
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	private ArrayList objects;
	private PARAM_TYPE paramType = PARAM_TYPE.ID;

	public ParsedObject() {
	}

	public ParsedObject(COMMAND_TYPE commandType, TASK_TYPE taskType, ArrayList objects) {
		this.commandType = commandType;
		this.taskType = taskType;
		this.objects = objects;
	}

	public ParsedObject(COMMAND_TYPE commandType, TASK_TYPE taskType, ArrayList objects, PARAM_TYPE paramType) {
		this.commandType = commandType;
		this.taskType = taskType;
		this.objects = objects;
		this.paramType = paramType;
	}


	/**
	 * @return the commandType
	 */
	public COMMAND_TYPE getCommandType() {
		return commandType;
	}

	/**
	 * @param commandType the commandType to set
	 */
	public void setCommandType(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	/**
	 * @return the taskType
	 */
	public TASK_TYPE getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}

	/**
	 * @return the tasks
	 */
	public ArrayList getObjects() {
		return objects;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(ArrayList objects) {
		this.objects = objects;
	}

	public PARAM_TYPE getParamType() {
		return this.paramType;
	}

	public void setParamType(PARAM_TYPE paramType) {
		this.paramType = paramType;
	}
}
```
###### main\model\tableModels\DeadlinesTableModel.java
``` java
 *
 */
public class DeadlinesTableModel extends AbstractTableModel {
	private static DeadlinesTableModel dtm = DeadlinesTableModel.getInstance();
	private final String[] columnNames = { "ID", "Deadline (2)", "Task Description (3)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, String.class, Boolean.class };
	private List<Task> deadlines = new ArrayList<>();
	private MainGUI mainGui;

	private DeadlinesTableModel() {
		super();
	}

	public void setTasks(List<Task> tasks) {
		this.deadlines = tasks;
	}

	public static DeadlinesTableModel getInstance() {
		if (dtm == null) {
			dtm = new DeadlinesTableModel();
		}
		return dtm;
	}

	public void setMainGui(MainGUI mainGui) {
		this.mainGui = mainGui;
	}

	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class<?> getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return deadlines.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
        	case 1:
        		return true;
        	case 2:
        		return true;
        	case 3:
        		return true;
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Deadline t = (Deadline)deadlines.get(row);
		Boolean shouldProcess = false;
		String fakeCommand = "update " + t.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Date)value;
				break;
			case 2:
				shouldProcess = true;
				fakeCommand = fakeCommand + (String)value;
				break;
			case 3:
				shouldProcess = true;
				fakeCommand = ((Boolean)value ?  "done" : "undone") + " " + t.getTaskID();
				break;
		}
		if (shouldProcess && mainGui != null) {
			mainGui.fakeInputComeIn(fakeCommand);
		}
    }

	public Object getValueAt(int row, int col) {
		Deadline t = (Deadline)deadlines.get(row);
		switch (col) {
			case 0:
				return t.getTaskID();
			case 1:
				return t.getDate();
			case 2:
				return t.getTaskDesc();
			case 3:
				return t.isDone();
		}

		return new String();
	}
}
```
###### main\model\tableModels\EventsTableModel.java
``` java
 *
 */
public class EventsTableModel extends AbstractTableModel {
	private static EventsTableModel etm = EventsTableModel.getInstance();
	private final String[] columnNames = { "ID", "Start Date (2)", "End Date (3)", "Task Description (4)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, Date.class, String.class, Boolean.class };
	private List<Task> events = new ArrayList<>();
	private MainGUI mainGui;

	private EventsTableModel() {
		super();
	}

	public void setTasks(List<Task> tasks) {
		this.events = tasks;
	}

	public static EventsTableModel getInstance() {
		if (etm == null) {
			etm = new EventsTableModel();
		}
		return etm;
	}

	public void setMainGui(MainGUI mainGui) {
		this.mainGui = mainGui;
	}

	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class<?> getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return events.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
	    	case 1:
	    		return true;
	    	case 2:
	    		return true;
        	case 3:
        		return true;
        	case 4:
        		return true;
        	default:
        		return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Event evt = (Event)events.get(row);
		Boolean shouldProcess = false;
		String fakeCommand = "update " + evt.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Date)value;
				break;
			case 2:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Date)value;
				break;
			case 3:
				shouldProcess = true;
				fakeCommand = fakeCommand + (String)value;
				break;
			case 4:
				shouldProcess = true;
				fakeCommand = ((Boolean)value ?  "done" : "undone") + " " + evt.getTaskID();
				break;
		}
		if (shouldProcess && mainGui != null) {
			mainGui.fakeInputComeIn(fakeCommand);
		}
    }

	public Object getValueAt(int row, int col) {
		Event evt = (Event)events.get(row);
		switch (col) {
			case 0:
				return evt.getTaskID();
			case 1:
				return evt.getFromDate();
			case 2:
				//return (evt.getFromDate() == evt.getToDate()) ? null : evt.getToDate();
				return evt.getToDate();
			case 3:
				return evt.getTaskDesc();
			case 4:
				return evt.isDone();
		}
		return new String();
	}
}
```
###### main\model\tableModels\TodosTableModel.java
``` java
 *
 */
public class TodosTableModel extends AbstractTableModel {
	private static TodosTableModel ttm = TodosTableModel.getInstance();
	private final String[] columnNames = { "ID", "Task Description (2)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, String.class, Boolean.class };
	private List<Task> todos = new ArrayList<>();
	private MainGUI mainGui;

	private TodosTableModel() {
		super();
	}

	public void setTasks(List<Task> tasks) {
		this.todos = tasks;
	}

	public static TodosTableModel getInstance() {
		if (ttm == null) {
			ttm = new TodosTableModel();
		}
		return ttm;
	}

	public void setMainGui(MainGUI mainGui) {
		this.mainGui = mainGui;
	}

	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return todos.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
        	case 1:
        		return true;
        	case 2:
        		return true;
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Todo t = (Todo)todos.get(row);
		Boolean shouldProcess = false;
		String fakeCommand = "update " + t.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				shouldProcess = true;
				fakeCommand = fakeCommand + (String)value;
				break;
			case 2:
				shouldProcess = true;
				fakeCommand = ((Boolean)value ?  "done" : "undone") + " " + t.getTaskID();
				break;
		}
		if (shouldProcess && mainGui != null) {
			mainGui.fakeInputComeIn(fakeCommand);
		}

    }

	public Object getValueAt(int row, int col) {
		Todo t = (Todo)todos.get(row);
		switch (col) {
			case 0:
				return t.getTaskID();
			case 1:
				return t.getTaskDesc();
			case 2:
				return t.isDone();
		}

		return new String();
	}
}
```
###### main\model\taskModels\Deadline.java
``` java
 *
 */
public class Deadline extends Task {
	private static final TASK_TYPE type = TASK_TYPE.DEADLINE;
	private Date date;

	public Deadline(Date date, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.date = date;
	}

	@Override
	public Deadline clone() {
		Deadline deadline = (Deadline)super.clone();
		deadline.setDate((Date)deadline.getDate().clone());
		return deadline;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}
}
```
###### main\model\taskModels\Event.java
``` java
 *
 */
public class Event extends Task {
	private static final TASK_TYPE type = TASK_TYPE.EVENT;
	private Date fromDate;
	private Date toDate;

	public Event(Date fromDate, Date toDate, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	@Override
	public Event clone() {
		Event event = (Event)super.clone();
		event.setFromDate((Date)event.getFromDate().clone());
		event.setToDate((Date)event.getToDate().clone());
		return event;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}
}
```
###### main\model\taskModels\Todo.java
``` java
 *
 */
public class Todo extends Task {
	private static final TASK_TYPE type = TASK_TYPE.TODO;

	public Todo(String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}

}
```
###### main\parser\Parser.java
``` java
 *
 */
public class Parser {
	private static Parser parser = null;
	private final PrettyTimeParser ptParser = new PrettyTimeParser();
	private final Logger logger = Logger.getLogger(Parser.class.getName());
	private String[] updateCmdList = {"update", "/u", "edit", "/e", "modify", "/m"};
	private String[] deleteCmdList = {"delete", "del", "/d", "remove", "rm", "/r"};
	private String[] doneCmdList = {"done", "complete"};
	private String[] undoneCmdList = {"!done", "undone", "incomplete"};
	private String[] undoCmdList = {"undo", "/un"};
	private String[] redoCmdList = {"redo", "/re"};
	private String[] exitCmdList = {"exit", "/e", "quit", "/q"};
	private String[] displayCmdList = {"display", "show", "/sh", "view", "/v"};

	private final String UPDATE_REGEX = "^\\d+\\s+\\d+\\s+(\\w*|\\d*|\\s*)+";
	private final String DELETE_REGEX = "^\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)";
	private final String DISPLAY_REGEX = "^(\\w|\\d|\\s)+";
	private final String DONE_UNDONE_REGEX= "^\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)";
	private final String UNDO_REDO_REGEX = "^\\d+\\s*$";

	private Parser() {}

	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
		}
		return parser;
	}

	public COMMAND_TYPE determineCommandType(String input) {
		if (input.trim().isEmpty()) {
			return COMMAND_TYPE.INVALID;
		}

		if (isCommand(input, updateCmdList)) {
			if (hasValidParameters(removeCommandWord(input, updateCmdList), UPDATE_REGEX)) {
				return COMMAND_TYPE.UPDATE;
			}
		} else if (isCommand(input, deleteCmdList)) {
			if (hasValidParameters(removeCommandWord(input, deleteCmdList), DELETE_REGEX)) {
				return COMMAND_TYPE.DELETE;
			}
		} else if (isCommand(input, displayCmdList)) {
			if (hasValidParameters(removeCommandWord(input, displayCmdList), DISPLAY_REGEX)) {
				return COMMAND_TYPE.DISPLAY;
			}
		} else if (isCommand(input, doneCmdList)) {
			if (hasValidParameters(removeCommandWord(input, doneCmdList), DONE_UNDONE_REGEX)) {
				return COMMAND_TYPE.DONE;
			}
		} else if (isCommand(input, undoneCmdList)) {
			if (hasValidParameters(removeCommandWord(input, undoneCmdList), DONE_UNDONE_REGEX)) {
				return COMMAND_TYPE.UNDONE;
			}
		} else if (isCommand(input, undoCmdList)) {
			input = removeCommandWord(input, undoCmdList);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.isEmpty()) {
				return COMMAND_TYPE.UNDO;
			}
		} else if (isCommand(input, redoCmdList)) {
			input = removeCommandWord(input, redoCmdList);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.isEmpty()) {
				return COMMAND_TYPE.REDO;
			}
		} else if (isCommand(input, exitCmdList)) {
			if (removeCommandWord(input, exitCmdList).isEmpty()) {
				return COMMAND_TYPE.EXIT;
			}
		} else {
			return COMMAND_TYPE.ADD;
		}

		return COMMAND_TYPE.INVALID;
	}

	private boolean isCommand(String input, String[] commandList) {
		for(int i = 0; i < commandList.length; i++) {
			Pattern pattern = Pattern.compile("(?ui)^" + commandList[i] + "\\s*");
			Matcher matcher = pattern.matcher(input);
	        if (matcher.find()) {
        		return true;
	        }
		}
		return false;
	}

	private boolean hasValidParameters(String input, String regex) {
		Pattern pattern = Pattern.compile("(?ui)" + regex);
		Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
        	return true;
        }
		return false;
	}

	private String removeCommandWord(String input, String[] commandList) {
		String regexStr;
		for(int i = 0; i < commandList.length; i++) {
			regexStr = "(?ui)^" + commandList[i] + "\\s*";
			Pattern pattern = Pattern.compile("(?ui)^" + commandList[i] + "\\s*");
			Matcher matcher = pattern.matcher(input);
			if (matcher.find()) {
				return input.replaceAll(regexStr, "");
			}
		}
		return null;
	}

	public String formatDate(Date d, String format) {
		return new SimpleDateFormat(format).format(d);
	}

	public List<Date> parseDateGroups(String input) {
		String timeRegexPattern = "(((\\d+\\s+(minutes|min|seconds|sec|hours))|[0-9](am|pm|a.m.|p.m.)|1[0-2](am|pm|a.m.|p.m.))|(0[0-9]|1[0-9]|2[0-3])\\:?([0-5][0-9]))\\:?([0-5][0-9])?(am|pm|a.m.|p.m.|h|\\shours)?";
		Pattern timePattern = Pattern.compile("(?ui)" + timeRegexPattern);
		input = input.replaceAll("until", "till");
		List<DateGroup> dGroups = getDateGroups(input);
		List<Date> dates = new ArrayList<Date>();
		Calendar temp1 = Calendar.getInstance();
		Calendar temp2 = Calendar.getInstance();
		Calendar temp3 = Calendar.getInstance();
		DateGroup firstGroup;
		DateGroup secondGroup;
		DateGroup thirdGroup;
		List<Date> firstGroupDates;
		List<Date> secondGroupDates;
		List<Date> thirdGroupDates;
		Matcher matcher1;
		Matcher matcher2;
		Matcher matcher3;

		if (dGroups != null) {
			switch (dGroups.size()) {
				case 1:
					// lunch with john at 1pm tomorrow
					// lunch in 5 minutes with john
					firstGroup = dGroups.get(0);
					firstGroupDates = firstGroup.getDates();
					matcher1 = timePattern.matcher(firstGroup.getText());
					if (matcher1.find()) {
						return firstGroupDates;
					} else {
						if (firstGroupDates.size() == 1) {
							temp1.setTime(firstGroupDates.get(0));
							setDateTime(temp1, -1, -1, -1, 0, 0, 0);
							dates.add(temp1.getTime());
						} else {
							for (int i = 0; i < firstGroupDates.size(); i++) {
								dates.add(firstGroupDates.get(i));
							}
						}
					}
					break;
				case 2:
					// at 1pm lunch with john tomorrow
					// tomorrow lunch with john at 1pm
					// lunch from 1pm with john to 2pm tomorrow
					// lunch from 1pm tomorrow with john to 2pm
					// lunch from 1pm tomorrow with john to 2pm tomorrow
					// sleep from 1pm to 2pm tomorrow
					firstGroup = dGroups.get(0);
					secondGroup = dGroups.get(1);
					firstGroupDates = firstGroup.getDates();
					secondGroupDates = secondGroup.getDates();

					matcher1 = timePattern.matcher(firstGroup.getText());
					matcher2 = timePattern.matcher(secondGroup.getText());

					if (firstGroupDates.size() == 1 && secondGroupDates.size() == 1) {
						temp1.setTime(firstGroupDates.get(0));
						temp2.setTime(secondGroupDates.get(0));
						if (matcher1.find() && !matcher2.find()) {
							setDateTime(temp1, temp2.get(Calendar.YEAR), temp2.get(Calendar.MONTH), temp2.get(Calendar.DATE), -1, -1, -1);
							dates.add(temp1.getTime());
						} else if (!matcher1.find() && matcher2.find()) {
							setDateTime(temp2, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);
							dates.add(temp2.getTime());
						} else {
							if (temp1.compareTo(temp2) < 0) {
								dates.add(temp1.getTime());
								dates.add(temp2.getTime());
							} else if (temp1.compareTo(temp2) > 0){
								if (temp1.get(Calendar.HOUR_OF_DAY) < temp2.get(Calendar.HOUR_OF_DAY)) {
									setDateTime(temp2, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);
								}
								dates.add(temp1.getTime());
								dates.add(temp2.getTime());
							} else {
								dates.add(temp1.getTime());
							}
						}
					} else {
						// lunch from 1 to 2pm with john tomorrow
						// from 1 to 2pm lunch tomorrow with john
						if (firstGroupDates.size() == 2 && secondGroupDates.size() == 1) {
							temp1.setTime(firstGroupDates.get(0));
							temp2.setTime(firstGroupDates.get(1));
							temp3.setTime(secondGroupDates.get(0));
						} else if (firstGroupDates.size() == 1 && secondGroupDates.size() == 2) {
							// lunch tomorrow with john from 1 to 2pm
							matcher1 = timePattern.matcher(secondGroup.getText());
							matcher2 = timePattern.matcher(firstGroup.getText());
							temp1.setTime(secondGroupDates.get(0));
							temp2.setTime(secondGroupDates.get(1));
							temp3.setTime(firstGroupDates.get(0));
						}

						if (matcher1.find() && !matcher2.find()) {
							setDateTime(temp1, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);
							setDateTime(temp2, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);

							if (temp1.compareTo(temp2) < 0) {
								dates.add(temp1.getTime());
								dates.add(temp2.getTime());
							} else if (temp1.compareTo(temp2) > 0){
								dates.add(temp2.getTime());
								dates.add(temp1.getTime());
							} else {
								dates.add(temp1.getTime());
							}
						}
					}
					break;
				case 3:
					// next saturday lunch from 1pm with john to 2pm
					// 1pm lunch to 2pm with john next saturday
					// 11-11 sleep from 1pm at home to 2pm
					firstGroup = dGroups.get(0);
					secondGroup = dGroups.get(1);
					thirdGroup = dGroups.get(2);
					firstGroupDates = firstGroup.getDates();
					secondGroupDates = secondGroup.getDates();
					thirdGroupDates = thirdGroup.getDates();

					matcher1 = timePattern.matcher(firstGroup.getText());
					matcher2 = timePattern.matcher(secondGroup.getText());
					matcher3 = timePattern.matcher(thirdGroup.getText());
					temp1.setTime(firstGroupDates.get(0));
					temp2.setTime(secondGroupDates.get(0));
					temp3.setTime(thirdGroupDates.get(0));

					if (matcher1.find() && matcher2.find() && !matcher3.find()) {
						setDateTime(temp1, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);
						setDateTime(temp2, temp3.get(Calendar.YEAR), temp3.get(Calendar.MONTH), temp3.get(Calendar.DATE), -1, -1, -1);

						if (temp1.compareTo(temp2) < 0) {
							dates.add(temp1.getTime());
							dates.add(temp2.getTime());
						} else if (temp1.compareTo(temp2) > 0){
							dates.add(temp2.getTime());
							dates.add(temp1.getTime());
						} else {
							dates.add(temp1.getTime());
						}
					} else if (matcher1.find() && !matcher2.find() && matcher3.find()) {
						setDateTime(temp1, temp2.get(Calendar.YEAR), temp2.get(Calendar.MONTH), temp2.get(Calendar.DATE), -1, -1, -1);
						setDateTime(temp3, temp2.get(Calendar.YEAR), temp2.get(Calendar.MONTH), temp2.get(Calendar.DATE), -1, -1, -1);

						if (temp1.compareTo(temp3) < 0) {
							dates.add(temp1.getTime());
							dates.add(temp3.getTime());
						} else if (temp1.compareTo(temp3) > 0){
							dates.add(temp3.getTime());
							dates.add(temp1.getTime());
						} else {
							dates.add(temp1.getTime());
						}
					} else {
						setDateTime(temp2, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);
						setDateTime(temp3, temp1.get(Calendar.YEAR), temp1.get(Calendar.MONTH), temp1.get(Calendar.DATE), -1, -1, -1);

						if (temp2.compareTo(temp3) < 0) {
							dates.add(temp2.getTime());
							dates.add(temp3.getTime());
						} else if (temp2.compareTo(temp3) > 0){
							dates.add(temp3.getTime());
							dates.add(temp2.getTime());
						} else {
							dates.add(temp2.getTime());
						}
					}
					break;
				default:
					return null;
			}
			return dates;
		} else {
			return null;
		}
	}

	public List<DateGroup> getDateGroups(String input) {
		List<DateGroup> dGroup = ptParser.parseSyntax(input);
		if (!dGroup.isEmpty()) {
			return dGroup;
		} else {
			return null;
		}
	}

	private String getTaskDesc(String input) {
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(input);
		String date = "";
		if (matcher.find()) {
			input = matcher.group().replace("\"", "");
		} else {
			List<DateGroup> dateGroup = ptParser.parseSyntax(input);
			for (int i = 0; i < dateGroup.size(); i++) {
				date = dateGroup.get(i).getText();
				input = input.replaceAll("(?ui)\\s*((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s*" + date, "");
				input = input.replaceAll("(?ui)\\s*" + date + "\\s*", " ");
			}
			//Pattern splitPattern = Pattern.compile("\\s+((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s+");
			//String[] excessWords = splitPattern.split(date);
			Pattern splitPattern = Pattern.compile("\\s");
			String[] excessWords = splitPattern.split(date);
			for (String word : excessWords) {
				input = input.replaceAll(word, "");
			}
			input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)*\\s*$", "");
			input = input.replaceAll("^(?ui)\\s*((due on)|(due by)|due|by|before|until|till|to|from|on|at)\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)", "");
		}
		input = input.replaceAll("^\\s+|\\s+$", "");
		return input;
	}

	public ArrayList<String> getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		String[] paramArray;

		switch (cmdType) {
		case DELETE:
			if (input.contains("to") || input.contains("-")) {
				pattern = "\\-+|to";
			} else {
				pattern = "\\s+|\\.+|,+|:+|;+|/+|\\\\+|\\|+";
			}
			paramArray = input.split(pattern);
			break;
		case UPDATE:
			paramArray = input.split("\\s+", 3);
			break;
		default:
			pattern = "\\s+";
			paramArray = input.split(pattern);
		}

		ArrayList<String> paramList = new ArrayList<String>(Arrays.asList(paramArray));

		for (int i = 0; i < paramList.size(); i++) {
			paramList.set(i, paramList.get(i).replaceAll("^\\s+|\\s+$", ""));
		}

		return paramList;
	}

	public ParsedObject getDisplayParsedObject(String input) {
		ParsedObject obj;
		input = removeCommandWord(input, displayCmdList);
		List<Date> parsedInput = parseDateGroups(input);
		if (parsedInput == null) {
			if (input.matches("(?ui)^\\s*all\\s*$")) {
				obj = new ParsedObject(COMMAND_TYPE.DISPLAY_ALL, null, null);
			} else {
				obj = new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		} else {
			ArrayList<Date> dates = new ArrayList<Date>(parsedInput);
			switch (dates.size()) {
				case 1:
					obj = new ParsedObject(COMMAND_TYPE.DISPLAY_ON, null, dates);
					break;
				case 2:
					obj = new ParsedObject(COMMAND_TYPE.DISPLAY_BETWEEN, null, dates);
					break;
				default:
					obj = new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		}

		return obj;
	}

	public ParsedObject getAddParsedObject(String input) {
		List<Date> parsedInput = parseDateGroups(input);
		ArrayList<Task> tasks = new ArrayList<Task>();
		ParsedObject obj;
		if (parsedInput != null) {
			switch (parsedInput.size()) {
				case 1:
					if (input.contains("by") || input.contains("due") || input.contains("before")) {
						tasks.add(new Deadline(parsedInput.get(0), getTaskDesc(input), false));
						obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DEADLINE, tasks);
					} else {
						tasks.add(new Event(parsedInput.get(0), parsedInput.get(0), getTaskDesc(input), false));
						obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.SINGLE_DATE_EVENT, tasks);
					}
					break;
				case 2:
					tasks.add(new Event(parsedInput.get(0), parsedInput.get(1), getTaskDesc(input), false));
					obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.DOUBLE_DATE_EVENT, tasks);
					break;
				default:
					obj = new ParsedObject(COMMAND_TYPE.INVALID, null, null);
			}
		} else {
			tasks.add(new Todo(input.trim(), false));
			obj = new ParsedObject(COMMAND_TYPE.ADD, TASK_TYPE.TODO, tasks);
		}
		return obj;
	}

	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, updateCmdList);
		ArrayList<String> paramsList = getCommandParameters(params, COMMAND_TYPE.UPDATE);
		ParsedObject obj = new ParsedObject(COMMAND_TYPE.UPDATE, null, paramsList);
		return obj;
	}

	public int parseInteger(String intString) {
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			//throw new NumberFormatException();
		}
		return 0;
	}

	public ParsedObject getDeleteParsedObject(String input) {
		String params = removeCommandWord(input, deleteCmdList);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);
		ParsedObject obj;

		if (input.contains("to") || input.contains("-")) {
			int fromID;
			int toID;
			try {
				fromID = parseInteger(taskIDList.get(0));
				toID = parseInteger(taskIDList.get(1));
			} catch (Exception e) {
				fromID = 0;
				toID = 0;
			}

			for (int i = fromID; i <= toID; i++) {
				taskIDs.add(i);
			}
		} else if (input.matches("(?ui)^\\s*all\\s*$")) {
			taskIDs.add(-1);
		} else {
			for (int i = 0; i < taskIDList.size(); i++) {
				try {
					taskIDs.add(parseInteger(taskIDList.get(i)));
				} catch (Exception e) {
					// Not number exception
				}

			}
		}
		obj = new ParsedObject(COMMAND_TYPE.DELETE, null, taskIDs);
		return obj;
	}

	public ParsedObject getChangeStatusParsedObject(String input, boolean newStatus) {
		String params;
		if (newStatus) {
			params = removeCommandWord(input, doneCmdList);
		} else {
			params = removeCommandWord(input, undoneCmdList);
		}
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<String> taskIDList = getCommandParameters(params, COMMAND_TYPE.DELETE);
		ParsedObject obj;

		if (input.contains("to") || input.contains("-")) {
			int fromID;
			int toID;
			try {
				fromID = parseInteger(taskIDList.get(0));
				toID = parseInteger(taskIDList.get(1));
			} catch (Exception e) {
				fromID = 0;
				toID = 0;
			}

			for (int i = fromID; i <= toID; i++) {
				taskIDs.add(i);
			}
		} else {
			for (int i = 0; i < taskIDList.size(); i++) {
				try {
					taskIDs.add(parseInteger(taskIDList.get(i)));
				} catch (Exception e) {
					// Not number exception
				}

			}
		}
		obj = new ParsedObject(newStatus ? COMMAND_TYPE.DONE : COMMAND_TYPE.UNDONE, null, taskIDs);
		return obj;
	}


	public ParsedObject getUndoParsedObject(String input) {
		String params = removeCommandWord(input, undoCmdList);
		ArrayList<Integer> numOfExec = new ArrayList<Integer>();
		if (params.isEmpty()) {
			numOfExec.add(1);
		} else {
			numOfExec.add(parseInteger(params));
		}

		return new ParsedObject(COMMAND_TYPE.UNDO, null, numOfExec);
	}

	public ParsedObject getRedoParsedObject(String input) {
		String params = removeCommandWord(input, redoCmdList);
		ArrayList<Integer> numOfExec = new ArrayList<Integer>();
		if (params.isEmpty()) {
			numOfExec.add(1);
		} else {
			numOfExec.add(parseInteger(params));
		}

		return new ParsedObject(COMMAND_TYPE.REDO, null, numOfExec);
	}

	public Calendar setDateTime(Calendar cal, int year, int month, int date, int hours, int minutes, int seconds) {
		if (year != -1) {
			cal.set(Calendar.YEAR, year);
		}
		if (month != -1) {
			cal.set(Calendar.MONTH, month);
		}
		if (date != -1) {
			cal.set(Calendar.DATE, date);
		}
		if (hours != -1) {
			cal.set(Calendar.HOUR_OF_DAY, hours);
		}
		if (minutes != -1) {
			cal.set(Calendar.MINUTE, minutes);
		}
		if (seconds != -1) {
			cal.set(Calendar.SECOND, seconds);
		}
		return cal;
	}
}
```
###### main\parser\StringFinder.java
``` java
 *
 */
class StringFinder {
    private final String phrase;
    private final Map<String, Boolean> cache = new HashMap<String, Boolean>();

    public StringFinder(String phrase) {
    	this.phrase = phrase;
    }

    public StringFinder containsAll(String... strings) {
        for (String string : strings) {
            if (contains(string) == false)
            	return new FailedStringFinder(phrase);
        }
        return this;
    }

    public StringFinder andOneOf(String... strings) {
        for (String string: strings) {
            if (contains(string))
            	return this;
        }
        return new FailedStringFinder(phrase);
    }

    public StringFinder andNot(String... strings) {
        for (String string : strings) {
            if (contains(string))
            	return new FailedStringFinder(phrase);
        }
        return this;
    }

    public boolean matches() { return true; }

    private boolean contains(String s) {
        Boolean cached = cache.get(s);
        if (cached == null) {
            cached = phrase.toLowerCase().contains(s.toLowerCase());
            cache.put(s, cached);
        }
        return cached;
    }


}

class FailedStringFinder extends StringFinder {

    public FailedStringFinder(String phrase) {
        super(phrase);
    }

    public boolean matches() { return false; }

    // The below are actually optional, but save on performance:
    public StringFinder containsAll(String... strings) { return this; }
    public StringFinder andOneOf(String... strings) { return this; }
    public StringFinder andNot(String... strings) { return this; }
}
```
###### main\ui\CustomCellRenderer.java
``` java
 *
 */
public class CustomCellRenderer extends JTextArea implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setMargin(new Insets(2,10,2,10));

        if (System.getProperty("os.name").startsWith("Mac")) {
        	this.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
        } else {
        	this.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
        	setBackground(table.getBackground());
            setForeground(table.getForeground());
        }

        if (value != null) {
	        if (value instanceof Date) {
	        	this.setText(formatDate((Date)value));
        		long currentTime = System.currentTimeMillis();
        		long dateTime = ((Date)value).getTime();
        		if (dateTime < currentTime) {
	        		this.setForeground(Color.RED);
	        	} else {
	        		this.setForeground(Color.decode("0x009900"));
	        	}

        		if (table.getName().equals("Events")) {
        			int modelRow = table.getRowSorter().convertRowIndexToModel(row);
	        		if (column == 2) {
	        			String fromDate = table.getModel().getValueAt(modelRow, 1).toString();
		        		String toDate = table.getModel().getValueAt(modelRow, 2).toString();
		        		if (fromDate.equals(toDate)) {
		        			this.setText(null);
		        		} else {
		        			this.setText(formatDate((Date)value));
		        		}
        			} else {
        				this.setText(formatDate((Date)value));
        			}
        		}
	        } else {
	        	this.setText(value.toString());
	        }
        }

        return this;
    }

    private String formatDate(Date d) {
    	SimpleDateFormat dateFmt = new SimpleDateFormat("EEE, dd MMM yyyy");
		SimpleDateFormat dayFmt = new SimpleDateFormat("h:mm a");
		return dateFmt.format(d) + "\n" + dayFmt.format(d);
    }
}
```
###### main\ui\CustomDateCellEditor.java
``` java
 *
 */
public class CustomDateCellEditor extends DefaultCellEditor {
	static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	static PrettyTimeParser parser = new PrettyTimeParser();

	public CustomDateCellEditor() {
		super(new JTextField());
	}

	@Override
	public Object getCellEditorValue() {
		String value = ((JTextField)getComponent()).getText();
		String date = parser.parse(value).get(0).toString();

		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		JTextField tf = ((JTextField)getComponent());
		tf.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		tf.setBorder(new LineBorder(Color.BLACK));

		try {
			tf.setText(sdf.format(value));
		} catch (Exception e) {
			tf.setText("");
		}

		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	tf.selectAll();
            	tf.setBackground(Color.YELLOW);
            }
        });

		return tf;
	}

	@Override
	public boolean stopCellEditing() {
		String value = ((JTextField)getComponent()).getText();
		List<Date> dates = parser.parse(value);
		if (dates.size() <= 0) {
			((JComponent)getComponent()).setBorder(new LineBorder(Color.RED));
			return false;
		}

		return super.stopCellEditing();
	}
}
```
###### main\ui\InputHistory.java
``` java
 *
 */
public class InputHistory {
	private static InputHistory inputHistory = null;
	private static int pointer;
	private static ArrayList<String> history;

	private InputHistory() {}

	public static InputHistory getInstance() {
		if (inputHistory == null) {
			inputHistory = new InputHistory();
			history = new ArrayList<String>();
			pointer = 0;
		}
		return inputHistory;
	}

	public void addInputHistory(String input) {
		if (!input.trim().isEmpty()) {
			history.add(input);
			pointer = history.size();
		}
	}

	public String getPreviousInput() {
		if (history.size() > 0 && pointer > 0) {
			pointer -= 1;
			return history.get(pointer);
		}
		return null;
	}

	public String getNextInput() {
		if (history.size() > 0 && pointer < history.size()-1) {
			pointer += 1;
			return history.get(pointer);
		} else {
			return null;
		}

	}
}
```
###### main\ui\MainGUI.java
``` java
 *
 */
public class MainGUI extends Observable implements Observer {

	private static MainGUI mainGUI;

	private JFrame frmTodokoro;
	private JPanel inputPanel;
	private JTextField tfFilter;
	private JTextPane tpUserInput;
	private JLabel lblFilter;
	private JTable eventsTable, todosTable, deadlinesTable;
	private JTabbedPane tabbedPane;
	private JScrollPane eventsScrollPane, todosScrollPane, deadlineTasksScrollPane;
	private TableRowSorter eventsSorter, todosSorter, deadlinesSorter;
	private JTextArea taStatusMessage;

	private static final Logger logger = Logger.getLogger(MainGUI.class.getName());
	private static final InputHistory history = InputHistory.getInstance();
	private static EventsTableModel etm = EventsTableModel.getInstance();
	private static TodosTableModel ttm = TodosTableModel.getInstance();
	private static DeadlinesTableModel dtm = DeadlinesTableModel.getInstance();

	private static final int FRAME_WIDTH = 768;
	//private static final int FRAME_WIDTH = 1024;
	private static final int FRAME_HEIGHT = 640;
	private static final float FRAME_OPACITY = 1f;

	private static final int INPUT_PANEL_HEIGHT = 137;
	private static final int INPUT_PANEL_WIDTH = 762;

	private static final int FRAME_SIMPLE_MODE_WIDTH = 768;
	private static final int FRAME_SIMPLE_MODE_HEIGHT = 167;
	private static final float FRAME_SIMPLE_MODE_OPACITY = 0.9f;

	private static final int FRAME_HELP_LIST_WIDTH = 1024;
	private static final int FRAME_HELP_LIST_HEIGHT = 640;

	private static final int TABLE_FONT_SIZE = 14;
	private static final int LABEL_FONT_SIZE = 15;
	private static final int TABLE_ROW_HEIGHT = 50;

	private static Color normalTextColour = Color.BLACK;
	private static Color highlightedTextColour = Color.RED;

	private static String[] themes = {"bernstein.BernsteinLookAndFeel", "noire.NoireLookAndFeel", "smart.SmartLookAndFeel", "mint.MintLookAndFeel", "mcwin.McWinLookAndFeel"};
	private static int themeIndex = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "LookAndFeel: " + e.toString(), e);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					getInstance().addObserver(Logic.getInstance());
					getInstance().frmTodokoro.setVisible(true);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "EventQueue Invoke: " + e.toString(), e);
				}
			}
		});
	}

	public static MainGUI getInstance() {
		if (mainGUI == null) {
			mainGUI = new MainGUI();
		}
		return mainGUI;
	}

	/**
	 * Create the application.
	 */
	private MainGUI() {
		try {
			initialise();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "MainGui Constructor: " + e.toString(), e);
		}

		/*
		 * msgObserver.setOwner(this); tablesObserver.setOwner(this);
		 */
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialise() throws Exception {
		setupTableModels();
		setupMainFrame();
		setupPanels();
		setupTextFields();
		setupLabels();
		setupTabbedPane();
		setupTables();
		setupTableSorters();
	}

	private void setupTableModels() {
		dtm.setMainGui(this);
		etm.setMainGui(this);
		ttm.setMainGui(this);
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setAlwaysOnTop(true);
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(0, 23, FRAME_WIDTH, FRAME_HEIGHT);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);

		frmTodokoro.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				tpUserInput.requestFocusInWindow();
			}
		});

		InputMap im = frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = frmTodokoro.getRootPane().getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "Simple Mode");
		am.put("Simple Mode", new AbstractAction() {
			boolean isNormalMode = false;

			public void actionPerformed(ActionEvent e) {
				if (isNormalMode) {
					tabbedPane.setVisible(true);
					lblFilter.setVisible(true);
					tfFilter.setVisible(true);
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
					frmTodokoro.setOpacity(FRAME_OPACITY);
					inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
				} else {
					tabbedPane.setVisible(false);
					lblFilter.setVisible(false);
					tfFilter.setVisible(false);
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_SIMPLE_MODE_WIDTH, FRAME_SIMPLE_MODE_HEIGHT);
					frmTodokoro.setOpacity(FRAME_SIMPLE_MODE_OPACITY);
					inputPanel.setBounds(0, 0, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
				}

				isNormalMode = !isNormalMode;
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "Change Directory");
		am.put("Change Directory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ChangeDirectory cd = new ChangeDirectory(frmTodokoro);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, InputEvent.CTRL_DOWN_MASK), "Cycle Themes");
		am.put("Cycle Themes", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (themeIndex == themes.length-1) {
					themeIndex = 0;
				} else {
					themeIndex++;
				}
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
					switch (themeIndex) {
						case 1:
							normalTextColour = Color.WHITE;
							highlightedTextColour = Color.ORANGE;
							break;
						default:
							normalTextColour = Color.BLACK;
							highlightedTextColour = Color.RED;
					}
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SwingUtilities.updateComponentTreeUI(frmTodokoro);
				eventsTable.setRowHeight(TABLE_ROW_HEIGHT);
				todosTable.setRowHeight(TABLE_ROW_HEIGHT);
				deadlinesTable.setRowHeight(TABLE_ROW_HEIGHT);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK), "Cycle Tabs");
		am.put("Cycle Tabs", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex() == 2) {
					tabbedPane.setSelectedIndex(0);
				} else {
					tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex()+1);
				}
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK), "Scroll Down Tables");
		am.put("Scroll Down Tables", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				case 2:
					deadlineTasksScrollPane.getVerticalScrollBar().setValue(deadlineTasksScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				}
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK), "Scroll Up Tables");
		am.put("Scroll Up Tables", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				case 2:
					deadlineTasksScrollPane.getVerticalScrollBar().setValue(deadlineTasksScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				}
			}
		});

		/*
		GetHelpList demo = new GetHelpList();
		demo.setBounds(768, 0, 240, 600);
		demo.setVisible(true);
		frmTodokoro.getContentPane().add(demo);

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "Help List");
		frmTodokoro.getRootPane().getActionMap().put("Help List", new AbstractAction() {
			boolean isSimpleMode = false;
			public void actionPerformed(ActionEvent e) {
				if (isSimpleMode) {
					tfUserInput.requestFocusInWindow();
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
				} else {
					demo.requestListFocus();
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_HELP_LIST_WIDTH, FRAME_HELP_LIST_HEIGHT);
				}

				isSimpleMode = !isSimpleMode;
				//GetHelpList.createAndShowGUI(frmTodokoro.getWidth(), 0);
			}
		});*/
	}

	private void highlightText() {
		String[] keywords = {"update", "delete", "display", "undo", "redo", "exit", "!done", "done"};
		String[] days = {"mon", "tue", "wed", "thurs", "fri", "sat", "sun", "monday", "tueday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        String input = tpUserInput.getText();

        SimpleAttributeSet defaultSet = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultSet, normalTextColour);
        tpUserInput.getStyledDocument().setCharacterAttributes(0, input.length(), defaultSet, true);
        SimpleAttributeSet customSet = new SimpleAttributeSet();
        StyleConstants.setForeground(customSet, highlightedTextColour);
        if (!tpUserInput.getText().isEmpty()) {
        	taStatusMessage.setText(null);
        }

        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("(?ui)^" + keyword);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {

            	/*
                System.out.print("Start index: " + matcher.start());
                System.out.print(" End index: " + matcher.end());
                System.out.println(" Found: " + matcher.group());
                */

                tpUserInput.getStyledDocument().setCharacterAttributes(matcher.start(), keyword.length(), customSet, true);
            }
        }

        for (String day : days) {
            Pattern pattern = Pattern.compile("(?ui)" + day + "\\s+(\\d+|(?:Jan(?:uary)?|Feb(?:ruary)?|"
            									+ "Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|"
            									+ "Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?))");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                tpUserInput.getStyledDocument().setCharacterAttributes(matcher.start(), day.length(), customSet, true);
                taStatusMessage.setText("Special case detected. Please surround your task description with double quotes to prevent parsing errors. (e.g. \"lunch with john\" at 1pm)");
            }
        }
    }

	private void setupPanels() {
		inputPanel = new JPanel();
		inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
		inputPanel.setLayout(null);
		frmTodokoro.getContentPane().add(inputPanel);
	}

	private void setupTextFields() {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(4, 4, 0, 4);
		Border border = new CompoundBorder(rounded, empty);
		tpUserInput = new JTextPane();
		inputPanel.add(tpUserInput);
		tpUserInput.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		tpUserInput.setBounds(12, 87, 738, 38);
		tpUserInput.setBorder(border);
		tpUserInput.setFocusAccelerator('e');

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Send Command");
		tpUserInput.getActionMap().put("Send Command", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sendUserInput(tpUserInput.getText().trim());
				history.addInputHistory(tpUserInput.getText());
				tpUserInput.setText(null);
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Previous Input");
		tpUserInput.getActionMap().put("Previous Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String prevInput = history.getPreviousInput();
				if (prevInput != null) {
					tpUserInput.setText(prevInput);
				}
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Next Input");
		tpUserInput.getActionMap().put("Next Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String nextInput = history.getNextInput();
				if (nextInput != null) {
					tpUserInput.setText(nextInput);
				}
			}
		});

		tpUserInput.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {

			}

			public void insertUpdate(DocumentEvent e) {
				Runnable doHighlight = new Runnable() {
			        @Override
			        public void run() {
			        	highlightText();
			        }
			    };
			    SwingUtilities.invokeLater(doHighlight);
			}

			public void removeUpdate(DocumentEvent e) {
				Runnable doHighlight = new Runnable() {
			        @Override
			        public void run() {
			        	highlightText();
			        }
			    };
			    SwingUtilities.invokeLater(doHighlight);
			}
		});

		tfFilter = new JTextField();
		tfFilter.setBounds(594, 12, 156, 26);
		frmTodokoro.getContentPane().add(tfFilter);
		tfFilter.setColumns(10);
		Border inner = new EmptyBorder(0, 4, 0, 4);
		Border compBorder = new CompoundBorder(rounded, inner);
		tfFilter.setBorder(compBorder);
		tfFilter.setFocusAccelerator('f');
		tfFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				rowFilter(eventsSorter, 3);
				rowFilter(todosSorter, 1);
				rowFilter(deadlinesSorter, 2);
			}

			public void insertUpdate(DocumentEvent e) {
				rowFilter(eventsSorter, 3);
				rowFilter(todosSorter, 1);
				rowFilter(deadlinesSorter, 2);
			}

			public void removeUpdate(DocumentEvent e) {
				rowFilter(eventsSorter, 3);
				rowFilter(todosSorter, 1);
				rowFilter(deadlinesSorter, 2);
			}
		});
	}

	private void setupLabels() {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(0, 5, 0, 5);
		Border border = new CompoundBorder(rounded, empty);
		TitledBorder titled = new TitledBorder(border, "Status Message", 0, 0, new Font("Segoe UI", Font.BOLD, 14));
		taStatusMessage = new JTextArea(2, 20);
		taStatusMessage.setBounds(12, 4, 738, 75);
		taStatusMessage.setWrapStyleWord(true);
		taStatusMessage.setLineWrap(true);
		taStatusMessage.setOpaque(false);
		taStatusMessage.setEditable(false);
		taStatusMessage.setFocusable(false);
		taStatusMessage.setBackground(UIManager.getColor("Label.background"));
		taStatusMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
		taStatusMessage.setBorder(titled);
		inputPanel.add(taStatusMessage);

		/*tpStatusMessage = new JTextPane();
		tpStatusMessage.setBounds(12, 4, 738, 75);
		tpStatusMessage.setOpaque(false);
		tpStatusMessage.setEditable(false);
		tpStatusMessage.setFocusable(false);
		tpStatusMessage.setMaximumSize(new Dimension(738, 75));
		tpStatusMessage.setBackground(UIManager.getColor("Label.background"));
		TitledBorder titled = new TitledBorder("Status Message");
		UIManager.put("TitledBorder.border", border);
		tpStatusMessage.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		tpStatusMessage.setBorder(titled);
		inputPanel.add(tpStatusMessage);*/

		//lblStatusMsg = new JLabel();
		//TitledBorder titled = new TitledBorder("Status Message");
		//UIManager.put("TitledBorder.border", new LineBorder(new Color(200,200,200), 2));
		//lblStatusMsg.setBorder(titled);
		//lblStatusMsg.setVerticalAlignment(SwingConstants.TOP);
		//lblStatusMsg.setHorizontalAlignment(SwingConstants.LEFT);
		//lblStatusMsg.setPreferredSize(new Dimension(1, 1));
		//lblStatusMsg.setVerticalAlignment(SwingConstants.CENTER);
		//lblStatusMsg.setHorizontalAlignment(SwingConstants.CENTER);
		//lblStatusMsg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		// lblStatusMsg.setBounds(67, 484, 683, 39);
		//lblStatusMsg.setBounds(298, 35, 738, 79);
		//inputPanel.add(lblStatusMsg);

		lblFilter = new JLabel("Filter:");
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblFilter.setBounds(530, 16, 60, 16);
		frmTodokoro.getContentPane().add(lblFilter);
	}

	private void setupTableSorters() {
		eventsSorter = new TableRowSorter<EventsTableModel>(etm);
		eventsTable.setRowSorter(eventsSorter);
		eventsSorter.toggleSortOrder(1);

		todosSorter = new TableRowSorter<TodosTableModel>(ttm);
		todosTable.setRowSorter(todosSorter);
		todosSorter.toggleSortOrder(2);

		deadlinesSorter = new TableRowSorter<DeadlinesTableModel>(dtm);
		deadlinesTable.setRowSorter(deadlinesSorter);
		deadlinesSorter.toggleSortOrder(1);
	}

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		//tabbedPane.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
		tabbedPane.setBounds(12, 8, 738, 465);
		eventsScrollPane = new JScrollPane();
		todosScrollPane = new JScrollPane();
		deadlineTasksScrollPane = new JScrollPane();
		eventsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		todosScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		deadlineTasksScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events (1)</b></body></html>",
							null, eventsScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos (2)</b></body></html>",
							null, todosScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Deadlines (3)</b></body></html>",
							null, deadlineTasksScrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	private void setupTables() {
		setupDeadlineTasksTable();
		setupTodosTable();
		setupEventsTable();
	}

	private void setupEventsTable() {
		eventsTable = new JTable();
		eventsTable.setName("Events");
		eventsTable.setModel(etm);
		setupTableProperties(eventsTable);
		setupRenderersAndEditors(eventsTable);
		setupDimensions(eventsTable);
		eventsScrollPane.setViewportView(eventsTable);
	}

	private void setupTodosTable() {
		todosTable = new JTable();
		todosTable.setName("Todos");
		todosTable.setModel(ttm);
		setupTableProperties(todosTable);
		setupRenderersAndEditors(todosTable);
		setupDimensions(todosTable);
		todosScrollPane.setViewportView(todosTable);
	}

	private void setupDeadlineTasksTable() {
		deadlinesTable = new JTable();
		deadlinesTable.setName("Deadlines");
		deadlinesTable.setModel(dtm);
		setupTableProperties(deadlinesTable);
		setupRenderersAndEditors(deadlinesTable);
		setupDimensions(deadlinesTable);
		deadlineTasksScrollPane.setViewportView(deadlinesTable);
	}

	private void setupRenderersAndEditors(JTable table) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		CustomCellRenderer customRenderer = new CustomCellRenderer();
		CustomDateCellEditor customDateEditor = new CustomDateCellEditor();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		table.setDefaultRenderer(Integer.class, centerRenderer);
		table.setDefaultRenderer(String.class, customRenderer);

		if (table.getName().equals("Events") || table.getName().equals("Deadlines")) {
			table.setDefaultRenderer(Date.class, customRenderer);
			table.setDefaultEditor(Date.class, customDateEditor);
		}
	}

	private void rowFilter(TableRowSorter<?> sorter, int index) {
		RowFilter<Object, Object> rowFilter = null;
		List<RowFilter<Object, Object>> rowfilterList = new ArrayList<RowFilter<Object, Object>>();

		try {
			String text = tfFilter.getText();

			if (text.equals("done")) {
				rowFilter = RowFilter.regexFilter("^true$");
			} else if (text.equals("!done") || text.equals("not done") || text.equals("undone")) {
				rowFilter = RowFilter.regexFilter("^false$");
			} else {
				String[] textArray = text.split(" ");

				for (int i = 0; i < textArray.length; i++) {
					rowfilterList.add(RowFilter.regexFilter("(?iu)" + textArray[i], index, index+1));
				}

				rowFilter = RowFilter.andFilter(rowfilterList);
			}
		} catch (PatternSyntaxException e) {
			logger.log(Level.SEVERE, "Row Filter:" + e.getMessage());
		}

		sorter.setRowFilter(rowFilter);
	}

	public void updateTables(EnumTypes.TASK_TYPE type, List<Task> tasks, boolean shouldSwitch) {
		switch (type) {
		case EVENT:
			etm.setTasks(tasks);
			etm.fireTableDataChanged();
			if (shouldSwitch) {
				tabbedPane.setSelectedIndex(0);
			}
			break;
		case TODO:
			ttm.setTasks(tasks);
			ttm.fireTableDataChanged();
			if (shouldSwitch) {
				tabbedPane.setSelectedIndex(1);
			}
			break;
		case DEADLINE:
			dtm.setTasks(tasks);
			dtm.fireTableDataChanged();
			if (shouldSwitch) {
				tabbedPane.setSelectedIndex(2);
			}
			break;
		default:
		}
	}

	private void setupDimensions(JTable table) {
		table.setRowHeight(TABLE_ROW_HEIGHT);
		table.getColumnModel().getColumn(0).setMaxWidth(45);

		switch (table.getName()) {
		case "Events":
			table.getColumnModel().getColumn(1).setMinWidth(130);
			table.getColumnModel().getColumn(1).setMaxWidth(130);
			table.getColumnModel().getColumn(2).setMinWidth(130);
			table.getColumnModel().getColumn(2).setMaxWidth(130);
			table.getColumnModel().getColumn(3).setMinWidth(379);
			table.getColumnModel().getColumn(3).setMaxWidth(684);
			table.getColumnModel().getColumn(4).setMaxWidth(50);
			break;
		case "Todos":
			table.getColumnModel().getColumn(1).setMinWidth(639);
			table.getColumnModel().getColumn(2).setMaxWidth(50);
			break;
		case "Deadlines":
			table.getColumnModel().getColumn(1).setMinWidth(130);
			table.getColumnModel().getColumn(1).setMaxWidth(130);
			table.getColumnModel().getColumn(2).setMinWidth(507);
			table.getColumnModel().getColumn(3).setMaxWidth(50);
			break;
		}
	}

	private void setupTableProperties(JTable table) {
		// table.setAutoCreateRowSorter(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//table.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
	}

	public void fakeInputComeIn(String command) {
		sendUserInput(command);
	}

	public void updateStatusMsg(String msg) {
		//lblStatusMsg.setText("<html><body style='width:100px'>" + msg + "</body></html>");
		taStatusMessage.setText(msg);
		//tpStatusMessage.setText(msg);
	}

	private void sendUserInput(String command) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_USER_INPUT_CODE, new ObserverEvent.EInput(command)));
	}

	@Override
	public void update(Observable observable, Object event) {

		ObserverEvent OEvent = (ObserverEvent) event;

		if (OEvent.getCode() == ObserverEvent.CHANGE_MESSAGE_CODE) {
			ObserverEvent.EMessage eMessage = (ObserverEvent.EMessage) OEvent.getPayload();
			//System.out.println(eMessage.getMessage());
			updateStatusMsg(eMessage.getMessage());
			return;
		}

		if (OEvent.getCode() == ObserverEvent.CHANGE_TABLE_CODE) {
			ObserverEvent.ETasks eTasks = (ObserverEvent.ETasks) OEvent.getPayload();
			//System.out.println(eTasks.getTaskType());
			updateTables(eTasks.getTaskType(), eTasks.getTasks(), eTasks.shouldSwitch());
			return;
		}

	}
}
```

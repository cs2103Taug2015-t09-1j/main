# Dalton
###### main\logic\Add.java
``` java
 */
public class Add extends Command {
	private static Add add = null;
	private static Storage storage = null;
	private static Parser parser = null;
	private static VersionControl vControl = null;
	private static final Logger logger = Logger.getLogger(Add.class.getName());
	private static final boolean DEBUG = true;

	/**
	 * Instantiates a new add.
	 */
	private Add() {
		storage = Storage.getInstance();
		parser = Parser.getInstance();
		vControl = VersionControl.getInstance();
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	/**
	 * Gets the single instance of Add.
	 *
	 * @return single instance of Add
	 */
	public static Add getInstance() {
		if (add == null) {
			add = new Add();
		}
		return add;
	}

	/**
	 * Executes the Add command
	 *
	 * @param ParsedObject	the ParsedObject containing command information from the Parser
	 * @return 				true if successfully deleted
	 */
	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		assert obj.getObjects() instanceof ArrayList;

		ArrayList<Task> tasks = obj.getObjects();

		if (!tasks.isEmpty()) {
			Task task = tasks.get(0);
			message = "\"" + task.getTaskDesc() + "\" has been successfully added as ";
			switch (obj.getTaskType()) {
				case SINGLE_DATE_EVENT:
					Event sEvt = (Event)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					addNewTask(sEvt);
					setMessage(sEvt, EnumTypes.TASK_TYPE.SINGLE_DATE_EVENT);

					if (DEBUG) {
						logger.log(Level.FINE, "Event Added: {" + sEvt.getTaskID() + ", " + sEvt.getTaskDesc() + ", " + sEvt.getFromDate() + ", " + sEvt.getToDate() + "}");
					}

					return true;
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					addNewTask(dEvt);
					setMessage(dEvt, EnumTypes.TASK_TYPE.DOUBLE_DATE_EVENT);

					if (DEBUG) {
						logger.log(Level.FINE, "Event Added: {" + dEvt.getTaskID() + ", " + dEvt.getTaskDesc() + ", " + dEvt.getFromDate() + ", " + dEvt.getToDate() + "}");
					}
					return true;
				case TODO:
					Todo tt = (Todo)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.TODO;
					addNewTask(tt);
					setMessage(tt, taskType);

					if (DEBUG) {
						logger.log(Level.FINE, "Todo Added: {" + tt.getTaskID() + ", " + tt.getTaskDesc() + "}");
					}

					return true;
				case DEADLINE:
					Deadline dt = (Deadline)tasks.get(0);
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					addNewTask(dt);
					setMessage(dt, taskType);

					if (DEBUG) {
						logger.log(Level.FINE, "Deadline Added: {" + dt.getTaskID() + ", " + dt.getTaskDesc() + ", " + dt.getDate() + "}");
					}

					return true;
				default:
					// default case handled outside
			}
		}

		taskType = EnumTypes.TASK_TYPE.INVALID;
		setMessage(null, taskType);
		if (DEBUG) {
			logger.log(Level.SEVERE, "Add Command Failed: " + obj);
		}

		taskType = EnumTypes.TASK_TYPE.INVALID;
		message = "Add command has failed.";

		return false;
	}

	/**
	 * Undo Add.
	 *
	 * @param task	the task
	 * @return 		true, if successful
	 */
	public boolean undo(Task task) {
		return storage.delete(task.getTaskID());
	}

	/**
	 * Redo Add.
	 *
	 * @param task		the task
	 * @return 			true, if successful
	 */
	public boolean redo(Task task) {
		return storage.addTask(task);
	}

	/**
	 * Adds the new task.
	 *
	 * @param task	the task
	 */
	private void addNewTask(Task task) {
		storage.addTask(task);
		storage.saveTaskType(taskType);
		vControl.addNewData(new VersionModel.AddModel(task));
	}

	/**
	 * Sets the feedback message.
	 *
	 * @param task			the task
	 * @param specficType		the specfic type
	 */
	private void setMessage(Task task, EnumTypes.TASK_TYPE specficType) {
		switch (specficType) {
			case SINGLE_DATE_EVENT:
				Event sdEvt = (Event)task;
				message += "an Event on " + parser.formatDate(sdEvt.getFromDate(), "EEE, d MMM yyyy") + " at " + parser.formatDate(sdEvt.getFromDate(), "h:mm a") + ".";
				break;
			case DOUBLE_DATE_EVENT:
				Event ddEvt = (Event)task;
				message += "an Event from " + parser.formatDate(ddEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + " to " + parser.formatDate(ddEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + ".";
				break;
			case DEADLINE:
				Deadline dd = (Deadline)task;
				message += "a Deadline task that must be completed by " + parser.formatDate(dd.getDate(), "h:mm aa") + " on " + parser.formatDate(dd.getDate(), "EEE, d MMM yyyy") + ".";
				break;
			case TODO:
				message += "a Todo task.";
				break;
			default:
				message = "Add command has failed.";
		}
	}
}
```
###### main\logic\ChangeDirectory.java
``` java
 */
public class ChangeDirectory extends JFrame {
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(ChangeDirectory.class.getName());

	/**
	 * Instantiates a new change directory.
	 *
	 * @param frame	 the frame
	 */
	public ChangeDirectory(JFrame frame) {
		LogFileHandler.getInstance().addLogFileHandler(logger);
		JFileChooser dirChooser = new JFileChooser();
		String title = "Select a directory";

		dirChooser.setCurrentDirectory(new File("."));
		dirChooser.setDialogTitle(title);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setAcceptAllFileFilterUsed(false);
		dirChooser.setVisible(true);

	    if (dirChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
	    	storage.setStoreDir(dirChooser.getSelectedFile().getPath());
	    }

	    try {
	    	this.dispose();
	    } catch (Exception e) {
	    	logger.log(Level.SEVERE, e.getMessage(), e);
	    }
	}
}
```
###### main\logic\Command.java
``` java
 */
public abstract class Command {
	protected EnumTypes.TASK_TYPE taskType;
	protected String message = "";

	/**
	 * Execute.
	 *
	 * @param obj	the obj
	 * @return 		true, if successful
	 */
	public abstract boolean execute(ParsedObject obj);

	/**
	 * Gets the feedback message.
	 *
	 * @return the feedback message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the task type.
	 *
	 * @return the task type
	 */
	public EnumTypes.TASK_TYPE getTaskType() {
		return this.taskType;
	}
}
```
###### main\logic\Delete.java
``` java
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
```
###### main\logic\Delete.java
``` java
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
```
###### main\logic\Display.java
``` java
	 *
	 * @param d				the d
	 * @param isStartOfDay	the is start of day
	 * @return 				the date
	 */
	private Date resetTime(Date d, boolean isStartOfDay) {
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if (isStartOfDay) {
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);
		} else {
			date.set(Calendar.HOUR_OF_DAY, 23);
			date.set(Calendar.MINUTE, 59);
			date.set(Calendar.SECOND, 59);
			date.set(Calendar.MILLISECOND, 999);
		}

		return date.getTime();
	}
}
```
###### main\logic\Logic.java
``` java
 */
public class Logic extends Observable implements Observer {
	private static Logic logic = null;
	private static Storage storage = null;
	private static Parser parser = null;
	private final Logger logger = Logger.getLogger(Logic.class.getName());

	/**
	 * Instantiates a new logic.
	 */
	private Logic() {}

	/**
	 * Initialise.
	 */
	private static void initialise() {
		logic = new Logic();
		logic.addObserver(MainGUI.getInstance());

		storage = Storage.getInstance();
		parser = Parser.getInstance();

		logic.updateModelData(TASK_TYPE.DEADLINE, false);
		logic.updateModelData(TASK_TYPE.TODO, false);
		logic.updateModelData(TASK_TYPE.EVENT, true);
	}

	/**
	 * Gets the single instance of Logic.
	 *
	 * @return single instance of Logic
	 */
	public static Logic getInstance() {
		if (logic == null) {
			initialise();
		}
		return logic;
	}

	/**
	 * Process command.
	 *
	 * @param input		the input
	 */
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

	/**
	 * Process add command.
	 *
	 * @param input		the input
	 */
	private void processAddCommand(String input) {
		Add addCmd = Add.getInstance();
		if (addCmd.execute(parser.getAddParsedObject(input))) {
			updateModelData(addCmd.getTaskType(), true);
		}
		updateMessage(addCmd.getMessage());
	}

	/**
	 * Process update command.
	 *
	 * @param input		the input
	 */
	private void processUpdateCommand(String input) {
		Update updateCmd = Update.getInstance();
		if (updateCmd.execute(parser.getUpdateParsedObject(input))) {
			updateModelData(updateCmd.getTaskType(), true);
		}
		updateMessage(updateCmd.getMessage());
	}

	/**
	 * Process delete command.
	 *
	 * @param input		the input
	 */
	private void processDeleteCommand(String input) {
		Delete deleteCmd = Delete.getInstance();
		if (deleteCmd.execute(parser.getDeleteParsedObject(input))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(deleteCmd.getMessage());
	}

	/**
	 * Process change status command.
	 *
	 * @param input			the input
	 * @param newStatus		the new status
	 */
	private void processChangeStatusCommand(String input, boolean newStatus) {
		ChangeStatus changeStatus = ChangeStatus.getInstance(newStatus);
		if (changeStatus.execute(parser.getChangeStatusParsedObject(input, newStatus))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(changeStatus.getMessage());
	}

	/**
	 * Process undo command.
	 *
	 * @param input		the input
	 */
	private void processUndoCommand(String input) {
		VersionControl vControl = VersionControl.getInstance();
		if (vControl.execute(parser.getUndoRedoParsedObject(input, COMMAND_TYPE.UNDO))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(vControl.getMessage());
	}

	/**
	 * Process redo command.
	 *
	 * @param input		the input
	 */
	private void processRedoCommand(String input) {
		VersionControl vControl = VersionControl.getInstance();
		if (vControl.execute(parser.getUndoRedoParsedObject(input, COMMAND_TYPE.REDO))) {
			updateModelData(TASK_TYPE.DEADLINE, false);
			updateModelData(TASK_TYPE.TODO, false);
			updateModelData(TASK_TYPE.EVENT, false);
		}
		updateMessage(vControl.getMessage());
	}

	/**
	 * Process display command.
	 *
	 * @param input		the input
	 */
	private void processDisplayCommand(String input) {
		Display displayCmd = Display.getInstance(TASK_TYPE.DEADLINE);
		List<List<Task>> temp = displayCmd.process(parser.getDisplayParsedObject(input));
		if (temp != null) {
			updateModelData(TASK_TYPE.DEADLINE, temp.get(0), false);
			updateModelData(TASK_TYPE.EVENT, temp.get(1), false);
			updateModelData(TASK_TYPE.TODO, temp.get(2), false);
		}
		updateMessage(displayCmd.getMessage());
	}

	/**
	 * Update model data.
	 *
	 * @param type			the type
	 * @param shouldSwitch	the should switch
	 */
	private void updateModelData(TASK_TYPE type, boolean shouldSwitch) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_TABLE_CODE, new ObserverEvent.ETasks(storage.getAllTask(type), type, shouldSwitch)));
	}

	/**
	 * Update model data.
	 *
	 * @param type	the type
	 * @param tasks	the tasks
	 * @param shouldSwitch	the should switch
	 */
	private void updateModelData(TASK_TYPE type, List<Task> tasks, boolean shouldSwitch) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_TABLE_CODE, new ObserverEvent.ETasks(tasks, type, shouldSwitch)));
	}

	/**
	 * Update message.
	 *
	 * @param message	the message
	 */
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
 */
public class Update extends Command {
	private static Update update = null;
	private static Parser parser = null;
	private static Storage storage = null;
	private static VersionControl vControl = null;
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final boolean DEBUG = true;

	/**
	 * Instantiates a new update.
	 */
	private Update() {
		parser = Parser.getInstance();
		storage = Storage.getInstance();
		vControl = VersionControl.getInstance();
	}

	/**
	 * Gets the single instance of Update.
	 *
	 * @return single instance of Update
	 */
	public static Update getInstance() {
		if (update == null) {
			update = new Update();
		}
		return update;
	}

	/**
	 * Executes the Update command
	 *
	 * @param ParsedObject	the ParsedObject containing command information from the Parser
	 * @return 				true if successfully deleted
	 */
	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		assert obj.getObjects() instanceof ArrayList;

		ArrayList<String> params = obj.getObjects();
		Task task = storage.getTaskByID(parser.parseInteger(params.get(0)));

		if (task != null) {
			message = "Task ID " + task.getTaskID() + ": ";
			switch (task.getType()) {
				case EVENT:
					taskType = EnumTypes.TASK_TYPE.EVENT;
					return updateEvent((Event) task, params);
				case TODO:
					taskType = EnumTypes.TASK_TYPE.TODO;
					return updateTodo((Todo) task, params);
				case DEADLINE:
					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					return updateDeadline((Deadline) task, params);
			}
		}

		message = "Invalid column or value entered.";
		taskType = EnumTypes.TASK_TYPE.INVALID;
		return false;
	}

	/**
	 * Update event.
	 *
	 * @param evt		the evt
	 * @param params	the params
	 * @return 			true, if successful
	 */
	private boolean updateEvent(Event evt, ArrayList<String> params) {
		Task oldEvt = evt.clone();

		switch (params.get(1)) {
			case "2":
				try {
					Date fromDate = parser.parseDateGroups(params.get(2)).get(0);
					message += "\"" + parser.formatDate(evt.getFromDate(), "EEE, d MMM yyyy h:mm a") + "\" has been updated to \""
							+ parser.formatDate(fromDate,  "EEE, d MMM yyyy h:mm a") + "\".";
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
					message += "\"" + parser.formatDate(evt.getToDate(),  "EEE, d MMM yyyy h:mm a") + "\" has been updated to \""
							+ parser.formatDate(toDate,  "EEE, d MMM yyyy h:mm a") + "\".";
					evt.setToDate(toDate);
				} catch (Exception e) {
					message += "Invalid column or value entered.";
					taskType = EnumTypes.TASK_TYPE.INVALID;
					return false;
				}
				break;
			case "4":
				String taskDesc = params.get(2);
				message += "\"" + evt.getTaskDesc() + "\" has been updated to \"" + taskDesc + "\".";
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

	/**
	 * Update todo.
	 *
	 * @param t			the t
	 * @param params	the params
	 * @return 			true, if successful
	 */
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

	/**
	 * Update deadline.
	 *
	 * @param d			the d
	 * @param params	the params
	 * @return 			true, if successful
	 */
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

	/**
	 * Adds the new update model.
	 *
```
###### main\model\EnumTypes.java
``` java
 */
public class EnumTypes {

	public static enum COMMAND_TYPE {
		ADD, DELETE, UPDATE, SEARCH, DISPLAY, UNDO, REDO, EXIT, INVALID, DONE, UNDONE, DONE_UNDONE, DISPLAY_ON, DISPLAY_BETWEEN, DISPLAY_ON_BETWEEN, DISPLAY_ALL
	};

	public static enum TASK_TYPE {
		SINGLE_DATE_EVENT, DOUBLE_DATE_EVENT, EVENT, DEADLINE, TODO, ALL, INVALID
	};

	public static enum PARAM_TYPE {
		ID, CATEGORY, TIME, TASK, DATE, INTEGER, STRING
	};

	public static enum CATEGORY {
		COMPLETED, UNCOMPLETED, EXPIRED, NONEXPIRED, ALL
	};

}
```
###### main\model\ParsedObject.java
``` java
 */
public class ParsedObject {
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	private ArrayList objects;
	private PARAM_TYPE paramType;

	/**
	 * Instantiates a new parsed object.
	 */
	public ParsedObject() {}

	/**
	 * Instantiates a new parsed object.
	 *
	 * @param commandType	the command type
	 */
	public ParsedObject(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	/**
	 * Instantiates a new parsed object.
	 *
	 * @param commandType	the command type
	 * @param paramType		the param type
	 * @param objects		the objects
	 */
	public ParsedObject(COMMAND_TYPE commandType, PARAM_TYPE paramType, ArrayList objects) {
		this.commandType = commandType;
		this.paramType = paramType;
		this.objects = objects;
	}

	/**
	 * Instantiates a new parsed object.
	 *
	 * @param commandType	the command type
	 * @param paramType		the param type
	 * @param taskType		the task type
	 * @param objects		the objects
	 */
	public ParsedObject(COMMAND_TYPE commandType, PARAM_TYPE paramType, TASK_TYPE taskType, ArrayList objects) {
		this.commandType = commandType;
		this.taskType = taskType;
		this.objects = objects;
		this.paramType = paramType;
	}


	/**
	 * Gets the command type.
	 *
	 * @return the command type
	 */
	public COMMAND_TYPE getCommandType() {
		return commandType;
	}

	/**
	 * Sets the command type.
	 *
	 * @param commandType	the new command type
	 */
	public void setCommandType(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	/**
	 * Gets the task type.
	 *
	 * @return the task type
	 */
	public TASK_TYPE getTaskType() {
		return taskType;
	}

	/**
	 * Sets the task type.
	 *
	 * @param taskType	the new task type
	 */
	public void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}

	/**
	 * Gets the objects.
	 *
	 * @return the objects
	 */
	public ArrayList getObjects() {
		return objects;
	}

	/**
	 * Sets the tasks.
	 *
	 * @param objects	the new tasks
	 */
	public void setTasks(ArrayList objects) {
		this.objects = objects;
	}

	/**
	 * Gets the param type.
	 *
	 * @return the param type
	 */
	public PARAM_TYPE getParamType() {
		return this.paramType;
	}

	/**
	 * Sets the param type.
	 *
	 * @param paramType	the new param type
	 */
	public void setParamType(PARAM_TYPE paramType) {
		this.paramType = paramType;
	}
}
```
###### main\model\tableModels\DeadlinesTableModel.java
``` java
 */
@SuppressWarnings("serial")
public class DeadlinesTableModel extends AbstractTableModel {
	private static DeadlinesTableModel dtm = null;
	private static MainGUI mainGUI = null;
	private List<Task> deadlines = null;
	private final String[] columnNames = { "ID", "Deadline (2)", "Task Description (3)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, Date.class, String.class, Boolean.class };


	/**
	 * Instantiates a new deadlines table model.
	 */
	private DeadlinesTableModel() {
		super();
		deadlines = new ArrayList<Task>();
	}

	/**
	 * Gets the single instance of DeadlinesTableModel.
	 *
	 * @return single instance of DeadlinesTableModel
	 */
	public static DeadlinesTableModel getInstance() {
		if (dtm == null) {
			dtm = new DeadlinesTableModel();
		}
		return dtm;
	}

	/**
	 * Sets the UI instance.
	 *
	 * @param ui	the new UI instance
	 */
	public void setUIInstance(MainGUI ui) {
		assert ui != null;
		mainGUI = ui;
	}

	/**
	 * Sets the tasks.
	 *
	 * @param tasks	the new tasks
	 */
	public void setTasks(List<Task> tasks) {
		assert tasks != null;
		deadlines = tasks;
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
		Deadline deadline = (Deadline)deadlines.get(row);
		String simulatedCommand = "update " + deadline.getTaskID() + " " + (col + 1) + " ";
		String updatedValue = "";
		String originalValue = "";
		switch (col) {
			case 1:
				originalValue += deadline.getDate();
				updatedValue += value;
				break;
			case 2:
				originalValue += deadline.getTaskDesc();
				updatedValue += value;
				break;
			case 3:
				originalValue += value;
				simulatedCommand = ((Boolean)value ?  "done" : "undone") + " " + deadline.getTaskID();
				break;
			default:
				// impossible case
				assert false : col;
		}

		// Sends a simulated command if the user updates from the table directly
		if (!updatedValue.trim().equals(originalValue)) {
			mainGUI.sendUserInput(simulatedCommand + updatedValue);
		}
    }

	public Object getValueAt(int row, int col) {
		Deadline deadline = (Deadline)deadlines.get(row);
		switch (col) {
			case 0:
				return deadline.getTaskID();
			case 1:
				return deadline.getDate();
			case 2:
				return deadline.getTaskDesc();
			case 3:
				return deadline.isDone();
			default:
				// impossible case
				assert false : col;
		}

		return new String();
	}


}
```
###### main\model\tableModels\EventsTableModel.java
``` java
 */
@SuppressWarnings("serial")
public class EventsTableModel extends AbstractTableModel {
	private static EventsTableModel etm = null;
	private static MainGUI mainGUI = null;
	private List<Task> events = null;
	private final String[] columnNames = {"ID", "Start Date (2)", "End Date (3)", "Task Description (4)", "Done"};
	private final Class<?>[] columnTypes = {Integer.class, Date.class, Date.class, String.class, Boolean.class};

	/**
	 * Instantiates a new events table model.
	 */
	private EventsTableModel() {
		super();
		events = new ArrayList<Task>();
	}

	/**
	 * Gets the single instance of EventsTableModel.
	 *
	 * @return single instance of EventsTableModel
	 */
	public static EventsTableModel getInstance() {
		if (etm == null) {
			etm = new EventsTableModel();
		}
		return etm;
	}

	/**
	 * Sets the UI instance.
	 *
	 * @param ui	the new UI instance
	 */
	public void setUIInstance(MainGUI ui) {
		assert ui != null;
		mainGUI = ui;
	}

	/**
	 * Sets the tasks.
	 *
	 * @param tasks	the new tasks
	 */
	public void setTasks(List<Task> tasks) {
		assert tasks != null;
		events = tasks;
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
		String simulatedCommand = "update " + evt.getTaskID() + " " + (col + 1) + " ";
		String updatedValue = "";
		String originalValue = "";
		switch (col) {
			case 1:
				originalValue += evt.getFromDate();
				updatedValue += value;
				break;
			case 2:
				originalValue += evt.getToDate();
				updatedValue += value;
				break;
			case 3:
				originalValue += evt.getTaskDesc();
				updatedValue += value;
				break;
			case 4:
				originalValue += value;
				simulatedCommand = ((Boolean)value ?  "done" : "undone") + " " + evt.getTaskID();
				break;
			default:
				// impossible case
				assert false : col;
		}

		// Sends a simulated command if the user updates from the table directly
		if (!updatedValue.trim().equals(originalValue)) {
			mainGUI.sendUserInput(simulatedCommand + updatedValue);
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
				return evt.getToDate();
			case 3:
				return evt.getTaskDesc();
			case 4:
				return evt.isDone();
			default:
				// impossible case
				assert false : col;
		}

		return new String();
	}
}
```
###### main\model\tableModels\TodosTableModel.java
``` java
 */
@SuppressWarnings("serial")
public class TodosTableModel extends AbstractTableModel {
	private static TodosTableModel ttm = null;
	private static MainGUI mainGUI = null;
	private List<Task> todos = null;
	private final String[] columnNames = { "ID", "Task Description (2)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, String.class, Boolean.class };

	/**
	 * Instantiates a new todos table model.
	 */
	private TodosTableModel() {
		super();
		todos = new ArrayList<Task>();
	}

	/**
	 * Gets the single instance of TodosTableModel.
	 *
	 * @return single instance of TodosTableModel
	 */
	public static TodosTableModel getInstance() {
		if (ttm == null) {
			ttm = new TodosTableModel();
		}
		return ttm;
	}

	/**
	 * Sets the UI instance.
	 *
	 * @param ui	the new UI instance
	 */
	public void setUIInstance(MainGUI ui) {
		assert ui != null;
		mainGUI = ui;
	}

	/**
	 * Sets the tasks.
	 *
	 * @param tasks	the new tasks
	 */
	public void setTasks(List<Task> tasks) {
		assert tasks != null;
		todos = tasks;
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
		Todo todo = (Todo)todos.get(row);
		String simulatedCommand = "update " + todo.getTaskID() + " " + (col + 1) + " ";
		String updatedValue = "";
		String originalValue = "";
		switch (col) {
			case 1:
				originalValue += todo.getTaskDesc();
				updatedValue += value;
				break;
			case 2:
				originalValue += value;
				simulatedCommand = ((Boolean)value ?  "done" : "undone") + " " + todo.getTaskID();
				break;
			default:
				// impossible case
				assert false : col;
		}

		// Sends a simulated command if the user updates from the table directly
		if (!updatedValue.trim().equals(originalValue)) {
			mainGUI.sendUserInput(simulatedCommand + updatedValue);
		}
    }

	public Object getValueAt(int row, int col) {
		Todo todo = (Todo)todos.get(row);
		switch (col) {
			case 0:
				return todo.getTaskID();
			case 1:
				return todo.getTaskDesc();
			case 2:
				return todo.isDone();
			default:
				// impossible case
				assert false : col;
		}

		return new String();
	}
}
```
###### main\model\taskModels\Deadline.java
``` java
 */
public class Deadline extends Task {
	private static final TASK_TYPE type = TASK_TYPE.DEADLINE;
	private Date date;

	/**
	 * Instantiates a new deadline.
	 *
	 * @param date		the date
	 * @param taskDesc	the task desc
	 * @param isDone	the is done boolean
	 */
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

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date
	 *            the new date
	 */
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
 */
public class Event extends Task {
	private static final TASK_TYPE type = TASK_TYPE.EVENT;
	private Date fromDate;
	private Date toDate;

	/**
	 * Instantiates a new event.
	 *
	 * @param fromDate	the from date
	 * @param toDate	the to date
	 * @param taskDesc	the task desc
	 * @param isDone	the is done
	 */
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

	/**
	 * Gets the from date.
	 *
	 * @return the from date
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 *
	 * @param fromDate	the new from date
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 *
	 * @return the to date
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 *
	 * @param toDate	the new to date
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}
}
```
###### main\model\taskModels\Task.java
``` java
 */
public abstract class Task implements Cloneable {

	// nextId is used to create an auto increment index for new task
	private static AtomicInteger nextId = new AtomicInteger();

	private int taskID;
	private String taskDesc;
	private boolean isDone;

	/**
	 * Instantiates a new task.
	 *
	 * @param taskDesc	the task desc
	 * @param isDone	the is done
	 */
	public Task(String taskDesc, boolean isDone) {
		this.taskID = nextId.incrementAndGet();
		this.taskDesc = taskDesc;
		this.isDone = isDone;
	}

	@Override
	public Task clone() {
		try {
			return (Task)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * Sets the task id.
	 *
	 * @param taskID	the new task id
	 */
	public void setTaskId(int taskID) {
		this.taskID = taskID;
	}

	/**
	 * Gets the task desc.
	 *
	 * @return the task desc
	 */
	public String getTaskDesc() {
		return taskDesc;
	}

	/**
	 * Sets the task desc.
	 *
	 * @param taskDesc	the new task desc
	 */
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	/**
	 * Checks if is done.
	 *
	 * @return true, if is done
	 */
	public boolean isDone() {
		return isDone;
	}

	/**
	 * Sets the done.
	 *
	 * @param isDone	the new done
	 */
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public TASK_TYPE getType() {
		return null;
	}

	/**
	 * Sets the next id.
	 *
	 * @param initState	the new next id
	 */
	public static void setNextId(int initState) {
		nextId = new AtomicInteger(initState);
	}
}
```
###### main\model\taskModels\Todo.java
``` java
 */
public class Todo extends Task {
	private static final TASK_TYPE type = TASK_TYPE.TODO;

	/**
	 * Instantiates a new todo.
	 *
	 * @param taskDesc	the task desc
	 * @param isDone	the is done
	 */
	public Todo(String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}

}
```
###### main\parser\ChiToEngConverter.java
``` java
 */
public class ChiToEngConverter {

	/**
	 * Instantiates a new ChiToEngConverter.
	 */
	private ChiToEngConverter() {}

	// Chinese To-Be-Converted Constants
	public static final String O_CLOCK = "\u70b9";

	public static final String ONE = "\u4e00";
	public static final String COUNT_TWO = "\u4e24";
	public static final String TWO = "\u4e8c";
	public static final String THREE = "\u4e09";
	public static final String FOUR = "\u56db";
	public static final String FIVE = "\u4e94";
	public static final String SIX = "\u516d";
	public static final String SEVEN = "\u4e03";
	public static final String EIGHT = "\u516b";
	public static final String NINE = "\u4e5d";
	public static final String TEN = "\u5341";
	public static final String ELEVEN = TEN + ONE;
	public static final String TWELVE = TEN + TWO;

	public static final String FROM = "\u4ece";
	public static final String TO = "\u5230";
	public static final String THE_FOLLOWING = "\u540e";
	public static final String THE_PREVIOUS = "\u524d";
	public static final String THE_LAST = "\u53bb";
	public static final String THE_UPCOMING = "\u4e0b(\u4e2a|" + ONE + ")";
	public static final String TONIGHT = "\u665a\u4e0a";
	public static final String MORNING = "\u65e9\u4e0a";
	public static final String AFTER_NOON = "\u4e0b\u5348";
	public static final String BEFORE_NOON = "\u4e0a\u5348";
	public static final String DAY = "\u5929";
	public static final String FOLLOWING_DAY = THE_FOLLOWING + DAY;
	public static final String PREVIOUS_DAY = THE_PREVIOUS + DAY;
	public static final String UPCOMING_DAY = THE_UPCOMING + DAY;

	public static final String YESTERDAY = "\u6628" + DAY;
	public static final String TODAY = "\u4eca" + DAY;
	public static final String TOMORROW = "\u660e" + DAY;

	public static final String DATE = "\u53f7|\u65e5";

	public static final String MONTH = "\u6708";
	public static final String FOLLOWING_MONTH = THE_FOLLOWING + MONTH;
	public static final String PREVIOUS_MONTH = THE_PREVIOUS + MONTH;
	public static final String UPCOMING_MONTH = THE_UPCOMING + MONTH;
	public static final String LAST_MONTH = THE_LAST + MONTH;
	public static final String JAN = ONE + MONTH;
	public static final String FEB = TWO + MONTH;
	public static final String MAR = THREE + MONTH;
	public static final String APR = FOUR + MONTH;
	public static final String MAY = FIVE + MONTH;
	public static final String JUN = SIX + MONTH;
	public static final String JUL = SEVEN + MONTH;
	public static final String AUG = EIGHT + MONTH;
	public static final String SEP = NINE + MONTH;
	public static final String OCT = TEN + MONTH;
	public static final String NOV = ELEVEN + MONTH;
	public static final String DEC = TWELVE + MONTH;

	public static final String YEAR = "\u5e74";
	public static final String FOLLOWING_YEAR = THE_FOLLOWING + YEAR;
	public static final String PREVIOUS_YEAR = THE_PREVIOUS + YEAR;
	public static final String UPCOMING_YEAR = THE_UPCOMING + YEAR;
	public static final String LAST_YEAR = THE_LAST + YEAR;

	public static final String PM = "(" + TONIGHT + "|" + AFTER_NOON + ")";
	public static final String AM = "(" + MORNING + "|" + BEFORE_NOON + ")";
	public static final String ONE_AM = "((?<=" + AM + ")(" + ONE + "|1)" + O_CLOCK + ")|((" + ONE + "|1)" + O_CLOCK + "(?=" + AM + "))";
	public static final String ONE_PM = "((?<=" + PM + ")(" + ONE + "|1)" + O_CLOCK + ")|((" + ONE + "|1)" + O_CLOCK + "(?=" + PM + "))";
	public static final String TWO_AM = "((?<=" + AM + ")(" + COUNT_TWO + "|2)" + O_CLOCK + ")|((" + COUNT_TWO + "|2)" + O_CLOCK + "(?=" + AM + "))";
	public static final String TWO_PM = "((?<=" + PM + ")(" + COUNT_TWO + "|2)" + O_CLOCK + ")|((" + COUNT_TWO + "|2)" + O_CLOCK + "(?=" + PM + "))";
	public static final String THREE_AM = "((?<=" + AM + ")(" + THREE + "|3)" + O_CLOCK + ")|((" + THREE + "|3)" + O_CLOCK + "(?=" + AM + "))";
	public static final String THREE_PM = "((?<=" + PM + ")(" + THREE + "|3)" + O_CLOCK + ")|((" + THREE + "|3)" + O_CLOCK + "(?=" + PM + "))";
	public static final String FOUR_AM = "((?<=" + AM + ")(" + FOUR + "|4)" + O_CLOCK + ")|((" + FOUR + "|4)" + O_CLOCK + "(?=" + AM + "))";
	public static final String FOUR_PM = "((?<=" + PM + ")(" + FOUR + "|4)" + O_CLOCK + ")|((" + FOUR + "|4)" + O_CLOCK + "(?=" + PM + "))";
	public static final String FIVE_AM = "((?<=" + AM + ")(" + FIVE + "|5)" + O_CLOCK + ")|((" + FIVE + "|5)" + O_CLOCK + "(?=" + AM + "))";
	public static final String FIVE_PM = "((?<=" + PM + ")(" + FIVE + "|5)" + O_CLOCK + ")|((" + FIVE + "|5)" + O_CLOCK + "(?=" + PM + "))";
	public static final String SIX_AM = "((?<=" + AM + ")(" + SIX + "|6)" + O_CLOCK + ")|((" + SIX + "|6)" + O_CLOCK + "(?=" + AM + "))";
	public static final String SIX_PM = "((?<=" + PM + ")(" + SIX + "|6)" + O_CLOCK + ")|((" + SIX + "|6)" + O_CLOCK + "(?=" + PM + "))";
	public static final String SEVEN_AM = "((?<=" + AM + ")(" + SEVEN + "|7)" + O_CLOCK + ")|((" + SEVEN + "|7)" + O_CLOCK + "(?=" + AM + "))";
	public static final String SEVEN_PM = "((?<=" + PM + ")(" + SEVEN + "|7)" + O_CLOCK + ")|((" + SEVEN + "|7)" + O_CLOCK + "(?=" + PM + "))";
	public static final String EIGHT_AM = "((?<=" + AM + ")(" + EIGHT + "|8)" + O_CLOCK + ")|((" + EIGHT + "|8)" + O_CLOCK + "(?=" + AM + "))";
	public static final String EIGHT_PM = "((?<=" + PM + ")(" + EIGHT + "|8)" + O_CLOCK + ")|((" + EIGHT + "|8)" + O_CLOCK + "(?=" + PM + "))";
	public static final String NINE_AM = "((?<=" + AM + ")(" + NINE + "|9)" + O_CLOCK + ")|((" + NINE + "|9)" + O_CLOCK + "(?=" + AM + "))";
	public static final String NINE_PM = "((?<=" + PM + ")(" + NINE + "|9)" + O_CLOCK + ")|((" + NINE + "|9)" + O_CLOCK + "(?=" + PM + "))";
	public static final String TEN_AM = "((?<=" + AM + ")(" + TEN + "|10)" + O_CLOCK + ")|((" + TEN + "|10)" + O_CLOCK + "(?=" + AM + "))";
	public static final String TEN_PM = "((?<=" + PM + ")(" + TEN + "|10)" + O_CLOCK + ")|((" + TEN + "|10)" + O_CLOCK + "(?=" + PM + "))";
	public static final String ELEVEN_AM = "((?<=" + AM + ")(" + ELEVEN + "|11)" + O_CLOCK + ")|((" + ELEVEN + "|11)" + O_CLOCK + "(?=" + AM + "))";
	public static final String ELEVEN_PM = "((?<=" + PM + ")(" + ELEVEN + "|11)" + O_CLOCK + ")|((" + ELEVEN + "|11)" + O_CLOCK + "(?=" + PM + "))";
	public static final String TWELVE_AM = "((?<=" + AM + ")(" + TWELVE + "|12)" + O_CLOCK + ")|((" + TWELVE + "|12)" + O_CLOCK + "(?=" + AM + "))";
	public static final String TWELVE_PM = "((?<=" + PM + ")(" + TWELVE + "|12)" + O_CLOCK + ")|((" + TWELVE + "|12)" + O_CLOCK + "(?=" + PM + "))";

	public static final String[] chineseConstants = {TWELVE_AM, TWELVE_PM,
													ELEVEN_AM, ELEVEN_PM,
													TEN_AM, TEN_PM,
													ONE_AM, ONE_PM,
													TWO_AM, TWO_PM,
													THREE_AM, THREE_PM,
													FOUR_AM, FOUR_PM,
													FIVE_AM, FIVE_PM,
													SIX_AM, SIX_PM,
													SEVEN_AM, SEVEN_PM,
													EIGHT_AM, EIGHT_PM,
													NINE_AM, NINE_PM,
													FROM, TO,
													TONIGHT, MORNING, AFTER_NOON, BEFORE_NOON,
													YESTERDAY, TODAY, TOMORROW,
													FOLLOWING_DAY, PREVIOUS_DAY, UPCOMING_DAY,
													FOLLOWING_MONTH, PREVIOUS_MONTH, UPCOMING_MONTH, LAST_MONTH,
													FOLLOWING_YEAR, PREVIOUS_YEAR, UPCOMING_YEAR, LAST_YEAR,
													DEC, NOV, OCT, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP,
													YEAR,
													(THREE+TEN), (THREE+ONE),
													(TWO+TEN), (TWO+ONE), (TWO+TWO), (TWO+THREE), (TWO+FOUR), (TWO+FIVE), (TWO+SIX), (TWO+SEVEN), (TWO+EIGHT), (TWO+NINE),
													(ONE+THREE), (ONE+FOUR), (ONE+FIVE), (ONE+SIX), (ONE+SEVEN), (ONE+EIGHT), (ONE+NINE), TWELVE, ELEVEN, TEN, ONE, TWO, COUNT_TWO, THREE, FOUR, FIVE, SIX,
													SEVEN, EIGHT, NINE, DATE, O_CLOCK};

	// English Replacement Constants
	public static final String ENG_O_CLOCK = " ";

	public static final String ENG_ONE = " 1 ";
	public static final String ENG_TWO = " 2 ";
	public static final String ENG_THREE = " 3 ";
	public static final String ENG_FOUR = " 4 ";
	public static final String ENG_FIVE = " 5 ";
	public static final String ENG_SIX = " 6 ";
	public static final String ENG_SEVEN = " 7 ";
	public static final String ENG_EIGHT = " 8 ";
	public static final String ENG_NINE = " 9 ";
	public static final String ENG_TEN = " 10 ";
	public static final String ENG_ELEVEN = " 11 ";
	public static final String ENG_TWELVE = " 12 ";

	public static final String ENG_FROM = " from ";
	public static final String ENG_TO = " to ";
	public static final String ENG_TONIGHT = " tonight ";
	public static final String ENG_MORNING = " morning ";
	public static final String ENG_AFTER_NOON = " ";
	public static final String ENG_BEFORE_NOON = " ";

	public static final String ENG_DAY = " day ";
	public static final String ENG_FOLLOWING_DAY = " following day ";
	public static final String ENG_PREVIOUS_DAY = " previous day ";
	public static final String ENG_UPCOMING_DAY = " next day ";
	public static final String ENG_YESTERDAY = " yesterday ";
	public static final String ENG_TODAY = " today ";
	public static final String ENG_TOMORROW = " tomorrow ";

	public static final String ENG_DATE = " ";

	public static final String ENG_MONTH = " ";
	public static final String ENG_FOLLOWING_MONTH = " following month ";
	public static final String ENG_PREVIOUS_MONTH = " previous month ";
	public static final String ENG_UPCOMING_MONTH = " next month ";
	public static final String ENG_LAST_MONTH = " last month ";
	public static final String ENG_JAN = " january ";
	public static final String ENG_FEB = " february ";
	public static final String ENG_MAR = " march ";
	public static final String ENG_APR = " april ";
	public static final String ENG_MAY = " may ";
	public static final String ENG_JUN = " june ";
	public static final String ENG_JUL = " july ";
	public static final String ENG_AUG = " august ";
	public static final String ENG_SEP = " september ";
	public static final String ENG_OCT = " october ";
	public static final String ENG_NOV = " november ";
	public static final String ENG_DEC = " december ";

	public static final String ENG_YEAR = " ";
	public static final String ENG_FOLLOWING_YEAR = " following year ";
	public static final String ENG_PREVIOUS_YEAR = " previous year ";
	public static final String ENG_UPCOMING_YEAR = " next year ";
	public static final String ENG_LAST_YEAR = " last year ";
	public static final String ENG_PM = " am ";
	public static final String ENG_AM = " pm ";
	public static final String ENG_ONE_AM = " 1am ";
	public static final String ENG_ONE_PM = " 1pm ";
	public static final String ENG_TWO_AM = " 2am ";
	public static final String ENG_TWO_PM = " 2pm ";
	public static final String ENG_THREE_AM = " 3am ";
	public static final String ENG_THREE_PM = " 3pm ";
	public static final String ENG_FOUR_AM = " 4am ";
	public static final String ENG_FOUR_PM = " 4pm ";
	public static final String ENG_FIVE_AM = " 5am ";
	public static final String ENG_FIVE_PM = " 5pm ";
	public static final String ENG_SIX_AM = " 6am ";
	public static final String ENG_SIX_PM = " 6pm ";
	public static final String ENG_SEVEN_AM = " 7am ";
	public static final String ENG_SEVEN_PM = " 7pm ";
	public static final String ENG_EIGHT_AM = " 8am ";
	public static final String ENG_EIGHT_PM = " 8pm ";
	public static final String ENG_NINE_AM = " 9am ";
	public static final String ENG_NINE_PM = " 9pm ";
	public static final String ENG_TEN_AM = " 10am ";
	public static final String ENG_TEN_PM = " 10pm ";
	public static final String ENG_ELEVEN_AM = " 11am ";
	public static final String ENG_ELEVEN_PM = " 11pm";
	public static final String ENG_TWELVE_AM = " 12am ";
	public static final String ENG_TWELVE_PM = " 12pm ";

	public static final String[] englishConstants = {ENG_TWELVE_AM, ENG_TWELVE_PM,
													ENG_ELEVEN_AM, ENG_ELEVEN_PM,
													ENG_TEN_AM, ENG_TEN_PM,
													ENG_ONE_AM, ENG_ONE_PM,
													ENG_TWO_AM, ENG_TWO_PM,
													ENG_THREE_AM, ENG_THREE_PM,
													ENG_FOUR_AM, ENG_FOUR_PM,
													ENG_FIVE_AM, ENG_FIVE_PM,
													ENG_SIX_AM, ENG_SIX_PM,
													ENG_SEVEN_AM, ENG_SEVEN_PM,
													ENG_EIGHT_AM, ENG_EIGHT_PM,
													ENG_NINE_AM, ENG_NINE_PM,
													ENG_FROM, ENG_TO,
													ENG_TONIGHT, ENG_MORNING, ENG_AFTER_NOON, ENG_BEFORE_NOON,
													ENG_YESTERDAY, ENG_TODAY, ENG_TOMORROW,
													ENG_FOLLOWING_DAY, ENG_PREVIOUS_DAY, ENG_UPCOMING_DAY,
													ENG_FOLLOWING_MONTH, ENG_PREVIOUS_MONTH, ENG_UPCOMING_MONTH, ENG_LAST_MONTH,
													ENG_FOLLOWING_YEAR, ENG_PREVIOUS_YEAR, ENG_UPCOMING_YEAR, ENG_LAST_YEAR,
													ENG_DEC, ENG_NOV, ENG_OCT, ENG_JAN, ENG_FEB, ENG_MAR, ENG_APR, ENG_MAY, ENG_JUN, ENG_JUL, ENG_AUG, ENG_SEP,
													ENG_YEAR,
													" 30 ", " 31 ",
													" 20 ", " 21 ", " 22 ", " 23 ", " 24 ", " 25 ", " 26 ", " 27 ", " 28 ", " 29 ",
													" 13 ", " 14 ", " 15 ", " 16 ", " 17 ", " 18 ", " 19 ",
													ENG_TWELVE, ENG_ELEVEN, ENG_TEN, ENG_ONE, ENG_TWO, ENG_TWO, ENG_THREE, ENG_FOUR, ENG_FIVE, ENG_SIX,
													ENG_SEVEN, ENG_EIGHT, ENG_NINE, ENG_DATE, ENG_O_CLOCK};


	/**
	 * Convert Chinese Unicode to English.
	 *
	 * @param input		the user input string
	 * @return 			the converted English string
	 */
	public static String convertChineseToEnglishUnicode(String input) {
		for (int i = 0; i < chineseConstants.length; i++) {
			Pattern pat = Pattern.compile("\\s*" + chineseConstants[i] + "\\s*", Pattern.UNICODE_CHARACTER_CLASS);
			Matcher mat = pat.matcher(input);
			if (mat.find()) {
				input = input.replaceAll(mat.group(), englishConstants[i]);
			}
		}
		return input;
	}

	/**
	 * Checks string contains characters within Unicode range of Chinese characters
	 *
	 * @param input		the user input string
	 * @return 				true, if is Chinese string
	 */
	public static boolean isChineseString(String input) {
		if(input.matches("^[\u4E00-\u62FF\u6300-\u77FF\u7800-\u8CFF\u8D00-\u9FFF\\p{IsDigit}]+")) {
			return true;
		}
		return false;
	}
}
```
###### main\parser\Parser.java
``` java
 */
public class Parser {
	private static Parser parser = null;
	private static PrettyTimeParser ptParser = null;
	private static final Logger logger = Logger.getLogger(Parser.class.getName());

	// String arrays containing the command keywords/aliases
	private final String[] updateCmdArr = {"update", "/u", "edit", "/e", "modify", "/m", "change"};
	private final String[] deleteCmdArr = {"delete", "del", "/d", "remove", "/r", "clear"};
	private final String[] doneCmdArr = {"done", "complete"};
	private final String[] undoneCmdArr = {"!done", "undone", "incomplete"};
	private final String[] undoCmdArr = {"undo", "back"};
	private final String[] redoCmdArr = {"redo", "forward"};
	private final String[] exitCmdArr = {"exit", "quit", "/q"};
	private final String[] displayCmdArr = {"display", "show", "/sh", "view", "/v"};

	// Regular expressions of the various command types and categories
	private final String ALL_REGEX = "\\s*all\\s*";
	private final String EXPIRED_REGEX = "\\s*(and|but)?\\s*expire(?:d)?\\s*";
	private final String NONEXPIRED_REGEX = "\\s*(and|but)?\\s*(not |un|non-|!)expire(?:d)?\\s*";
	private final String COMPLETED_REGEX = "\\s*(and|but)?\\s*(complete(?:d)?|done)\\s*";
	private final String UNCOMPLETED_REGEX = "\\s*(and|but)?\\s*((not |un|in|non-|!)complete(?:d)?|(not |un|!)done)\\s*";

	private final String[] categoriesRegex = {ALL_REGEX, EXPIRED_REGEX, NONEXPIRED_REGEX, COMPLETED_REGEX, UNCOMPLETED_REGEX};
	private final CATEGORY[] categoriesArr = {CATEGORY.ALL, CATEGORY.EXPIRED, CATEGORY.NONEXPIRED, CATEGORY.COMPLETED, CATEGORY.UNCOMPLETED};

	private final String UPDATE_REGEX = "^\\d+\\s+\\d+\\s+[-A-Za-z0-9~@#$^&*()+=_!`;'/><\\[\\]{}|\\\\,.?: ]*";
	private final String DISPLAY_REGEX = "^(\\w|\\d|\\s|!|-|,|to|between)+";
	private final String UNDO_REDO_REGEX = "^\\d+\\s*$";
	private final String DELETE_REGEX = "^(\\d+\\s*((((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)))|"
										+ "(" + ALL_REGEX + "|" + EXPIRED_REGEX + "|" + NONEXPIRED_REGEX + "|"
										+ COMPLETED_REGEX + "|" + UNCOMPLETED_REGEX + ")+";
	private final String DONE_UNDONE_REGEX = "^(\\d+\\s*((((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)))|"
											+ "(" + ALL_REGEX + "|" + EXPIRED_REGEX + "|" + NONEXPIRED_REGEX + "|"
											+ COMPLETED_REGEX + "|" + UNCOMPLETED_REGEX + ")+";

	private final String TIME_REGEX = "(((\\d+\\s+(minutes|min|seconds|sec|hrs|h|hours))|[0-9](am|pm|a.m.|p.m.)?|1[0-2](am|pm|a.m.|p.m.)?)|"
											+ "(0[0-9]|1[0-9]|2[0-3])\\:?([0-5][0-9]))\\:?([0-5][0-9])?(am|pm|a.m.|p.m.|h|hrs|hours)?";

	/**
	 * Instantiates a new parser and sets up the logger.
	 */
	private Parser() {
		ptParser = new PrettyTimeParser();
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	/**
	 * Gets the single instance of Parser.
	 *
	 * @return single instance of Parser
	 */
	public static Parser getInstance() {
		if (parser == null) {
			parser = new Parser();
		}
		return parser;
	}

	/**
	 * Determine command type.
	 *
	 * @param input		the user input string
	 * @return 			the command type
	 */
	public COMMAND_TYPE determineCommandType(String input) {
		if (input.trim().isEmpty()) {
			return COMMAND_TYPE.INVALID;
		}

		if (isCommand(input, updateCmdArr)) {
			if (hasValidParameters(removeCommandWord(input, updateCmdArr), UPDATE_REGEX)) {
				return COMMAND_TYPE.UPDATE;
			}
		} else if (isCommand(input, deleteCmdArr)) {
			if (hasValidParameters(removeCommandWord(input, deleteCmdArr), DELETE_REGEX)) {
				return COMMAND_TYPE.DELETE;
			}
		} else if (isCommand(input, displayCmdArr)) {
			input = removeCommandWord(input, displayCmdArr);
			if (hasValidParameters(input, DISPLAY_REGEX) || input.trim().isEmpty()) {
				return COMMAND_TYPE.DISPLAY;
			}
		} else if (isCommand(input, doneCmdArr)) {
			if (hasValidParameters(removeCommandWord(input, doneCmdArr), DONE_UNDONE_REGEX)) {
				return COMMAND_TYPE.DONE;
			}
		} else if (isCommand(input, undoneCmdArr)) {
			if (hasValidParameters(removeCommandWord(input, undoneCmdArr), DONE_UNDONE_REGEX)) {
				return COMMAND_TYPE.UNDONE;
			}
		} else if (isCommand(input, undoCmdArr)) {
			input = removeCommandWord(input, undoCmdArr);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.trim().isEmpty()) {
				return COMMAND_TYPE.UNDO;
			}
		} else if (isCommand(input, redoCmdArr)) {
			input = removeCommandWord(input, redoCmdArr);
			if (hasValidParameters(input, UNDO_REDO_REGEX) || input.trim().isEmpty()) {
				return COMMAND_TYPE.REDO;
			}
		} else if (isCommand(input, exitCmdArr)) {
			if (removeCommandWord(input, exitCmdArr).isEmpty()) {
				return COMMAND_TYPE.EXIT;
			}
		} else {
			return COMMAND_TYPE.ADD;
		}

		return COMMAND_TYPE.INVALID;
	}

	/**
	 * Checks if command is valid.
	 *
	 * @param input			the user input string
	 * @param commandList	the command array containing keywords
	 * @return 				true, if is valid command
	 */
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

	/**
	 * Checks for valid parameters.
	 *
	 * @param input		the user input string
	 * @param regex		the regex to match
	 * @return 			true, if parameters are valid
	 */
	private boolean hasValidParameters(String input, String regex) {
		Pattern pattern = Pattern.compile("(?ui)" + regex);
		Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
        	return true;
        }
		return false;
	}

	/**
	 * Removes the command keyword.
	 *
	 * @param input			the user input string
	 * @param commandList	the command array containing keywords
	 * @return 				the string without command keyword
	 */
	private String removeCommandWord(String input, String[] commandList) {
		for(int i = 0; i < commandList.length; i++) {
			Pattern pattern = Pattern.compile("(?ui)^" + commandList[i] + "\\s*");
			Matcher matcher = pattern.matcher(input);
			if (matcher.find()) {
				return input.replaceAll("(?ui)^" + commandList[i] + "\\s*", "");
			}
		}
		return null;
	}

	/**
	 * Format date.
	 *
	 * @param d			the date to format
	 * @param format	the date format
	 * @return 			the string of the formatted date
	 */
	public String formatDate(Date d, String format) {
		return new SimpleDateFormat(format).format(d);
	}

	/**
	 * Removes the task description from input.
	 *
	 * @param input		the user input string
	 * @return 			the string without task description
	 */
	private String removeTaskDescriptionFromInput(String input) {
		String[] taskDescWords;

		// Checks if input contains Chinese characters and converts to English if true
		if(ChiToEngConverter.isChineseString(input)) {
			input = ChiToEngConverter.convertChineseToEnglishUnicode(input);
			taskDescWords = getTaskDesc(input).split("\\s+");
			for (String word : taskDescWords) {
				input = input.replaceAll("\\s*" + word + "\\s*", " ");
			}
		} else {
			taskDescWords = getTaskDesc(input).split("\\s+");
			for (String word : taskDescWords) {
				input = input.replaceAll("\\s*" + word + "\\s+(?![0-9])", " ");
			}
		}

		// Remove any remaining 'from' keywords
		Matcher matcher = Pattern.compile("(?ui)from\\s*" + TIME_REGEX).matcher(input);
		if (matcher.find()) {
			input = input.substring(0, matcher.start()) +  input.substring(matcher.start()+5, input.length());
		}

		// Replace all 'until' with 'till' to work with PrettyTimeParser/Natty
		input = input.replaceAll("until", "till");

		return input;
	}

	/**
	 * Parses the date groups to get the correct list of parsed dates for the task.
	 *
	 * @param input		the user input string
	 * @return 			the correct list of parsed dates
	 */
	public List<Date> parseDateGroups(String input) {
		// Remove the task description from the user input for more efficient date parsing
		input = removeTaskDescriptionFromInput(input);

		List<DateGroup> dGroups = getDateGroups(input);
		List<Date> parsedDates = new ArrayList<Date>();

		// Parse the dates based on the number of dategroups returned by PrettyTimeParser/Natty
		if (dGroups != null) {
			for (int i = 0; i < dGroups.size(); i++) {
				DateGroup dGroup = dGroups.get(0);
				List<Date> dGroupDates = dGroup.getDates();
				Calendar cal = Calendar.getInstance();

				if (dGroupDates.size() == 1) {
					cal.setTime(dGroupDates.get(0));

					if (cal.compareTo(Calendar.getInstance()) == 0) {
						setDateTime(cal, -1, -1, -1, 23, 59, 0);
					}

					parsedDates.add(cal.getTime());
				} else {
					for (int j = 0; j < dGroupDates.size(); j++) {
						parsedDates.add(dGroupDates.get(j));
					}
				}
			}
		}

		return parsedDates;
	}

	/**
	 * Gets the date groups returned by PrettyTimeParser/Natty
	 *
	 * @param input		the user input string
	 * @return 			the date groups
	 */
	public List<DateGroup> getDateGroups(String input) {
		List<DateGroup> dGroup = ptParser.parseSyntax(input);
		if (!dGroup.isEmpty()) {
			return dGroup;
		} else {
			return null;
		}
	}

	/**
	 * Gets an array of words split from all dates in the date groups
	 *
	 * @param dateGroup		the date group
	 * @return 				the array of words
	 */
	private ArrayList<String> getDateGroupsWords(List<DateGroup> dateGroup) {
		ArrayList<String> words = new ArrayList<String>();
		for (int i = 0; i < dateGroup.size(); i++) {
			Pattern splitPattern = Pattern.compile("\\s");
			List<String> temp = Arrays.asList(splitPattern.split(dateGroup.get(i).getText()));

			for (int j = 0; j < temp.size(); j++) {
				if (!temp.get(j).matches("due|by|before|till|to|from|on|at")) {
					words.add(temp.get(j));
				}
			}
		}

		return words;
	}

	/**
	 * Gets the Task Description by going through multiple steps of filtering.
	 * Step 1: Remove exact string match parsed by PrettyTimeParser/Natty
	 * Step 2: Remove individual words match from array of words split from parsed date
	 * Step 3: Remove beginning and trailing keywords
	 * Step 4: Replace multiple spaces with single space
	 * Step 5: Remove beginning and trailing spaces
	 *
	 * @param input		the user input string
	 * @return 			the Task Description
	 */
	private String getTaskDesc(String input) {
		Pattern pattern = Pattern.compile("\"([^\"]*)\"");
		Matcher matcher = pattern.matcher(input);

		// If Task Description is contained within double quotes, directly extract it
		if (matcher.find()) {
			input = matcher.group().replace("\"", "");
		} else {
			// Replace all 'until' with 'till' to work with PrettyTimeParser/Natty
			input = input.replaceAll("until", "till");

			// Checks if input contains Chinese characters and converts to English if true
			if (ChiToEngConverter.isChineseString(input)) {
				input = ChiToEngConverter.convertChineseToEnglishUnicode(input);
			}

			List<DateGroup> dateGroup = ptParser.parseSyntax(input);

			// Remove all matching strings from the input using text parsed by PrettyTimeParser/Natty
			for (int i = 0; i < dateGroup.size(); i++) {
				input = input.replaceAll("(?ui)(\\s*(due on)|(due by)|due|by|before|till|to|from|on|at)?\\s+" + dateGroup.get(i).getText(), " ");
				input = input.replaceAll("(?ui)\\s*" + dateGroup.get(i).getText() + "\\s*", " ");
			}

			// Remove individual words match from array of words split from parsed date
			ArrayList<String> words = getDateGroupsWords(dateGroup);
			for (String word : words) {
				input = input.replaceAll("\\s+" + word + "\\s+", " ");
			}

			// Remove beginning and trailing keywords
			input = input.replaceAll("(?ui)\\s+((due on)|(due by)|due|by|before|till|to|from|on|at)*\\s*$", "");
			input = input.replaceAll("(?ui)^\\s*((due on)|(due by)|due|by|before|till|to|from|on|at)*\\s+", "");
		}
		// Replace multiple spaces with single space
		input = input.replaceAll("\\s+", " ");
		// Remove beginning and trailing spaces
		input = input.replaceAll("^\\s+|\\s+$", "");
		return input;
	}

	/**
	 * Gets the command parameters.
	 *
	 * @param input		the user input string
	 * @param cmdType	the command type
	 * @return 			the command parameters
	 */
	public ArrayList<String> getCommandParameters(String input, COMMAND_TYPE cmdType) {
		String pattern;
		String[] paramArray;

		switch (cmdType) {
			case DELETE:
			case DONE:
			case UNDONE:
				if (input.contains("to") || input.contains("-")) {
					pattern = "\\-+|to";
				} else {
					pattern = "\\s+|\\.+|,+|:+|;+|/+|\\\\+|\\|+";
				}
				paramArray = input.split(pattern);
				break;
			case DISPLAY:
				paramArray = input.split("(,| and | to )");
				break;
			case UPDATE:
				paramArray = input.split("\\s+", 3);
				break;
			default:
				paramArray = input.split("\\s+");
		}

		ArrayList<String> paramList = new ArrayList<String>(Arrays.asList(paramArray));

		// Remove beginning and trailing spaces
		for (int i = 0; i < paramList.size(); i++) {
			paramList.set(i, paramList.get(i).replaceAll("^\\s+|\\s+$", ""));
		}

		return paramList;
	}

	/**
	 * Creates and returns the ParsedObject for the Display command
	 *
	 * @param input		the user input string
	 * @return 			the Display command ParsedObject
	 */
	public ParsedObject getDisplayParsedObject(String input) {
		ParsedObject obj;

		input = removeCommandWord(input, displayCmdArr);
		List<Date> parsedInput = parseDateGroups(input);
		ArrayList<CATEGORY> categories = new ArrayList<CATEGORY>();

		if (parsedInput.size() == 0) {
			// Handle Display command with categories
			if (isCategoryCommand(input, categories)) {
				obj = new ParsedObject(COMMAND_TYPE.DISPLAY, PARAM_TYPE.CATEGORY, categories);
			} else {
				obj = new ParsedObject(COMMAND_TYPE.INVALID);
			}
		} else {
			ArrayList<Date> dates = new ArrayList<Date>();

			if (input.trim().isEmpty()) {
				obj = new ParsedObject(COMMAND_TYPE.INVALID);
			} else if (input.toLowerCase().contains(" to ")) {
				COMMAND_TYPE cmdType;
				// Handle Display command with date and time range
				if (input.startsWith("time")) {
					input = input.toLowerCase().replace("time", "");
					cmdType = COMMAND_TYPE.DISPLAY_BETWEEN;
				} else {
					cmdType = COMMAND_TYPE.DISPLAY_ON_BETWEEN;
				}

				// Parse and add the dates into the list
				ArrayList<String> dateStrings = getCommandParameters(input, EnumTypes.COMMAND_TYPE.DISPLAY);
				for (int i = 0; i < dateStrings.size(); i++) {
					List<DateGroup> dateGroups = getDateGroups(dateStrings.get(i));
					if (dateGroups != null && dateGroups.size() > 0) {
						if (dateGroups.get(0).getDates().size() == 1) {
							dates.add(dateGroups.get(0).getDates().get(0));
						}
					}
				}

				obj = new ParsedObject(cmdType, PARAM_TYPE.DATE, dates);
			} else {
				// Handle Display command with date only
				ArrayList<String> dateStrings = getCommandParameters(input, EnumTypes.COMMAND_TYPE.DISPLAY);
				dates.clear();

				// Parse and add the dates into the list
				for (int i = 0; i < dateStrings.size(); i++) {
					List<DateGroup> dateGroups = getDateGroups(dateStrings.get(i));
					if (dateGroups != null && dateGroups.size() > 0) {
						if (dateGroups.get(0).getDates().size() == 1) {
							dates.add(dateGroups.get(0).getDates().get(0));
						}
					}
				}
				obj = new ParsedObject(COMMAND_TYPE.DISPLAY_ON, PARAM_TYPE.DATE, dates);
			}
		}
		return obj;
	}

	/**
	 * Creates and returns the ParsedObject for the Add command
	 *
	 * @param input		the user input string
	 * @return 			the Add command ParsedObject
	 */
	public ParsedObject getAddParsedObject(String input) {
		ParsedObject obj;
		List<Date> parsedInput = parseDateGroups(input);
		ArrayList<Task> tasks = new ArrayList<Task>();

		// Determine task type based on the number of dates
		switch (parsedInput.size()) {
			case 0:
				tasks.add(new Todo(input.trim(), false));
				obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.TODO, tasks);
				break;
			case 1:
				if (input.contains(" by ") || input.contains(" due ") || input.contains(" before ")) {
					tasks.add(new Deadline(parsedInput.get(0), getTaskDesc(input), false));
					obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.DEADLINE, tasks);
				} else {
					tasks.add(new Event(parsedInput.get(0), parsedInput.get(0), getTaskDesc(input), false));
					obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.SINGLE_DATE_EVENT, tasks);
				}
				break;
			case 2:
				tasks.add(new Event(parsedInput.get(0), parsedInput.get(1), getTaskDesc(input), false));
				obj = new ParsedObject(COMMAND_TYPE.ADD, PARAM_TYPE.TASK, TASK_TYPE.DOUBLE_DATE_EVENT, tasks);
				break;
			default:
				obj = new ParsedObject(COMMAND_TYPE.INVALID);
		}

		return obj;
	}

	/**
	 * Creates and returns the ParsedObject for the Update command
	 *
	 * @param input		the user input string
	 * @return 			the Update command ParsedObject
	 */
	public ParsedObject getUpdateParsedObject(String input) {
		String params = removeCommandWord(input, updateCmdArr);
		ArrayList<String> paramsList = getCommandParameters(params, COMMAND_TYPE.UPDATE);
		ParsedObject obj = new ParsedObject(COMMAND_TYPE.UPDATE, PARAM_TYPE.STRING, paramsList);
		return obj;
	}

	/**
	 * Creates and returns the ParsedObject for the Delete command
	 *
	 * @param input		the user input string
	 * @return 			the Delete command ParsedObject
	 */
	public ParsedObject getDeleteParsedObject(String input) {
		ParsedObject obj;
		input = removeCommandWord(input, deleteCmdArr);
		ArrayList<String> params = getCommandParameters(input, COMMAND_TYPE.DELETE);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<CATEGORY> categories = new ArrayList<CATEGORY>();

		// Determine the type of Delete command
		if (isIDCommand(input, taskIDs, params)) {
			obj = new ParsedObject(COMMAND_TYPE.DELETE, PARAM_TYPE.ID, taskIDs);
		} else if (isCategoryCommand(input, categories)) {
			obj = new ParsedObject(COMMAND_TYPE.DELETE, PARAM_TYPE.CATEGORY, categories);
		} else {
			obj = new ParsedObject(COMMAND_TYPE.INVALID);
		}

		return obj;
	}

	/**
	 * Creates and returns the ParsedObject for the ChangeStatus(Done/Undone) command
	 *
	 * @param input			the user input string
	 * @param newStatus		the new status
	 * @return 				the ChangeStatus command ParsedObject
	 */
	public ParsedObject getChangeStatusParsedObject(String input, boolean newStatus) {
		ParsedObject obj;
		String[] cmdList;

		// Determine whether it is a done or undone command
		if (newStatus) {
			cmdList = doneCmdArr;
		} else {
			cmdList = undoneCmdArr;
		}

		input = removeCommandWord(input, cmdList);
		ArrayList<String> params = getCommandParameters(input, COMMAND_TYPE.DONE);
		ArrayList<Integer> taskIDs = new ArrayList<Integer>();
		ArrayList<CATEGORY> categories = new ArrayList<CATEGORY>();

		// Determine type of command
		if (isIDCommand(input, taskIDs, params)) {
			obj = new ParsedObject(newStatus ? COMMAND_TYPE.DONE : COMMAND_TYPE.UNDONE, PARAM_TYPE.ID, taskIDs);
		} else if (isCategoryCommand(input, categories)){
			obj = new ParsedObject(newStatus ? COMMAND_TYPE.DONE : COMMAND_TYPE.UNDONE, PARAM_TYPE.CATEGORY, categories);
		} else {
			obj = new ParsedObject(COMMAND_TYPE.INVALID);
		}

		return obj;
	}

	/**
	 * Creates and returns the ParsedObject for the Undo/Redo command
	 *
	 * @param input		the user input string
	 * @param cmdType	the command type
	 * @return 			the Undo/Redo command ParsedObject
	 */
	public ParsedObject getUndoRedoParsedObject(String input, COMMAND_TYPE cmdType) {
		String[] cmdList;

		// Determine whether it is an undo or redo command
		if (cmdType == COMMAND_TYPE.UNDO) {
			cmdList = undoCmdArr;
		} else {
			cmdList = redoCmdArr;
		}

		input = removeCommandWord(input, cmdList);
		ArrayList<Integer> numOfExec = new ArrayList<Integer>();

		// Execute once by default if no parameters specified by the user
		if (input.isEmpty()) {
			numOfExec.add(1);
		} else {
			numOfExec.add(parseInteger(input));
		}

		return new ParsedObject(cmdType, PARAM_TYPE.INTEGER, numOfExec);
	}

	/**
	 * Parses the integer String.
	 *
	 * @param intString		the integer String
	 * @return 				the parsed integer
	 */
	public int parseInteger(String intString) {
		try {
			return Integer.parseInt(intString);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return 0;
	}

	/**
	 * Sets the date time.
	 *
	 * @param cal		the calendar to modify
	 * @param year		the year
	 * @param month		the month
	 * @param date		the date
	 * @param hours		the hours
	 * @param minutes	the minutes
	 * @param seconds	the seconds
	 * @return 			the calendar object with new time set
	 */
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

	/**
	 * Returns the array of command keywords.
	 *
	 * @param command	the command
	 * @return 			the command keyword list
	 */
	public String[] getCommandKeywordList(String command) {
		switch (command) {
			case "update":
				return updateCmdArr;
			case "delete":
				return deleteCmdArr;
			case "done":
				return doneCmdArr;
			case "undone":
				return undoneCmdArr;
			case "undo":
				return undoCmdArr;
			case "redo":
				return redoCmdArr;
			case "exit":
				return exitCmdArr;
			case "display":
				return displayCmdArr;
			default:
				return null;
		}
	}

	/**
	 * Checks if the command has categories as parameters
	 *
	 * @param input			the user input string
	 * @param categories	the list of categories
	 * @return 			true, if it is a category command
	 */
	private boolean isCategoryCommand(String input, ArrayList<CATEGORY> categories) {
		ArrayList<String> categoriesRegexClone = new ArrayList<String>(Arrays.asList(categoriesRegex));
		ArrayList<CATEGORY> categoriesArrClone = new ArrayList<CATEGORY>(Arrays.asList(categoriesArr));

		if (areMatchedCategories(input, categories, categoriesRegexClone, categoriesArrClone)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the command has IDs as parameters
	 *
	 * @param input			the user input string
	 * @param taskIDs		the list of taskIDs
	 * @param params		the list of parameters
	 * @return 				true, if is an ID command
	 */
	private boolean isIDCommand(String input, ArrayList<Integer> taskIDs, ArrayList<String> params) {
		if (input.matches("\\d+\\s*(to|-)(\\s*|\\d+)*")) {
			// Matches ID range format
			int startID = 0, endID = 0;

			try {
				startID = parseInteger(params.get(0));
				endID = parseInteger(params.get(1));
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				return false;
			}

			for (int i = startID; i <= endID; i++) {
				taskIDs.add(i);
			}

			return true;
		} else if (input.matches("(\\d+\\s*)*")) {
			// Matches single/multiple IDs format
			for (int i = 0; i < params.size(); i++) {
				try {
					taskIDs.add(parseInteger(params.get(i)));
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Recursive method to determine which categories are selected by the user
	 *
	 * @param input			the user input string
	 * @param categories	the list selected categories
	 * @param cateRegexList	the clone of list of categories regex
	 * @param cateList		the clone of list of categories
	 * @return 				true, if successful
	 */
	private boolean areMatchedCategories(String input, List<CATEGORY> categories, ArrayList<String> cateRegexList, ArrayList<CATEGORY> cateList) {
		// If parameter is ALL, add to categories list and return true
		if (input.trim().isEmpty() || input.matches("(?ui)^" + ALL_REGEX + "$")) {
			categories.add(cateList.get(0));
			return true;
		}

		// Search for regex match for categories and add to categories list then recursively call itself until input is empty
		for (int i = 1; i < cateRegexList.size(); i++) {
			if (input.matches("(?ui)^" + cateRegexList.get(i) + ".*?")) {
				categories.add(cateList.get(i));
				input = input.replaceFirst(("(?ui)^" + cateRegexList.get(i)), "");
				cateRegexList.remove(cateRegexList.get(i));
				cateList.remove(cateList.get(i));
				if (!input.trim().isEmpty()) {
					areMatchedCategories(input, categories, cateRegexList, cateList);
				}
				break;
			}
		}

		// If no matches, return false
		if (categories.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
}
```
###### main\storage\LogFileHandler.java
``` java
 */
public class LogFileHandler {
	public static LogFileHandler lfHandler = null;
	private static final Logger logger = Logger.getLogger(LogFileHandler.class.getName());
	private static Handler fileHandler = null;

	/**
	 * Instantiates a new log file handler.
	 */
	public LogFileHandler() {
		try {
			fileHandler = new FileHandler("debug.log", true);
			logger.addHandler(fileHandler);
			logger.setLevel(Level.FINE);
			fileHandler.setFormatter(new SimpleFormatter());
		} catch (SecurityException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
		} catch (IOException e2) {
			logger.log(Level.SEVERE, e2.getMessage(), e2);
		}
	}

	/**
	 * Gets the single instance of LogFileHandler.
	 *
	 * @return single instance of LogFileHandler
	 */
	public static LogFileHandler getInstance() {
		if (lfHandler == null) {
			lfHandler = new LogFileHandler();
		}
		return lfHandler;
	}

	/**
	 * Adds the log file handler.
	 *
	 * @param l		the logger
	 */
	public void addLogFileHandler(Logger l) {
		l.addHandler(fileHandler);
	}
}
```
###### main\ui\CustomCellRenderer.java
``` java
 */
@SuppressWarnings("serial")
public class CustomCellRenderer extends JTextArea implements TableCellRenderer {
	private static final Color NON_EXPIRED_FONT_COLOR = Color.decode("0x009900");
	private static final Color EXPIRED_FONT_COLOR = Color.RED;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	assert this != null;
    	this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setMargin(new Insets(2,10,2,10));
        renderFontSizeRelativeToOS();

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
	        	setFontColour((Date)value);

	        	// Hides the value of date if both from and to dates are equal. (Single Date Events)
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

    /**
	 * Render font size relative to OS.
	 */
    private void renderFontSizeRelativeToOS() {
    	if (System.getProperty("os.name").startsWith("Mac")) {
        	this.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 11));
        } else {
        	this.setFont(new Font("Dialog UI", Font.BOLD, 13));
        }
    }

    /**
	 * Format date.
	 *
	 * @param d		the date
	 * @return 		the string
	 */
    private String formatDate(Date d) {
    	SimpleDateFormat dateFmt = new SimpleDateFormat("EEE, dd MMM yyyy");
		SimpleDateFormat dayFmt = new SimpleDateFormat("h:mm a");
		return dateFmt.format(d) + "\n" + dayFmt.format(d);
    }

    /**
	 * Sets the font colour of the date to RED if it is expired
	 * Sets the font colour of the date to GREEN if it is not expired
	 *
	 * @param d		the date to modify
	 */
    private void setFontColour(Date d) {
    	Calendar now = Calendar.getInstance();
    	Calendar dateTime = Calendar.getInstance();
    	dateTime.setTime(d);

		if (now.compareTo(dateTime) > 0) {
    		this.setForeground(EXPIRED_FONT_COLOR);
    	} else {
    		this.setForeground(NON_EXPIRED_FONT_COLOR);
    	}
    }
}
```
###### main\ui\CustomDateCellEditor.java
``` java
 */
public class CustomDateCellEditor extends DefaultCellEditor {
	private static final Logger logger = Logger.getLogger(CustomDateCellEditor.class.getName());
	private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
	private static PrettyTimeParser parser = new PrettyTimeParser();
	private final String CELL_FONT = "Segoe UI Semibold";
	private final int CELL_FONT_TYPE = Font.PLAIN;
	private final int CELL_FONT_SIZE = 12;

	/**
	 * Instantiates a new custom date cell editor and sets up the logger.
	 */
	public CustomDateCellEditor() {
		super(new JTextField());
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	@Override
	public Object getCellEditorValue() {
		String value = ((JTextField)getComponent()).getText();
		String date = parser.parse(value).get(0).toString();

		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return d;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		JTextField tf = (JTextField)getComponent();
		tf.setFont(new Font(CELL_FONT, CELL_FONT_TYPE, CELL_FONT_SIZE));
		tf.setBorder(new LineBorder(Color.BLACK));

		try {
			tf.setText(sdf.format(value));
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
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
		JTextField tf = (JTextField)getComponent();
		List<Date> dates = parser.parse(tf.getText());

		// Sets the colour of the border to RED if value is not a date
		if (dates.size() <= 0) {
			tf.setBorder(new LineBorder(Color.RED));
			return false;
		}

		return super.stopCellEditing();
	}
}
```
###### main\ui\CustomStringCellEditor.java
``` java
 */
@SuppressWarnings("serial")
public class CustomStringCellEditor extends DefaultCellEditor {
	private final String CELL_FONT = "Segoe UI Semibold";
	private final int CELL_FONT_TYPE = Font.PLAIN;
	private final int CELL_FONT_SIZE = 12;
	private final Color BORDER_COLOUR = Color.BLACK;
	private final Color BACKGROUND_COLOUR = Color.YELLOW;

	/**
	 * Instantiates a new custom string cell editor.
	 */
	public CustomStringCellEditor() {
		super(new JTextField());
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		JTextField tf = (JTextField)getComponent();
		tf.setFont(new Font(CELL_FONT, CELL_FONT_TYPE, CELL_FONT_SIZE));
		tf.setBorder(new LineBorder(BORDER_COLOUR));
		tf.setText(value.toString());

		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	tf.selectAll();
            	tf.setBackground(BACKGROUND_COLOUR);
            }
        });

		return tf;
	}

	@Override
	public boolean stopCellEditing() {
		JTextField tf = (JTextField)getComponent();
		String value = tf.getText();

		// Sets the colour of the border to RED if value is empty
		if (value.trim().isEmpty()) {
			tf.setBorder(new LineBorder(Color.RED));
			return false;
		}

		return super.stopCellEditing();
	}
}
```
###### main\ui\InputFeedbackListener.java
``` java
 */
public class InputFeedbackListener implements DocumentListener {
	private JTextPane tpInput = null;
	private JTextArea taMessage = null;
	private static Parser parser = null;
	private static InputFeedbackListener feedbackListener = null;
	private static Pattern pattern = null;
	private static Matcher matcher = null;
	private static final Logger logger = Logger.getLogger(InputFeedbackListener.class.getName());

	private static final Color NORMAL_FONT_COLOUR = Color.BLACK;
	private static final Color KEYWORD_FONT_COLOUR = Color.decode("#19D14A");
	private static final Color WARNING_FONT_COLOUR = Color.decode("#FF5757");

	private static final SimpleAttributeSet defaultSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet keywordSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet warningSet = new SimpleAttributeSet();

	private static final String[] commandSyntaxPrompt = {"Command aliases: update, modify, edit, /u, /m, /e  change\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): update {taskID} {columnID} {value}",
														  "Command aliases: delete, del, remove, /d, /r\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): delete {taskID} *((to|-) {taskID})",
														  "Command aliases: display, show, view, /sh, /v (| means OR, * means OPTIONAL )\n"
														+ "Command syntax: display (expired|!expired|completed|!completed) | {date} *(to {date})",
														  "Command aliases: undo, back\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): undo *{numberOfCommands}",
														  "Command aliases: redo, forward\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): redo *{numberOfCommands}",
														  "Command aliases: exit, quit\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): exit",
														  "Command aliases: undone, !done, incomplete\n"
														+ "Command Syntax( | means OR, * means OPTIONAL ): undone {taskID} *((to|-) {taskID})",
														  "Command aliases: done, complete\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): done {taskID} *((to|-) {taskID})"};

	private static final String TIME_REGEX = "\\s*(((\\d+\\s+(minutes|min|seconds|sec|hours))|[0-9](am|pm|a.m.|p.m.)?|1[0-2](am|pm|a.m.|p.m.)?)|"
											+ "(0[0-9]|1[0-9]|2[0-3])\\:?([0-5][0-9]))\\:?([0-5][0-9])?(am|pm|a.m.|p.m.|h|\\shours)?\\s*";
	private static final String TIME_MESSAGE = "Numbers in the Task Description may be parsed as dates. "
											+ "Surround your Task Description with double quotes if you do not want it to be parsed. (e.g. \"Lunch at 18 Chefs with John\" from 12 to 1pm)";

	private static final String MONTH_REGEX = "\\s*(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|"
        									+ "Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?)\\s*";
	private static final String MONTH_MESSAGE = "Months in the Task Description may be parsed as dates. "
											+ "Surround your Task Description with double quotes if you do not want it to be parsed. (e.g. \"Lunch with April\" from 12 to 1pm)";

	private static final String DAY_REGEX = "\\s*(?:(Mon|Tue(?:s)|Wed(?:nes)|Thur(?:s)|Fri|Sat(?:ur)|Sun)(?:day)?)\\s*";
	private static final String DAY_MESSAGE = "Days in the Task Description may be parsed as dates. "
											+ "Surround your Task Description with double quotes if you do not want it to be parsed. (e.g. \"Lunch with Wednesday\" from 12 to 1pm)";

	/**
	 * Instantiates a new input feedback listener and sets up the logger.
	 */
	private InputFeedbackListener() {
		parser = Parser.getInstance();
		StyleConstants.setForeground(defaultSet, NORMAL_FONT_COLOUR);
		StyleConstants.setForeground(keywordSet, KEYWORD_FONT_COLOUR);
		StyleConstants.setForeground(warningSet, WARNING_FONT_COLOUR);
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	/**
	 * Gets the single instance of InputFeedbackListener.
	 *
	 * @return single instance of InputFeedbackListener
	 */
	public static InputFeedbackListener getInstance() {
		if (feedbackListener == null) {
			feedbackListener = new InputFeedbackListener();
		}
		return feedbackListener;
	}

	/**
	 * Setup References to the UI components.
	 *
	 * @param tpInput		reference of tpInput
	 * @param taMessage		reference of taMessage
	 */
	public void setupReferences(JTextPane tpInput, JTextArea taMessage) {
		this.tpInput = tpInput;
		this.taMessage = taMessage;
	}

	/**
	 * Highlight text of matching cases.
	 */
	public void highlightText() {
		String input = tpInput.getText();
        tpInput.getStyledDocument().setCharacterAttributes(0, input.length(), defaultSet, true);

        if (!input.isEmpty()) {
        	taMessage.setText(null);
        }

        String[][] commandArrays = getCommandArrays();

        highlightCommandKeyword(commandArrays, input);
        highlightWarningCases(input, TIME_REGEX, TIME_MESSAGE);
        highlightWarningCases(input, MONTH_REGEX, MONTH_MESSAGE);
        highlightWarningCases(input, DAY_REGEX, DAY_MESSAGE);
    }

	/**
	 * Search through the input string and highlight any keywords matching to the commands in the command arrays.
	 *
	 * @param commandArrays	the array of command arrays kept by the Parser
	 * @param input			the input
	 */
	private void highlightCommandKeyword(String[][] commandArrays, String input) {
		for (int i = 0; i < commandArrays.length; i++) {
        	for (String keyword : commandArrays[i]) {
        		pattern = Pattern.compile("(?ui)^" + keyword + "\\s+");
                matcher = pattern.matcher(input);
                while (matcher.find()) {
                	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), keyword.length(), keywordSet, true);
                	taMessage.setText(commandSyntaxPrompt[i]);
                }
        	}
        }
	}

	/**
	 * Retrieve command arrays from the Parser.
	 *
	 * @return 	array of command arrays
	 */
	private String[][] getCommandArrays() {
		return new String[][] {parser.getCommandKeywordList("update"), parser.getCommandKeywordList("delete"),
								parser.getCommandKeywordList("display"), parser.getCommandKeywordList("undo"),
								parser.getCommandKeywordList("redo"), parser.getCommandKeywordList("exit"),
								parser.getCommandKeywordList("undone"), parser.getCommandKeywordList("done")};
	}

	/**
	 * Highlight warning cases.
	 *
	 * @param input
	 *            the user input
	 * @param regex
	 *            the regex for parsing
	 * @param message
	 *            the message to display to the user
	 */
	private void highlightWarningCases(String input, String regex, String message) {
		pattern = Pattern.compile("(?ui)" + regex);
        matcher = pattern.matcher(input);
        while (matcher.find()) {
        	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.group().length(), warningSet, true);
        	taMessage.setText(message);
        }
	}

	/**
	 * Handle the change in text and invoke later to prevent clashes with other threads
	 */
    private void handleTextChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
            		highlightText();
            	} catch (Exception e) {
        			logger.log(Level.SEVERE, e.getMessage(), e);
        		}
            }
        });
    }

	@Override
	public void insertUpdate(DocumentEvent e) {
		handleTextChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		handleTextChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// Not required
	}
}
```
###### main\ui\InputHistoryHandler.java
``` java
 */
public class InputHistoryHandler {
	private static InputHistoryHandler inputHistory = null;
	private static ArrayList<String> history = null;
	private static int pointer;

	private InputHistoryHandler() { }

	/**
	 * Gets the single instance of InputHistoryHandler.
	 *
	 * @return 	single instance of InputHistoryHandler
	 */
	public static InputHistoryHandler getInstance() {
		if (inputHistory == null) {
			inputHistory = new InputHistoryHandler();
			history = new ArrayList<String>();
			pointer = 0;
		}
		return inputHistory;
	}

	/**
	 * Saves the current input and shifts the pointer to prepare for the next input.
	 *
	 * @param input		the current input of the user
	 */
	public void saveInputHistory(String input) {
		if (!input.trim().isEmpty()) {
			history.add(input);
			pointer = history.size();
		}
	}

	/**
	 * Shifts the pointer backward by 1 index if possible and return the current value which
	 * contains the previous user input.
	 *
	 * @return 	the previous input of the user
	 */
	public String getPreviousInput() {
		if (history.size() > 0 && pointer > 0) {
			pointer -= 1;
			return history.get(pointer);
		}
		return null;
	}

	/**
	 * Shifts the pointer forward by 1 index if possible and return the current value which
	 * contains the next user input.
	 *
	 * @return 	the next input of the user
	 */
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
	private JScrollPane eventsScrollPane, todosScrollPane, deadlinesScrollPane;
	private TableRowSorter<?> eventsSorter, todosSorter, deadlinesSorter;
	private JTextArea taStatusMessage;

	private static final Logger logger = Logger.getLogger(MainGUI.class.getName());
	private static HelpList helpList = null;
	private static InputHistoryHandler history = null;
	private static InputFeedbackListener feedback = null;
	private static EventsTableModel etm = null;
	private static TodosTableModel ttm = null;
	private static DeadlinesTableModel dtm = null;

	private static final int FRAME_WIDTH = 768;
	private static final int FRAME_HEIGHT = 640;
	private static final int FRAME_X_LOC = 0;
	private static final int FRAME_Y_LOC = 23;
	private static final float FRAME_OPACITY = 1f;

	private static final int MINI_MODE_HEIGHT = 137;
	private static final int MINI_MODE_WIDTH = 762;
	private static final int MINI_MODE_X_LOC = 0;
	private static final int MINI_MODE_Y_LOC = 475;

	private static final int MINI_MODE_FRAME_WIDTH = 768;
	private static final int MINI_MODE_FRAME_HEIGHT = 167;
	private static final float MINI_MODE_FRAME_OPACITY = 0.9f;

	private static final int HELP_LIST_MODE_WIDTH = 270;
	private static final int HELP_LIST_MODE_HEIGHT = 600;

	private static final int HELP_LIST_MODE_FRAME_WIDTH = 1044;
	private static final int HELP_LIST_MODE_FRAME_HEIGHT = 640;

	private static final int INPUT_TEXTPANE_HEIGHT = 38;
	private static final int INPUT_TEXTPANE_WIDTH = 738;
	private static final int INPUT_TEXTPANE_X_LOC = 12;
	private static final int INPUT_TEXTPANE_Y_LOC = 87;

	private static final String FILTER_LABEL_FONT = "Dialog UI";
	private static final int FILTER_LABEL_FONT_STYLE = Font.BOLD;
	private static final int FILTER_LABEL_FONT_SIZE = 14;

	private static final String STATUS_TEXTAREA_FONT = "Dialog UI";
	private static final int STATUS_TEXTAREA_FONT_STYLE = Font.BOLD;
	private static final int STATUS_TEXTAREA_FONT_SIZE = 14;

	private static final String INPUT_TEXTPANE_FONT = "Segoe UI Semibold";
	private static final int INPUT_TEXTPANE_FONT_STYLE = Font.BOLD;
	private static final int INPUT_TEXTPANE_FONT_SIZE = 20;

	private static final int TABLE_ROW_HEIGHT = 50;

	private static final String[] themes = {"bernstein.BernsteinLookAndFeel", "noire.NoireLookAndFeel",
											"smart.SmartLookAndFeel", "mint.MintLookAndFeel", "mcwin.McWinLookAndFeel"};
	private static int themeIndex = 0;

	private static boolean isMiniMode = false;
	private static boolean isDisplayingHelpList = false;

	/**
	 * The main method.
	 *
	 * @param args	the arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainGUI = MainGUI.getInstance();
					mainGUI.sendInstanceToModels();
					mainGUI.addObserver(Logic.getInstance());
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Gets the single instance of MainGUI.
	 *
	 * @return single instance of MainGUI
	 */
	public static MainGUI getInstance() {
		if (mainGUI == null) {
			return new MainGUI();
		}
		return mainGUI;
	}

	/**
	 * Instantiates a new main GUI and sets up the logger.
	 */
	private MainGUI() {
		try {
			initialise();
			LogFileHandler.getInstance().addLogFileHandler(logger);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Initialise the main components of the GUI.
	 */
	private void initialise() {
		try {
			setupMainFrame();
			setupPanels();
			setupTextPanes();
			setupTabbedPane();
			setupTableModels();
			setupTables();
			setupTableSorters();
			setupFilterLabel();
			setupStatusMessageTextArea();
			setupInputHistoryHandler();
			setupInputFeedbackReferences();
			setupHelpList();
			setupKeyBinds();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Setup main frame.
	 */
	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setAlwaysOnTop(true);
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(FRAME_X_LOC, FRAME_Y_LOC, FRAME_WIDTH, FRAME_HEIGHT);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);
		frmTodokoro.setVisible(true);

		// Set focus to user input textpane on windowOpened event
		frmTodokoro.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				tpUserInput.requestFocusInWindow();
			}
		});
	}

	/**
	 * Setup panels.
	 */
	private void setupPanels() {
		inputPanel = new JPanel();
		inputPanel.setBounds(MINI_MODE_X_LOC, MINI_MODE_Y_LOC, MINI_MODE_WIDTH, MINI_MODE_HEIGHT);
		inputPanel.setLayout(null);
		frmTodokoro.getContentPane().add(inputPanel);
	}

	/**
	 * Setup text panes.
	 */
	private void setupTextPanes() {
		tpUserInput = new JTextPane();
		inputPanel.add(tpUserInput);
		tpUserInput.setFont(generateFont(INPUT_TEXTPANE_FONT, INPUT_TEXTPANE_FONT_STYLE, INPUT_TEXTPANE_FONT_SIZE));
		tpUserInput.setBounds(INPUT_TEXTPANE_X_LOC, INPUT_TEXTPANE_Y_LOC, INPUT_TEXTPANE_WIDTH, INPUT_TEXTPANE_HEIGHT);
		tpUserInput.setBorder(createCompoundBorder(4, 4, 0, 4));
		tpUserInput.setFocusAccelerator('e');
		tpUserInput.getDocument().addDocumentListener(feedback);

		tfFilter = new JTextField();
		tfFilter.setBounds(594, 12, 156, 26);
		frmTodokoro.getContentPane().add(tfFilter);
		tfFilter.setColumns(10);
		tfFilter.setBorder(createCompoundBorder(0, 4, 0, 4));
		tfFilter.setFocusAccelerator('f');
		tfFilter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	filterTables();
				    }
			    });
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						filterTables();
					}
				});
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						filterTables();
					}
				});
			}
		});
	}

	/**
	 * Setup tabbed pane.
	 */
	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setBounds(12, 8, 738, 465);
		eventsScrollPane = new JScrollPane();
		todosScrollPane = new JScrollPane();
		deadlinesScrollPane = new JScrollPane();
		eventsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		todosScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		deadlinesScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events (1)</b></body></html>", null, eventsScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos (2)</b></body></html>", null, todosScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Deadlines (3)</b></body></html>", null, deadlinesScrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	/**
	 * Setup table models.
	 */
	private void setupTableModels() {
		etm = EventsTableModel.getInstance();
		ttm = TodosTableModel.getInstance();
		dtm = DeadlinesTableModel.getInstance();
	}

	/**
	 * Setup tables.
	 */
	private void setupTables() {
		setupDeadlinesTable();
		setupTodosTable();
		setupEventsTable();
	}

	/**
	 * Setup table sorters.
	 */
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

	/**
	 * Setup filter label.
	 */
	private void setupFilterLabel() {
		lblFilter = new JLabel("Filter:");
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(generateFont(FILTER_LABEL_FONT, FILTER_LABEL_FONT_STYLE, FILTER_LABEL_FONT_SIZE));
		lblFilter.setBounds(530, 16, 60, 16);
		frmTodokoro.getContentPane().add(lblFilter);
	}

	/**
	 * Setup status message text area.
	 */
	private void setupStatusMessageTextArea() {
		taStatusMessage = new JTextArea(2, 20);
		taStatusMessage.setBounds(12, 4, 738, 75);
		taStatusMessage.setWrapStyleWord(true);
		taStatusMessage.setLineWrap(true);
		taStatusMessage.setOpaque(false);
		taStatusMessage.setEditable(false);
		taStatusMessage.setFocusable(false);
		taStatusMessage.setBackground(UIManager.getColor("Label.background"));
		taStatusMessage.setFont(generateFont(STATUS_TEXTAREA_FONT, STATUS_TEXTAREA_FONT_STYLE, STATUS_TEXTAREA_FONT_SIZE));
		taStatusMessage.setBorder(new TitledBorder(createCompoundBorder(0, 5, 0, 5), "Status Message", 0, 0, generateFont("Dialog UI", Font.BOLD, 14)));
		inputPanel.add(taStatusMessage);
	}

	/**
	 * Initialise input history handler.
	 */
	private void setupInputHistoryHandler() {
		history = InputHistoryHandler.getInstance();
	}

	/**
	 * Setup input feedback references.
	 */
	private void setupInputFeedbackReferences() {
		feedback = InputFeedbackListener.getInstance();
		feedback.setupReferences(tpUserInput, taStatusMessage);
	}

	/**
	 * Setup help list panel and add it to the main frame.
	 */
	private void setupHelpList() {
		helpList = new HelpList();
		helpList.setBounds(FRAME_WIDTH-10, 0, HELP_LIST_MODE_WIDTH, HELP_LIST_MODE_HEIGHT);
		helpList.setVisible(true);
		frmTodokoro.getContentPane().add(helpList);
	}

	/**
	 * Setup key binds.
	 */
	@SuppressWarnings("serial")
	private void setupKeyBinds() {
		InputMap im = frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = frmTodokoro.getRootPane().getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Toggle Help List");
		am.put("Toggle Help List", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				toggleHelpList();
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "Toggle Mini Mode");
		am.put("Toggle Mini Mode", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				toggleMiniMode();
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "Change Directory");
		am.put("Change Directory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				new ChangeDirectory(frmTodokoro);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "Cycle Tabs");
		am.put("Cycle Tabs", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cycleTabs();
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "Scroll Up");
		am.put("Scroll Up", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				scrollTable("UP");
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "Scroll Down");
		am.put("Scroll Down", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				scrollTable("DOWN");
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, InputEvent.CTRL_DOWN_MASK), "Cycle Themes");
		am.put("Cycle Themes", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cycleThemes();
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Send Command");
		tpUserInput.getActionMap().put("Send Command", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sendUserInput(tpUserInput.getText().trim());
				history.saveInputHistory(tpUserInput.getText().trim());
				tpUserInput.setText(null);
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Load Previous Input");
		tpUserInput.getActionMap().put("Load Previous Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loadInput("PREVIOUS");
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Load Next Input");
		tpUserInput.getActionMap().put("Load Next Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loadInput("NEXT");
			}
		});
	}

	/**
	 * Send instance of UI to table models.
	 */
	private void sendInstanceToModels() {
		dtm.setUIInstance(mainGUI);
		etm.setUIInstance(mainGUI);
		ttm.setUIInstance(mainGUI);
	}

	/**
	 * Load next/previous input from input history.
	 *
	 * @param position	the position of the input
	 */
	private void loadInput(String position) {
		String input = null;
		if (position.equals("NEXT")) {
			input = history.getNextInput();
		} else if (position.equals("PREVIOUS")) {
			input = history.getPreviousInput();
		}

		if (input != null) {
			tpUserInput.setText(input);
		}
	}

	/**
	 * Toggle help list.
	 */
	private void toggleHelpList() {
		if (isDisplayingHelpList) {
			tpUserInput.requestFocusInWindow();
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
		} else {
			if (isMiniMode) {
				toggleMiniMode();
			}
			helpList.getHelpListFocus();
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), HELP_LIST_MODE_FRAME_WIDTH, HELP_LIST_MODE_FRAME_HEIGHT);
		}

		isDisplayingHelpList = !isDisplayingHelpList;
	}

	/**
	 * Toggle mini-mode.
	 */
	private void toggleMiniMode() {
		if (isMiniMode) {
			tabbedPane.setVisible(true);
			lblFilter.setVisible(true);
			tfFilter.setVisible(true);
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
			frmTodokoro.setOpacity(FRAME_OPACITY);
			inputPanel.setBounds(0, 475, MINI_MODE_WIDTH, MINI_MODE_HEIGHT);
		} else {
			tabbedPane.setVisible(false);
			lblFilter.setVisible(false);
			tfFilter.setVisible(false);
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), MINI_MODE_FRAME_WIDTH, MINI_MODE_FRAME_HEIGHT);
			frmTodokoro.setOpacity(MINI_MODE_FRAME_OPACITY);
			inputPanel.setBounds(0, 0, MINI_MODE_WIDTH, MINI_MODE_HEIGHT);
			isDisplayingHelpList = false;
		}

		isMiniMode = !isMiniMode;
	}

	/**
	 * Cycle through tabs.
	 * Alternative hotkey to select tabs for Mac as Mnemonics are not supported.
	 */
	private void cycleTabs() {
		if (tabbedPane.getSelectedIndex() == 2) {
			tabbedPane.setSelectedIndex(0);
		} else {
			tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex()+1);
		}
	}

	/**
	 * Cycle through themes.
	 */
	private void cycleThemes() {
		themeIndex++;
		themeIndex = themeIndex%themes.length;

		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (InstantiationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (UnsupportedLookAndFeelException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		SwingUtilities.updateComponentTreeUI(frmTodokoro);

		// Reset row height of tables after updating components
		eventsTable.setRowHeight(TABLE_ROW_HEIGHT);
		todosTable.setRowHeight(TABLE_ROW_HEIGHT);
		deadlinesTable.setRowHeight(TABLE_ROW_HEIGHT);
	}

	/**
	 * Scroll up/down table entries.
	 *
	 * @param direction		the scrolling direction
	 */
	private void scrollTable(String direction) {
		if (direction.equals("UP")) {
			switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()+todosScrollPane.getHeight()-24);
					break;
				case 2:
					deadlinesScrollPane.getVerticalScrollBar().setValue(deadlinesScrollPane.getVerticalScrollBar().getValue()+deadlinesScrollPane.getHeight()-24);
					break;
				default:
					// Impossible case
					assert false : tabbedPane.getSelectedIndex();
			}
		} else if (direction.equals("DOWN")) {
			switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()-todosScrollPane.getHeight()-24);
					break;
				case 2:
					deadlinesScrollPane.getVerticalScrollBar().setValue(deadlinesScrollPane.getVerticalScrollBar().getValue()-deadlinesScrollPane.getHeight()-24);
					break;
				default:
					// Impossible case
					assert false : tabbedPane.getSelectedIndex();
			}
		}
	}

	/**
	 * Setup events table.
	 */
	private void setupEventsTable() {
		eventsTable = new JTable();
		eventsTable.setName("Events");
		eventsTable.setModel(etm);
		setupTableProperties(eventsTable);
		setupRenderersAndEditors(eventsTable);
		setupDimensions(eventsTable);
		eventsScrollPane.setViewportView(eventsTable);
	}

	/**
	 * Setup todos table.
	 */
	private void setupTodosTable() {
		todosTable = new JTable();
		todosTable.setName("Todos");
		todosTable.setModel(ttm);
		setupTableProperties(todosTable);
		setupRenderersAndEditors(todosTable);
		setupDimensions(todosTable);
		todosScrollPane.setViewportView(todosTable);
	}

	/**
	 * Setup deadlines table.
	 */
	private void setupDeadlinesTable() {
		deadlinesTable = new JTable();
		deadlinesTable.setName("Deadlines");
		deadlinesTable.setModel(dtm);
		setupTableProperties(deadlinesTable);
		setupRenderersAndEditors(deadlinesTable);
		setupDimensions(deadlinesTable);
		deadlinesScrollPane.setViewportView(deadlinesTable);
	}

	/**
	 * Sets the up custom renderers and editors.
	 *
	 * @param table		the tables to assign renderers and editors
	 */
	private void setupRenderersAndEditors(JTable table) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		CustomCellRenderer customRenderer = new CustomCellRenderer();
		CustomDateCellEditor customDateEditor = new CustomDateCellEditor();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		table.setDefaultRenderer(Integer.class, centerRenderer);
		table.setDefaultRenderer(String.class, customRenderer);
		table.setDefaultEditor(String.class, new CustomStringCellEditor());

		if (table.getName().equals("Events") || table.getName().equals("Deadlines")) {
			table.setDefaultRenderer(Date.class, customRenderer);
			table.setDefaultEditor(Date.class, customDateEditor);
		}
	}

	/**
	 * Filter row.
	 *
	 * @param sorter	the table sorter
	 * @param index		the index of starting column to sort
	 */
	private void filterRow(TableRowSorter<?> sorter, int index) {
		RowFilter<Object, Object> rowFilter = null;
		List<RowFilter<Object, Object>> rowFilters = new ArrayList<RowFilter<Object, Object>>();

		try {
			String searchTerms = tfFilter.getText();

			if (searchTerms.equals("done")) {
				rowFilter = RowFilter.regexFilter("^true$");
			} else if (searchTerms.equals("!done") || searchTerms.equals("not done") || searchTerms.equals("undone")) {
				rowFilter = RowFilter.regexFilter("^false$");
			} else {
				String[] termsArr = searchTerms.split(" ");

				for (int i = 0; i < termsArr.length; i++) {
					rowFilters.add(RowFilter.regexFilter("(?iu)" + termsArr[i], index, index+1));
				}

				rowFilter = RowFilter.andFilter(rowFilters);
			}
		} catch (PatternSyntaxException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		sorter.setRowFilter(rowFilter);
	}

	/**
	 * Filter tables.
	 */
	private void filterTables() {
		filterRow(eventsSorter, 3);
		filterRow(todosSorter, 1);
		filterRow(deadlinesSorter, 2);
	}

	/**
	 * Update tables.
	 *
	 * @param type				the type of task
	 * @param tasks				the list of tasks
	 * @param shouldFocusTab	the boolean to determine which tab to focus to after updating
	 */
	public void updateTables(EnumTypes.TASK_TYPE type, List<Task> tasks, boolean shouldFocusTab) {
		switch (type) {
			case EVENT:
				etm.setTasks(tasks);
				etm.fireTableDataChanged();
				if (shouldFocusTab) {
					tabbedPane.setSelectedIndex(0);
				}
				break;
			case TODO:
				ttm.setTasks(tasks);
				ttm.fireTableDataChanged();
				if (shouldFocusTab) {
					tabbedPane.setSelectedIndex(1);
				}
				break;
			case DEADLINE:
				dtm.setTasks(tasks);
				dtm.fireTableDataChanged();
				if (shouldFocusTab) {
					tabbedPane.setSelectedIndex(2);
				}
				break;
			default:
				// Impossible case
				assert false : type;
		}
	}

	/**
	 * Sets up the table dimensions.
	 *
	 * @param table		the table to specify dimensions
	 */
	private void setupDimensions(JTable table) {
		table.setRowHeight(TABLE_ROW_HEIGHT);
		table.getColumnModel().getColumn(0).setMaxWidth(45);

		switch (table.getName()) {
		case "Events":
			table.getColumnModel().getColumn(1).setMinWidth(132);
			table.getColumnModel().getColumn(1).setMaxWidth(132);
			table.getColumnModel().getColumn(2).setMinWidth(132);
			table.getColumnModel().getColumn(2).setMaxWidth(132);
			table.getColumnModel().getColumn(3).setMinWidth(377);
			table.getColumnModel().getColumn(3).setMaxWidth(682);
			table.getColumnModel().getColumn(4).setMaxWidth(50);
			break;
		case "Todos":
			table.getColumnModel().getColumn(1).setMinWidth(641);
			table.getColumnModel().getColumn(2).setMaxWidth(50);
			break;
		case "Deadlines":
			table.getColumnModel().getColumn(1).setMinWidth(132);
			table.getColumnModel().getColumn(1).setMaxWidth(132);
			table.getColumnModel().getColumn(2).setMinWidth(509);
			table.getColumnModel().getColumn(3).setMaxWidth(50);
			break;
		}
	}

	/**
	 * Sets up the table properties.
	 *
	 * @param table		the table to specify properties
	 */
	private void setupTableProperties(JTable table) {
		table.getTableHeader().setReorderingAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
	}

	/**
	 * Generate font.
	 *
	 * @param font	the font
	 * @param style	the style
	 * @param size	the size
	 * @return 		the font
	 */
	private Font generateFont(String font, int style, int size) {
		return new Font(font, style, size);
	}

	/**
	 * Create a rounded compound border.
	 *
	 * @param top		the top margin
	 * @param left		the left margin
	 * @param bottom	the bottom margin
	 * @param right		the right margin
	 * @return the compound border
	 */
	private Border createCompoundBorder(int top, int left, int bottom, int right) {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(top, left, bottom, right);
		return new CompoundBorder(rounded, empty);
	}

	/**
	 * Update status message.
	 *
	 * @param msg	the new status message
	 */
	public void updateStatusMsg(String msg) {
    	taStatusMessage.setText(msg);
	}

	/**
	 * Notify observers that input value has changed.
	 *
	 * @param command	the input command
	 */
	public void sendUserInput(String command) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_USER_INPUT_CODE, new ObserverEvent.EInput(command)));
	}

	/**
	 * Update method that is called upon receiving change notification by observable
	 */
	@Override
	public void update(Observable observable, Object event) {
		ObserverEvent OEvent = (ObserverEvent) event;

		if (OEvent.getCode() == ObserverEvent.CHANGE_MESSAGE_CODE) {
			ObserverEvent.EMessage eMessage = (ObserverEvent.EMessage) OEvent.getPayload();
			updateStatusMsg(eMessage.getMessage());
			return;
		}

		if (OEvent.getCode() == ObserverEvent.CHANGE_TABLE_CODE) {
			ObserverEvent.ETasks eTasks = (ObserverEvent.ETasks) OEvent.getPayload();
			updateTables(eTasks.getTaskType(), eTasks.getTasks(), eTasks.shouldSwitch());
			return;
		}
	}
}
```
###### test\MainParserTest.java
``` java
	 *  Command is deemed invalid when the string is empty regardless of any whitespaces
	 */
	@Test
	public void determineCommandTypeInvalid() {
		assertEquals(EnumTypes.COMMAND_TYPE.INVALID, testParser.determineCommandType("          "));
	}

```
###### test\MainParserTest.java
``` java
	 *  Add command is flexible as it it parsed using Natural Language Processing
	 */
	@Test
	public void determineCommandTypeAdd() {
		assertEquals(EnumTypes.COMMAND_TYPE.ADD, testParser.determineCommandType("lunch with john at 9pm tomorrow"));
		assertEquals(EnumTypes.COMMAND_TYPE.ADD, testParser.determineCommandType("lunch at 9pm tomorrow with john"));
		assertEquals(EnumTypes.COMMAND_TYPE.ADD, testParser.determineCommandType("at 9pm tomorrow lunch with john"));
	}

```
###### test\MainParserTest.java
``` java
	 *  Update command is parsed using the regex pattern ("(?i)^" + command + "\\s+\\d+\\s+\\d+")
	 *  where command is a word from the string array updateCmdList with values {"update", "/u", "edit", "/e", "modify", "/m"};
	 *	The following test is to test the boundary of the regex pattern for the Update command
	 */
	@Test
	public void testDetermineCommandTypeUpdate() {
		determineCommandTypeUpdateValid();
		determineCommandTypeUpdateInvalid();
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeUpdateValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update   1   4   testing one two three"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("/u 1  4   one  two    three"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("edit 1  4  four   five  six"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("/e 1  4  789"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("modify 1  4   abc"));
		assertEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("/m 1   4   def"));
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeUpdateInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update test 124"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("update 1 test"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.UPDATE, testParser.determineCommandType("test update"));
	}

```
###### test\MainParserTest.java
``` java
	 *  Delete command is parsed using the regex pattern ("(?i)^" + command + "\\s+\\d+\\s*(((to|-)\\s*\\d+\\s*)?|(\\d+\\s*)*)$")
	 *  where command is a word from the string array deleteCmdList with values {"delete", "del", "/d", "remove", "rm", "/r"};
	 *	The following test is to test the boundary of the regex pattern for the Delete command
	 */
	@Test
	public void testDetermineCommandTypeDelete() {
		determineCommandTypeDeleteValid();
		determineCommandTypeDeleteInvalid();
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeDeleteValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete 1   to   10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete  5  6   7   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("del 1  -    10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("del  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/d 1-10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/d  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("remove 1-10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("remove  567   "));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/r 1-10"));
		assertEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("/r  567   "));
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeDeleteInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("123 delete"));
		assertNotEquals(EnumTypes.COMMAND_TYPE.DELETE, testParser.determineCommandType("delete 1to10 1 2 3"));
	}

```
###### test\MainParserTest.java
``` java
	 *  Undo command is parsed using the regex pattern ("(?i)^" + command + "(\\s+\\d+\\s*)?$")
	 *  where command is a word from the string array undoCmdList with values {"undo", "/un"};
	 *	The following test is to test the boundary of the regex pattern for the Undo command
	 */
	@Test
	public void testDetermineCommandTypeUndo() {
		determineCommandTypeUndoValid();
		determineCommandTypeUndoInvalid();
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeUndoValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("undo"));
		assertEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("undo  567   "));
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeUndoInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.UNDO, testParser.determineCommandType("undo 235 2"));
	}

```
###### test\MainParserTest.java
``` java
	 *  Redo command is parsed using the regex pattern ("(?i)^" + command + "(\\s+\\d+\\s*)?$")
	 *  where command is a word from the string array redoCmdList with values {"redo", "/re"};
	 *	The following test is to test the boundary of the regex pattern for the Redo command
	 */
	@Test
	public void testDetermineCommandTypeRedo() {
		determineCommandTypeRedoValid();
		determineCommandTypeRedoInvalid();
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeRedoValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("redo"));
		assertEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("redo  234   "));
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeRedoInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.REDO, testParser.determineCommandType("redo 32 2"));
	}

```
###### test\MainParserTest.java
``` java
	 *  Exit command is parsed using the regex pattern ("(?i)^" + command + "\\s*$")
	 *  where command is a word from the string array exitCmdList with values {"exit", "/e", "quit", "/q"}
	 *	The following test is to test the boundary of the regex pattern for the Exit command
	 */
	@Test
	public void testDetermineCommandTypeExit() {
		determineCommandTypeExitValid();
		determineCommandTypeExitInvalid();
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeExitValid() {
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("exit"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("exit   "));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("quit"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("quit   "));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("/q"));
		assertEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("/q   "));
	}

```
###### test\MainParserTest.java
``` java
	private void determineCommandTypeExitInvalid() {
		assertNotEquals(EnumTypes.COMMAND_TYPE.EXIT, testParser.determineCommandType("exit22141"));
	}

}
```

# Hiep
###### main\logic\ChangeStatus.java
``` java
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
```
###### main\logic\Delete.java
``` java
	 *
	 * @param tasks		the tasks
	 * @return 			true, if successful
	 */
	public boolean undo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.addTask(task);
		}
		return true;
	}

	/**
	 * Redo Delete.
	 *
```
###### main\logic\Delete.java
``` java
	 *
	 * @param tasks		the tasks
	 * @return 			true, if successful
	 */
	public boolean redo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.delete(task.getTaskID());
		}
		return true;
	}

	/**
	 * Delete all tasks based on the list of task IDs.
	 *
```
###### main\logic\Display.java
``` java
 */
public class Display extends Command {

	private static Display display = null;
	private static Storage storage = null;
	private TASK_TYPE taskType;

	/**
	 * Instantiates a new display.
	 */
	private Display() {
		storage = Storage.getInstance();
	}

	/**
	 * Gets the single instance of Display.
	 *
	 * @param type		the type
	 * @return 			single instance of Display
	 */
	public static Display getInstance(TASK_TYPE type) {
		if (display == null) {
			display = new Display();
		}
		display.taskType = type;
		return display;
	}

	public TASK_TYPE getTaskType() {
		return this.taskType;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		return true;
	}

	/**
	 * Process.
	 *
	 * @param obj	the obj
	 * @return 		the list
	 */
	public List<List<Task>> process(ParsedObject obj) {
		List<List<Task>> result = new ArrayList<List<Task>>();
		List<Task> tasks = storage.getAllTask(TASK_TYPE.ALL);
		List<Task> deadlines = new ArrayList<>();
		List<Task> events = new ArrayList<>();
		List<Task> todos = new ArrayList<>();

		if (obj.getParamType() != null && obj.getParamType() == EnumTypes.PARAM_TYPE.CATEGORY) {
			displayByCategories(obj, todos, events, deadlines);
		} else {
			if (!displayByTime(obj, tasks, todos, events, deadlines)) {
				return null;
			}
		}

		result.add(deadlines);
		result.add(events);
		result.add(todos);

		return result;
	}

	/**
	 * Display by categories.
	 *
	 * @param obj		the obj
	 * @param todos		the todos
	 * @param events	the events
	 * @param deadlines	the deadlines
	 */
	public void displayByCategories(ParsedObject obj, List<Task> todos, List<Task> events, List<Task> deadlines) {
		List<Integer> ids = storage.getIdByCategory(obj.getObjects());
		for (int id : ids) {
			Task task = storage.getTaskByID(id);
			if (task != null) {
				switch (task.getType()) {
				case EVENT:
					events.add(task);
					break;
				case DEADLINE:
					deadlines.add(task);
					break;
				case TODO:
					todos.add(task);
					break;
				}

			}
		}
		message = "Tasks are displayed.";
	}

	/**
	 * Display by time.
	 *
	 * @param obj		the obj
	 * @param tasks		the tasks
	 * @param todos		the todos
	 * @param events	the events
	 * @param deadlines	the deadlines
	 * @return 			true, if successful
	 */
	public boolean displayByTime(ParsedObject obj, List<Task> tasks, List<Task> todos, List<Task> events, List<Task> deadlines) {
		Date fromDate, toDate;
		switch (obj.getCommandType()) {
		case DISPLAY_ON:
			message = "Displaying all tasks on ";
			for (int i = 0; i < obj.getObjects().size(); i++) {
				Date checkDate = (Date) obj.getObjects().get(i);
				message += new SimpleDateFormat("EEE, dd MMM yyyy").format(checkDate) + ((i < obj.getObjects().size()-1) ? ", " : "");

				for (Task task : tasks) {
					switch (task.getType()) {
					case EVENT:
						if (isOn(checkDate, ((Event) task).getFromDate())
								|| isOn(checkDate, ((Event) task).getToDate())) {
							events.add(task);
						}
						break;
					case DEADLINE:
						if (isOn(checkDate, ((Deadline) task).getDate())) {
							deadlines.add(task);
						}
						break;
					default:
						break;
					}
				}
			}
			message += ".";
			break;
		case DISPLAY_ON_BETWEEN:
			fromDate = (Date) obj.getObjects().get(0);
			toDate = (Date) obj.getObjects().get(1);
			message = "Displaying all tasks between " + new SimpleDateFormat("EEE, dd MMM yyyy").format(fromDate) + " and " + new SimpleDateFormat("EEE, dd MMM yyyy").format(toDate) + ".";
			for (Task task : tasks) {
				switch (task.getType()) {
				case EVENT:
					if (isOnBetween(fromDate, toDate, ((Event) task).getFromDate()) || isBetween(fromDate, toDate, ((Event) task).getToDate())) {
						events.add(task);
					}
					break;
				case DEADLINE:
					if (isOnBetween(fromDate, toDate, ((Deadline) task).getDate())) {
						deadlines.add(task);
					}
					break;
				default:
					break;
				}
			}
			break;
		case DISPLAY_BETWEEN:
			fromDate = (Date) obj.getObjects().get(0);
			toDate = (Date) obj.getObjects().get(1);
			message = "Displaying all tasks between " + new SimpleDateFormat("EEE, dd MMM yyyy, h:mm a").format(fromDate) + " and " + new SimpleDateFormat("EEE, dd MMM yyyy, h:mm a").format(toDate) + ".";
			for (Task task : tasks) {
				switch (task.getType()) {
				case EVENT:
					if (isBetween(fromDate, toDate, ((Event) task).getFromDate()) || isBetween(fromDate, toDate, ((Event) task).getToDate())) {
						events.add(task);
					}
					break;
				case DEADLINE:
					if (isBetween(fromDate, toDate, ((Deadline) task).getDate())) {
						deadlines.add(task);
					}
					break;
				default:
					break;
				}
			}
			break;
		case INVALID:
			message = "Invalid parameters for display command.";
			return false;
		default:
			message = "No matching tasks found.";
			return false;
		}
		todos = storage.getAllTask(TASK_TYPE.TODO);
		return true;
	}

	/**
	 * Checks if is on between.
	 *
	 * @param left	the left
	 * @param right	the right
	 * @param cur	the cur
	 * @return 		true, if is on between
	 */
	private boolean isOnBetween(Date left, Date right, Date cur) {
		Date startDate = resetTime((Date)left.clone(), true);
		Date endDate = resetTime((Date)right.clone(), false);

		if (cur.compareTo(startDate) >= 0 && cur.compareTo(endDate) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is between.
	 *
	 * @param left	the left
	 * @param right	the right
	 * @param cur	the cur
	 * @return 		true, if is between
	 */
	private boolean isBetween(Date left, Date right, Date cur) {
		return (left.compareTo(cur) <= 0 && cur.compareTo(right) <= 0);
	}

	/**
	 * Checks if is on.
	 *
	 * @param date	the date
	 * @param cur	the cur
	 * @return 		true, if is on
	 */
	private boolean isOn(Date date, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date comparedDate = resetTime((Date)date.clone(), true);

		if (currentDate.compareTo(comparedDate) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reset time.
```
###### main\logic\Update.java
``` java
	 *
	 * @param oldTask	the old task
	 * @param newTask	the new task
	 */
	private void addNewUpdateModel(Task oldTask, Task newTask) {
		vControl.addNewData(new VersionModel.UpdateModel(oldTask, newTask));
	}

	/**
	 * Undo.
	 *
```
###### main\logic\Update.java
``` java
	 *
	 * @param oldTask	the old task
	 * @return 			true, if successful
	 */
	public static boolean undo(Task oldTask) {
		return storage.updateTask(oldTask);
	}

	/**
	 * Redo.
	 *
```
###### main\logic\Update.java
``` java
	 *
	 * @param newTask	the new task
	 * @return 			true, if successful
	 */
	public static boolean redo(Task newTask) {
		return storage.updateTask(newTask);
	}
}
```
###### main\model\ObserverEvent.java
``` java
 */
public class ObserverEvent {
	public static int CHANGE_MESSAGE_CODE = 0;
	public static int CHANGE_TABLE_CODE = 1;
	public static int CHANGE_USER_INPUT_CODE = 2;

	private int code;
	private Object payload;

	/**
	 * Instantiates a new observer event.
	 *
	 * @param code
	 *            the code
	 * @param payload
	 *            the payload
	 */
	public ObserverEvent(int code, Object payload) {
		this.code = code;
		this.payload = payload;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Gets the payload.
	 *
	 * @return the payload
	 */
	public Object getPayload() {
		return this.payload;
	}

	/**
	 * The Class EInput.
	 *
```
###### main\model\ObserverEvent.java
``` java
	 */
	public static class EInput {
		private String command;

		/**
		 * Instantiates a new e input.
		 *
		 * @param command
		 *            the command
		 */
		public EInput(String command) {
			this.command = command;
		}

		/**
		 * Gets the command.
		 *
		 * @return the command
		 */
		public String getCommand() {
			return this.command;
		}
	}

	/**
	 * The Class EMessage.
	 *
```
###### main\model\ObserverEvent.java
``` java
	 */
	public static class EMessage {
		private String message;

		/**
		 * Instantiates a new e message.
		 *
		 * @param message
		 *            the message
		 */
		public EMessage(String message) {
			this.message = message;
		}

		/**
		 * Gets the message.
		 *
		 * @return the message
		 */
		public String getMessage() {
			return this.message;
		}
	}

	/**
	 * The Class ETasks.
	 *
```
###### main\model\ObserverEvent.java
``` java
	 */
	public static class ETasks {
		private List<Task> tasks = new ArrayList<>();
		private TASK_TYPE taskType;
		private boolean shouldSwitch = false;

		/**
		 * Instantiates a new e tasks.
		 *
		 * @param tasks
		 *            the tasks
		 * @param taskType
		 *            the task type
		 * @param shouldSwitch
		 *            the should switch
		 */
		public ETasks(List<Task> tasks, TASK_TYPE taskType, boolean shouldSwitch) {
			this.taskType = taskType;
			this.tasks = tasks;
			this.shouldSwitch = shouldSwitch;
		}

		/**
		 * Gets the tasks.
		 *
		 * @return the tasks
		 */
		public List<Task> getTasks() {
			return this.tasks;
		}

		/**
		 * Gets the task type.
		 *
		 * @return the task type
		 */
		public TASK_TYPE getTaskType() {
			return this.taskType;
		}

		/**
		 * Should switch.
		 *
		 * @return true, if successful
		 */
		public boolean shouldSwitch() {
			return this.shouldSwitch;
		}
	}
}
```
###### main\model\VersionModel.java
``` java
 */
public abstract class VersionModel {
	private final COMMAND_TYPE cmdType;

	/**
	 * Gets the cmd type.
	 *
	 * @return the cmd type
	 */
	public COMMAND_TYPE getCmdType() {
		return this.cmdType;
	}

	/**
	 * Instantiates a new version model.
	 *
	 * @param cmdType
	 *            the cmd type
	 */
	public VersionModel(COMMAND_TYPE cmdType) {
		this.cmdType = cmdType;
	}

	/**
	 * The Class AddModel.
	 *
```
###### main\model\VersionModel.java
``` java
	 */
	public static class AddModel extends VersionModel{
		private Task task = null;

		/**
		 * Instantiates a new adds the model.
		 *
		 * @param task
		 *            the task
		 */
		public AddModel(Task task) {
			super(COMMAND_TYPE.ADD);
			this.task = task;
		}

		/**
		 * Gets the task.
		 *
		 * @return the task
		 */
		public Task getTask() {
			return this.task;
		}
	}

	/**
	 * The Class DeleteModel.
	 *
```
###### main\model\VersionModel.java
``` java
	 */
	public static class DeleteModel extends VersionModel {

		private List<Task> tasks = new ArrayList<>();

		/**
		 * Instantiates a new delete model.
		 *
		 * @param tasks
		 *            the tasks
		 */
		public DeleteModel(List<Task> tasks) {
			super(COMMAND_TYPE.DELETE);
			this.tasks = tasks;
		}

		/**
		 * Gets the tasks.
		 *
		 * @return the tasks
		 */
		public List<Task> getTasks() {
			return this.tasks;
		}

	}

	/**
	 * The Class UpdateModel.
	 *
```
###### main\model\VersionModel.java
``` java
	 */
	public static class UpdateModel extends VersionModel {

		private Task oldTask = null, newTask = null;

		/**
		 * Instantiates a new update model.
		 *
		 * @param oldTask
		 *            the old task
		 * @param newTask
		 *            the new task
		 */
		public UpdateModel(Task oldTask, Task newTask) {
			super(COMMAND_TYPE.UPDATE);
			this.oldTask = oldTask;
			this.newTask = newTask;
		}

		/**
		 * Gets the old task.
		 *
		 * @return the old task
		 */
		public Task getOldTask() {
			return this.oldTask;
		}

		/**
		 * Gets the new task.
		 *
		 * @return the new task
		 */
		public Task getNewTask() {
			return this.newTask;
		}
	}

	/**
	 * The Class ChangeStatusModel.
	 *
```
###### main\model\VersionModel.java
``` java
	 */
	public static class ChangeStatusModel extends VersionModel {

		private List<Integer> ids = new ArrayList<>();
		private List<Boolean> oldStatuses = new ArrayList<>();
		private boolean newStatus = true;

		/**
		 * Instantiates a new change status model.
		 *
		 * @param ids
		 *            the ids
		 * @param oldStatuses
		 *            the old statuses
		 * @param newStatus
		 *            the new status
		 */
		public ChangeStatusModel(List<Integer> ids, List<Boolean> oldStatuses, boolean newStatus) {
			super(COMMAND_TYPE.DONE_UNDONE);
			this.ids = ids;
			this.oldStatuses = oldStatuses;
			this.newStatus = newStatus;
		}

		/**
		 * Gets the ids.
		 *
		 * @return the ids
		 */
		public List<Integer> getIds() {
			return this.ids;
		}

		/**
		 * Gets the old statuses.
		 *
		 * @return the old statuses
		 */
		public List<Boolean> getOldStatuses() {
			return this.oldStatuses;
		}

		/**
		 * Gets the new status.
		 *
		 * @return the new status
		 */
		public boolean getNewStatus() {
			return this.newStatus;
		}
	}

}
```
###### main\storage\DataParser.java
``` java
public class DataParser {
	
	private static volatile Gson gson = null;
	
	public static Gson getGson() {
        if (gson == null) {
            synchronized (DataParser.class) {
                if (gson == null) {
                	gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                }
            }
        }
        return gson;
	}
	
	/**
	 *
	 * @return Type of the task type "type"
	 */
	public static Type getListType(TASK_TYPE type) {
		switch (type) {
			case DEADLINE: return new TypeToken<List<Deadline>>() {}.getType();
			case EVENT: return new TypeToken<List<Event>>() {}.getType();
			case TODO: return new TypeToken<List<Todo>>() {}.getType();
			default: return null;
		}
	}
	/**
	 * 
	 * @param tasks
	 * @param type
	 * @return convert list tasks of type "type" into String of Json format 
	 */
	public static String serialize(List<Task> tasks, TASK_TYPE type) {
		Gson gson = getGson();
		Type listType = getListType(type);
		if (type == null) {
			return "[]";
		}
		return gson.toJson(tasks, listType);
	}
	
	/**
	 * 
	 * @param data
	 * @param type
	 * @return list of tasks from String "data" of Json format 
	 */
	public static List<Task> deserialize(String data, TASK_TYPE type) {
		Gson gson = getGson();
		Type listType = getListType(type);
		if (data.isEmpty()) data = "[]";
		return gson.fromJson(data, listType);
	}
}
```
###### main\storage\DirectoryHandler.java
``` java
public class DirectoryHandler {
	
	/**
	 * 
	 * @param dir is directory needs to be fixed
	 * @return UNIX format of directory of "dir"
	 */
	public static String fixDir(String dir) {
		return dir.replace("\\", "/");
	}
	
	/**
	 * get current working directory of the app 
	 * @return
	 */
	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}
}
```
###### main\storage\FileHandler.java
``` java
public class FileHandler {

	public static void createNewFolderIfNotExisit(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				// error occurs
			}
		}
	}

	public static void createNewFileIfNotExisit(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// error occurs
			}
		}
	}

	public static String readFromFile(String fileName) {

		StringBuilder result = new StringBuilder();
		BufferedReader fr = null;

		try {
			fr = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
			String line;
			while ((line = fr.readLine()) != null)
				result.append(line);
		} catch (Exception e){
			// error occurs
		} finally {
			try {
				fr.close();
			} catch (Exception e) {
				// error occurs
			}
		}
		return result.toString();
	}

	public static void writeToFile(String fileName, String data) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
			bw.write(data);
		} catch (IOException e) {
			// error occurs
		} finally {
			try {
				bw.close();
			} catch (IOException e){
				// error occurs
			}
		}
	}
}
```
###### main\storage\Storage.java
``` java
public class Storage {

	// file name and folder name for storing data
	private static String TODO_FILE = "todo.txt";
	private static String EVENT_FILE = "event.txt";
	private static String DEADLINE_FILE = "deadline.txt";
	private static String DATA_FOLDER = "data";

	// file name for storing 
	private String configFile = "config.txt";
	private String storeDir;
	private String todoFile;
	private String deadlineFile;
	private String eventFile;

	// Lists for storing tasks during the runtime
	private List<Task> todos = new ArrayList<>();
	private List<Task> events = new ArrayList<>();
	private List<Task> deadlines = new ArrayList<>();

	private static Storage storage = null;

	private Storage() {
		init();
	}

	public static Storage getInstance() {
		if (storage == null) {
			storage = new Storage();
		}
		return storage;
	}

	private void initTasks() {
		todos = DataParser.deserialize(FileHandler.readFromFile(todoFile), TODO);
		events = DataParser.deserialize(FileHandler.readFromFile(eventFile), EVENT);
		deadlines = DataParser.deserialize(FileHandler.readFromFile(deadlineFile), DEADLINE);
	}

	/*
	 * reset task ids to begin with 0
	 */
	private void initTaskId() {
		int cnt = 0;
		for (Task todo: events) {
			todo.setTaskId(++cnt);
		}
		for (Task todo: todos) {
			todo.setTaskId(++cnt);
		}
		for (Task todo: deadlines) {
			todo.setTaskId(++cnt);
		}
		//int curMaxId = Math.max(getMaxId(todos), Math.max(getMaxId(events), getMaxId(deadlines)));
		Task.setNextId(cnt);
	}

	private void initStoreDir(String storeDir) {
		if (storeDir.equals("")) storeDir = DirectoryHandler.getCurrentDir();
		storeDir = DirectoryHandler.fixDir(storeDir);

		this.storeDir = storeDir;
		FileHandler.createNewFolderIfNotExisit(storeDir + "/" + DATA_FOLDER);

		todoFile = storeDir + "/" + DATA_FOLDER + "/" + TODO_FILE;
		eventFile = storeDir + "/" + DATA_FOLDER + "/" + EVENT_FILE;
		deadlineFile = storeDir + "/" + DATA_FOLDER + "/" + DEADLINE_FILE;

		FileHandler.createNewFileIfNotExisit(todoFile);
		FileHandler.createNewFileIfNotExisit(deadlineFile);
		FileHandler.createNewFileIfNotExisit(eventFile);

		FileHandler.writeToFile(configFile, storeDir);
	}

	public void init() {
		initStoreDir(FileHandler.readFromFile(configFile));
		initTasks();
		initTaskId();
	}

	public boolean addTask(Task task) {
		task = task.clone();
		switch (task.getType()) {
			case EVENT:
				events.add(task);
				return true;
			case TODO:
				todos.add(task);
				return true;
			case DEADLINE:
				deadlines.add(task);
				return true;
			default:
				return false;
		}
	}

	public boolean updateTask(Task task) {
		if (delete(task.getTaskID())) {
			return addTask(task);
		}
		return false;
	}

	public boolean changeStatus(int id, boolean newStatus) {
		Task task = getRealTaskById(id);
		if (task != null) {
			task.setDone(newStatus);
			return true;
		}
		return false;
	}

	private List<Task> cloneList(List<Task> tasks) {
		List<Task> cloneTasks = new ArrayList<>();
		for (Task task : tasks) {
			cloneTasks.add(task.clone());
		}
		return cloneTasks;
	}

	public List<Task> getAllTask(TASK_TYPE type) {
		switch (type) {
			case TODO: return cloneList(todos);
			case EVENT: return cloneList(events);
			case DEADLINE: return cloneList(deadlines);
			case ALL:
				List<Task> allTasks = new ArrayList<Task>();
				for (Task t : cloneList(todos)) {
					allTasks.add(t);
				}
				for (Task t : cloneList(events)) {
					allTasks.add(t);
				}
				for (Task t : cloneList(deadlines)) {
					allTasks.add(t);
				}
				return allTasks;
			default: return new ArrayList<>();
		}
	}

	public Task getTaskByID(int id) {
		Task task = getRealTaskById(id);
		if (task != null) {
			return task.clone();
		}
		return null;
	}

	/**
	 * 
	 * @param categories
	 * @return all task which satisfies all category in list categories
	 */
	public List<Integer> getIdByCategory(List<CATEGORY> categories) {
		List<Integer> ids = new ArrayList<>();
		for (Task todo: todos) if (TaskChecker.isSatisfied(categories, todo)) {
			ids.add(todo.getTaskID());
		}
		for (Task event: events) if (TaskChecker.isSatisfied(categories, event)) {
			ids.add(event.getTaskID());
		}
		for (Task deadline: deadlines) if (TaskChecker.isSatisfied(categories, deadline)) {
			ids.add(deadline.getTaskID());
		}
		return ids;
	}

	/**
	 * 
	 * @param id is index of task
	 * @return task (same reference) with index id
	 */
	private Task getRealTaskById(int id) {
		for (Task event:events) if (event.getTaskID() == id){
			return event;
		}
		for (Task todo:todos) if (todo.getTaskID() == id){
			return todo;
		}
		for (Task deadline:deadlines) if (deadline.getTaskID() == id){
			return deadline;
		}
		return null;
	}

	public boolean delete(int id) {
		Task task = getRealTaskById(id);
		if (task != null) {
			todos.remove(task);
			deadlines.remove(task);
			events.remove(task);
			return true;
		}
		return false;
	}

	/**
	 * save all current data in memory to disk
	 */
	public void saveAllTask() {
		FileHandler.writeToFile(todoFile, DataParser.serialize(todos, TODO));
		FileHandler.writeToFile(eventFile, DataParser.serialize(events, EVENT));
		FileHandler.writeToFile(deadlineFile, DataParser.serialize(deadlines, DEADLINE));
	}

	/**
	 * 
	 * @param type 
	 * save current task of "type" in memory to disk 
	 */
	public void saveTaskType(TASK_TYPE type) {
		switch (type) {
		case TODO:
			FileHandler.writeToFile(todoFile, DataParser.serialize(todos, TODO));
			break;
		case EVENT:
			FileHandler.writeToFile(eventFile, DataParser.serialize(events, EVENT));
			break;
		case DEADLINE:
			FileHandler.writeToFile(deadlineFile, DataParser.serialize(deadlines, DEADLINE));
			break;
		default:
			saveAllTask();
		}
	}

	public String getStoreDir() {
		return storeDir;
	}
	
	public void setStoreDir(String storeDir) {
		initStoreDir(storeDir);
		saveAllTask();
	}
}
```
###### test\StorageTest.java
``` java
public class StorageTest {

	@Test
	public void test() {
		/*DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   //get current date time with Date()
		Date date = new Date();
		System.out.println(date.toString());
		Event event = new Event(date, date, "Task description", false);
		Storage.getInstance().addTask(event, EVENT);
		Storage.getInstance().saveAllTask();
		Storage.getInstance().init();
		List<Task> events = Storage.getInstance().getAllTask(EVENT);
		System.out.println(((Event)events.get(0)).getFromDate().toString());*/
	}

	@Test
	public void testStorageInit() {
		String workingDir = System.getProperty("user.dir");
		workingDir = DirectoryHandler.fixDir(workingDir);

		Storage storage = Storage.getInstance();
		storage.init();

		assertEquals(workingDir, FileHandler.readFromFile("config.txt"));
	}

	@Test
	public void testFileHandler() {
		String content = "Some content";
		FileHandler.writeToFile("tem.txt", content);
		String readBackContent = FileHandler.readFromFile("tem.txt");
		assertEquals(content, readBackContent);
	}

	@Test
	public void testChangeDir() {
		Storage storage = Storage.getInstance();
		String newDir = "F:\\TEM";
		storage.setStoreDir(newDir);
		assertEquals(DirectoryHandler.fixDir(newDir), FileHandler.readFromFile("config.txt"));
	}

}
```

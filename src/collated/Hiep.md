# Hiep
###### main\logic\Add.java
``` java
	public boolean undo(Task task) {
		return storage.delete(task.getTaskID());
	}

```
###### main\logic\Add.java
``` java
	public boolean redo(Task task) {
		return storage.addTask(task);
	}

```
###### main\logic\Add.java
``` java
	private void addNewTask(Task task) {
		vControl.addNewData(new VersionModel.AddModel(task));
	}
}
```
###### main\logic\ChangeStatus.java
``` java
	public boolean undo(List<Integer> ids, List<Boolean> oldStatuses) {
		for (int i = 0; i < ids.size(); i++) {
			storage.changeStatus(ids.get(i), oldStatuses.get(i));
		}
		return true;
	}

```
###### main\logic\ChangeStatus.java
``` java
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
	public boolean undo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.addTask(task);
		}
		return true;
	}

```
###### main\logic\Delete.java
``` java
	public boolean redo(List<Task> tasks) {
		for (Task task : tasks) {
			storage.delete(task.getTaskID());
		}
		return true;
	}
}
```
###### main\logic\Display.java
``` java
 *
 */
public class Display extends Command {

	private static Display display = null;
	private static Storage storage = Storage.getInstance();
	private TASK_TYPE taskType;

	private Display() {
	}

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

	public List<List<Task>> process(ParsedObject obj) {
		//List<Task> result = new ArrayList<>();
		List<List<Task>> result = new ArrayList<List<Task>>();
		List<Task> tasks = storage.getAllTask(TASK_TYPE.ALL);
		List<Task> deadlines = new ArrayList<>();
		List<Task> events = new ArrayList<>();

		if (obj.getParamType().equals(PARAM_TYPE.CATEGORY)) {
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
					}
				}
			}
			message = "Tasks are display!";
		} else {
			Date fromDate, toDate;
			switch (obj.getCommandType()) {
			case DISPLAY_ON:
				for (Object dateObj : obj.getObjects()) {
					Date checkDate = (Date) dateObj;
					message = "Displaying all tasks on " + new SimpleDateFormat("EEE, dd MMM yyyy").format(checkDate) + ".";
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
				return null;
			default:
				message = "No matching tasks found.";
				return null;
			}
		}


		result.add(deadlines);
		result.add(events);

		return result;
	}

	private boolean isOnBetween(Date left, Date right, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date startDate = resetTime((Date)left.clone(), true);
		Date endDate = resetTime((Date)right.clone(), false);

		if (cur.compareTo(startDate) >= 0 && cur.compareTo(endDate) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isBetween(Date left, Date right, Date cur) {
		return (left.compareTo(cur) <= 0 && cur.compareTo(right) <= 0);
	}

	private boolean isOn(Date date, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date comparedDate = resetTime((Date)date.clone(), true);

		if (currentDate.compareTo(comparedDate) == 0) {
			return true;
		} else {
			return false;
		}
	}

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
###### main\logic\VersionControl.java
``` java
public class VersionControl extends Command {

	private int curPosition = -1;
	private List<VersionModel> vList = new ArrayList<>();

	private static VersionControl instance = null;

	/*private static final Add add = Add.getInstance();
	private static final Delete delete = Delete.getInstance();
	private static final Update update = Update.getInstance();
	private static final ChangeStatus changeStatus = ChangeStatus.getInstance();
*/
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
				return true;
			} else {
				message = "There are no available tasks to undo.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
			}
		case REDO:
			count = redo(numOfExec);
			if (count > 0) {
				message = "The previous " + count + " commands have been reversed.";
				return true;
			} else {
				message = "There are no available tasks to redo.";
				taskType = EnumTypes.TASK_TYPE.INVALID;
				return false;
			}
		}

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
```
###### main\model\ObserverEvent.java
``` java
public class ObserverEvent {
	public static int CHANGE_MESSAGE_CODE = 0;
	public static int CHANGE_TABLE_CODE = 1;
	public static int CHANGE_USER_INPUT_CODE = 2;

	private int code;
	private Object payload;

	public ObserverEvent(int code, Object payload) {
		this.code = code;
		this.payload = payload;
	}

	public int getCode() {
		return this.code;
	}

	public Object getPayload() {
		return this.payload;
	}

	public static class EInput {
		private String command;

		public EInput(String command) {
			this.command = command;
		}

		public String getCommand() {
			return this.command;
		}
	}

	public static class EMessage {
		private String message;

		public EMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return this.message;
		}
	}

	public static class ETasks {
		private List<Task> tasks = new ArrayList<>();
		private TASK_TYPE taskType;
		private boolean shouldSwitch = false;

		public ETasks(List<Task> tasks, TASK_TYPE taskType, boolean shouldSwitch) {
			this.taskType = taskType;
			this.tasks = tasks;
			this.shouldSwitch = shouldSwitch;
		}

		public List<Task> getTasks() {
			return this.tasks;
		}

		public TASK_TYPE getTaskType() {
			return this.taskType;
		}

		public boolean shouldSwitch() {
			return this.shouldSwitch;
		}
	}
}
```
###### main\model\VersionModel.java
``` java
public abstract class VersionModel {
	private final COMMAND_TYPE cmdType;

	public COMMAND_TYPE getCmdType() {
		return this.cmdType;
	}

	public VersionModel(COMMAND_TYPE cmdType) {
		this.cmdType = cmdType;
	}

	public static class AddModel extends VersionModel{
		private Task task = null;
		public AddModel(Task task) {
			super(COMMAND_TYPE.ADD);
			this.task = task;
		}
		public Task getTask() {
			return this.task;
		}
	}

	public static class DeleteModel extends VersionModel {

		private List<Task> tasks = new ArrayList<>();

		public DeleteModel(List<Task> tasks) {
			super(COMMAND_TYPE.DELETE);
			this.tasks = tasks;
		}

		public List<Task> getTasks() {
			return this.tasks;
		}

	}

	public static class UpdateModel extends VersionModel {

		private Task oldTask = null, newTask = null;

		public UpdateModel(Task oldTask, Task newTask) {
			super(COMMAND_TYPE.UPDATE);
			this.oldTask = oldTask;
			this.newTask = newTask;
		}

		public Task getOldTask() {
			return this.oldTask;
		}

		public Task getNewTask() {
			return this.newTask;
		}
	}

	public static class ChangeStatusModel extends VersionModel {

		private List<Integer> ids = new ArrayList<>();
		private List<Boolean> oldStatuses = new ArrayList<>();
		private boolean newStatus = true;

		public ChangeStatusModel(List<Integer> ids, List<Boolean> oldStatuses, boolean newStatus) {
			super(COMMAND_TYPE.DONE_UNDONE);
			this.ids = ids;
			this.oldStatuses = oldStatuses;
			this.newStatus = newStatus;
		}

		public List<Integer> getIds() {
			return this.ids;
		}

		public List<Boolean> getOldStatuses() {
			return this.oldStatuses;
		}

		public boolean getNewStatus() {
			return this.newStatus;
		}
	}

}
```
###### main\storage\Storage.java
``` java
public class Storage {

	private static String TODO_FILE = "todo.txt";
	private static String EVENT_FILE = "event.txt";
	private static String DEADLINE_FILE = "deadline.txt";
	private static String DATA_FOLDER = "data";

	private String configFile = "config.txt";
	private String storeDir;
	private String todoFile;
	private String deadlineFile;
	private String eventFile;

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

	/*private int getMaxId(List<Task> tasks) {
		int res = 0;
		for (Task task : tasks) {
			res = Math.max(task.getTaskID(), res);
		}
		return res;
	}*/

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

	public void saveAllTask() {
		FileHandler.writeToFile(todoFile, DataParser.serialize(todos, TODO));
		FileHandler.writeToFile(eventFile, DataParser.serialize(events, EVENT));
		FileHandler.writeToFile(deadlineFile, DataParser.serialize(deadlines, DEADLINE));
	}

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

	/**
	 * @return the storeDir
	 */
	public String getStoreDir() {
		return storeDir;
	}

	/**
	 * @param storeDir the storeDir to set
	 */
	public void setStoreDir(String storeDir) {
		initStoreDir(storeDir);
		saveAllTask();
	}

	public void importData(String dataDir, boolean isReplace) {
		List<Task> importedTodos = DataParser.deserialize(FileHandler.readFromFile(dataDir + "/" + TODO_FILE), TODO);
		List<Task> importedEvents = DataParser.deserialize(FileHandler.readFromFile(dataDir + "/" + EVENT_FILE), EVENT);
		List<Task> importedDeadlines = DataParser.deserialize(FileHandler.readFromFile(dataDir + "/" + DEADLINE_FILE), DEADLINE);
		if (isReplace) {
			todos = importedTodos;
			events = importedEvents;
			deadlines = importedDeadlines;
		} else {
			todos.addAll(importedTodos);
			events.addAll(importedEvents);
			deadlines.addAll(importedDeadlines);
		}
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

package main.storage;

import static main.model.EnumTypes.TASK_TYPE.DEADLINE;
import static main.model.EnumTypes.TASK_TYPE.EVENT;
import static main.model.EnumTypes.TASK_TYPE.TODO;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes.CATEGORY;
import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Task;

//@@author Hiep
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

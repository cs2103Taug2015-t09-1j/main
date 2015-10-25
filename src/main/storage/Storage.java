package main.storage;

import static main.models.EnumTypes.TASK_TYPE.DEADLINE;
import static main.models.EnumTypes.TASK_TYPE.EVENT;
import static main.models.EnumTypes.TASK_TYPE.TODO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.prettytime.shade.net.fortuna.ical4j.model.parameter.Dir;

import main.logic.Logic;
import main.models.Deadline;
import main.models.Event;
import main.models.Task;
import main.models.Todo;
import main.models.EnumTypes.TASK_TYPE;

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

	private static Storage storage = Storage.getInstance();

	private Storage() {
		init();
	}

	public static Storage getInstance() {
		if (storage == null) {
			storage = new Storage();
		}
		return storage;
	}

	public static void start() {
		Storage.getInstance();
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
			default: return new ArrayList<>();
		}
	}

	public Task getTaskByID(int id) {
		for (Task event:events) if (event.getTaskID() == id){
			return event.clone();
		}
		for (Task event:todos) if (event.getTaskID() == id){
			return event.clone();
		}
		for (Task event:deadlines) if (event.getTaskID() == id){
			return event.clone();
		}
		return null;
	}

	public boolean delete(int id) {
		for (Task event : events) if (event.getTaskID() == id) {
			events.remove(event);
			return true;
		}
		for (Task todo : todos) if (todo.getTaskID() == id) {
			todos.remove(todo);
			return true;
		}
		for (Task deadline : deadlines) if (deadline.getTaskID() == id) {
			deadlines.remove(deadline);
			return true;
		}
		return true;
	}

	public void changeStatus(List<Integer> ids, boolean status) {
		for (int id : ids) {
			Task task = getTaskByID(id);
			if (task != null) {
				task.setDone(status);
			}
		}
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

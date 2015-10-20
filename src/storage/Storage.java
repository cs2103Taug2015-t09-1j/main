package storage;

import static models.EnumTypes.TASK_TYPE.DEADLINE;
import static models.EnumTypes.TASK_TYPE.EVENT;
import static models.EnumTypes.TASK_TYPE.TODO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.prettytime.shade.net.fortuna.ical4j.model.parameter.Dir;

import logic.Logic;
import models.EnumTypes.TASK_TYPE;
import models.Task;

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
	
	private void initDir() {
		initStoreDirFromConfig();
		updateStoreDir();
	}
	
	private void initTasks() {
		todos = DataParser.deserialize(FileHandler.readFromFile(todoFile), TODO);
		events = DataParser.deserialize(FileHandler.readFromFile(eventFile), EVENT);
		deadlines = DataParser.deserialize(FileHandler.readFromFile(deadlineFile), DEADLINE);
	}
	
	private void initTaskId() {
		int curMaxId = Math.max(getMaxId(todos), Math.max(getMaxId(events), getMaxId(deadlines)));
		Task.setNextId(curMaxId);
	}
	
	private void initStoreDirFromConfig() {
		storeDir = FileHandler.readFromFile(configFile);
		if (storeDir.equals("")) storeDir = DirectoryHandler.getCurrentDir();
		FileHandler.createNewFolderIfNotExisit(storeDir + "/" + DATA_FOLDER);
	}
	
	private void updateStoreDir() {
		todoFile = storeDir + "/" + DATA_FOLDER + "/" + TODO_FILE;
		eventFile = storeDir + "/" + DATA_FOLDER + "/" + EVENT_FILE;
		deadlineFile = storeDir + "/" + DATA_FOLDER + "/" + DEADLINE_FILE;
		
		FileHandler.createNewFileIfNotExisit(todoFile);
		FileHandler.createNewFileIfNotExisit(deadlineFile);
		FileHandler.createNewFileIfNotExisit(eventFile);
	}
	
	public void init() {
		initDir();
		initTasks();
		initTaskId();
	}

	private int getMaxId(List<Task> tasks) {
		int res = 0;
		for (Task task : tasks) {
			res = Math.max(task.getTaskID(), res);
		}
		return res;
	}

	public void addTask(Task task, TASK_TYPE type) {
		switch (type) {
			case TODO: todos.add(task); break;
			case EVENT: events.add(task); break;
			case DEADLINE: deadlines.add(task); break;
			default: break;
		}
	}

	public List<Task> getAllTask(TASK_TYPE type) {
		switch (type) {
			case TODO: return todos;
			case EVENT: return events;
			case DEADLINE: return deadlines;
			default: return new ArrayList<>();
		}
	}

	public Task getTaskByID(int id) {
		for (Task event:events) if (event.getTaskID() == id){
			return event;
		}
		for (Task event:todos) if (event.getTaskID() == id){
			return event;
		}
		for (Task event:deadlines) if (event.getTaskID() == id){
			return event;
		}
		return null;
	}

	public void delete(int id) {
		Task task = getTaskByID(id);
		if (task == null) {
			return;
		}
		events.remove(task);
		todos.remove(task);
		deadlines.remove(task);
	}

	public void delete(List<Integer> ids) {
		for (int id : ids) {
			delete(id);
		}
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
		this.storeDir = DirectoryHandler.fixDir(storeDir);
		updateStoreDir();
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

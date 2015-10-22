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


	private void initTasks() {
		todos = DataParser.deserialize(FileHandler.readFromFile(todoFile), TODO);
		events = DataParser.deserialize(FileHandler.readFromFile(eventFile), EVENT);
		deadlines = DataParser.deserialize(FileHandler.readFromFile(deadlineFile), DEADLINE);
	}

	private void initTaskId() {
		int curMaxId = Math.max(getMaxId(todos), Math.max(getMaxId(events), getMaxId(deadlines)));
		Task.setNextId(curMaxId);
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

	private int getMaxId(List<Task> tasks) {
		int res = 0;
		for (Task task : tasks) {
			res = Math.max(task.getTaskID(), res);
		}
		return res;
	}

	public void addTask(Task task) {
		if (task instanceof Event) {
			events.add(task);
		} else if (task instanceof Todo) {
			todos.add(task);
		} else if (task instanceof Deadline) {
			deadlines.add(task);
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

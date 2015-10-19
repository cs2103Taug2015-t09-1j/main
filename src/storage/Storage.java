package storage;

import static models.EnumTypes.TASK_TYPE.DEADLINE;
import static models.EnumTypes.TASK_TYPE.EVENT;
import static models.EnumTypes.TASK_TYPE.TODO;

import java.util.ArrayList;
import java.util.List;

import logic.Logic;
import models.EnumTypes.TASK_TYPE;
import models.Task;

public class Storage {
	private String storeFolder = "data";
	private String todo = storeFolder + "/Floating.txt";
	private String deadline = storeFolder + "/Deadline.txt";
	private String event = storeFolder + "/Event.txt";

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

	public void init() {
		FileHandler.createNewFolderIfNotExisit(storeFolder);
		todos = DataParser.deserialize(FileHandler.readFromFile(todo), TODO);
		events = DataParser.deserialize(FileHandler.readFromFile(event), EVENT);
		deadlines = DataParser.deserialize(FileHandler.readFromFile(deadline), DEADLINE);
		int curMaxId = Math.max(getMaxId(todos), Math.max(getMaxId(events), getMaxId(deadlines)));
		Task.setNextId(curMaxId);
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
		FileHandler.writeToFile(todo, DataParser.serialize(todos, TODO));
		FileHandler.writeToFile(event, DataParser.serialize(events, EVENT));
		FileHandler.writeToFile(deadline, DataParser.serialize(deadlines, DEADLINE));
	}

	public void saveTaskType(TASK_TYPE type) {
		switch (type) {
		case TODO:
			FileHandler.writeToFile(todo, DataParser.serialize(todos, TODO));
			break;
		case EVENT:
			FileHandler.writeToFile(event, DataParser.serialize(events, EVENT));
			break;
		case DEADLINE:
			FileHandler.writeToFile(deadline, DataParser.serialize(deadlines, DEADLINE));
			break;
		default:
			saveAllTask();
		}
	}

	/**
	 * @return the storeFolder
	 */
	public String getStoreFolder() {
		return storeFolder;
	}

	/**
	 * @param storeFolder the storeFolder to set
	 */
	public void setStoreFolder(String storeFolder) {
		this.storeFolder = storeFolder;
	}
}

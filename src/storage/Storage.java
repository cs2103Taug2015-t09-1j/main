package storage;

import static models.Commands.TASK_TYPE.DEADLINE;
import static models.Commands.TASK_TYPE.EVENT;
import static models.Commands.TASK_TYPE.TODO;

import java.util.ArrayList;
import java.util.List;

import models.Commands.TASK_TYPE;
import models.Task;

public class Storage {
	public static interface storageDir {
		String storeFolder = "data";
		String todo = "data/Floating.txt";
		String deadline = "data/Deadline.txt";
		String event = "data/Event.txt";
	}

	private static List<Task> todos = new ArrayList<>();
	private static List<Task> events = new ArrayList<>();
	private static List<Task> deadlines = new ArrayList<>();

	public static void init() {
		FileHandler.createNewFolderIfNotExisit(storageDir.storeFolder);
		todos = DataParser.deserialize(FileHandler.readFromFile(storageDir.todo), TODO);
		events = DataParser.deserialize(FileHandler.readFromFile(storageDir.event), EVENT);
		deadlines = DataParser.deserialize(FileHandler.readFromFile(storageDir.deadline), DEADLINE);
		int curMaxId = Math.max(getMaxId(todos), Math.max(getMaxId(events), getMaxId(deadlines)));
		Task.setNextId(curMaxId);
	}

	private static int getMaxId(List<Task> tasks) {
		int res = 0;
		for (Task task : tasks) {
			res = Math.max(task.getTaskID(), res);
		}
		return res;
	}

	public static void addTask(Task task, TASK_TYPE type) {
		switch (type) {
			case TODO: todos.add(task); break;
			case EVENT: events.add(task); break;
			case DEADLINE: deadlines.add(task); break;
			default: break;
		}
	}

	public static List<Task> getAllTask(TASK_TYPE type) {
		switch (type) {
			case TODO: return todos;
			case EVENT: return events;
			case DEADLINE: return deadlines;
			default: return new ArrayList<>();
		}
	}

	public static Task getTaskByID(int id) {
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

	public static void delete(int id) {
		Task task = getTaskByID(id);
		if (task == null) {
			return;
		}
		events.remove(task);
		todos.remove(task);
		deadlines.remove(task);
	}

	public static void delete(List<Integer> ids) {
		for (int id : ids) {
			delete(id);
		}
	}

	public static void changeStatus(List<Integer> ids, boolean status) {
		for (int id : ids) {
			Task task = getTaskByID(id);
			if (task != null) {
				task.setDone(status);
			}
		}
	}

	public static void saveAllTask() {
		FileHandler.writeToFile(storageDir.todo, DataParser.serialize(todos, TODO));
		FileHandler.writeToFile(storageDir.event, DataParser.serialize(events, EVENT));
		FileHandler.writeToFile(storageDir.deadline, DataParser.serialize(deadlines, DEADLINE));
	}

	public static void saveTaskType(TASK_TYPE type) {
		switch (type) {
		case TODO:
			FileHandler.writeToFile(storageDir.todo, DataParser.serialize(todos, TODO));
			break;
		case EVENT:
			FileHandler.writeToFile(storageDir.event, DataParser.serialize(events, EVENT));
			break;
		case DEADLINE:
			FileHandler.writeToFile(storageDir.deadline, DataParser.serialize(deadlines, DEADLINE));
			break;
		default:
			saveAllTask();
		}
	}
}

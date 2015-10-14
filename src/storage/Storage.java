package storage;

import java.util.ArrayList;
import java.util.List;
import static models.Commands.TASK_TYPE.*;

import models.Commands.TASK_TYPE;
import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.Task;

public class Storage {
	public static interface storageDir {
		String floatingTask = "data/Floating.txt";
		String deadline = "data/Deadline.txt";
		String event = "data/Event.txt";
	}
	
	private static List<Task> floatingTasks = new ArrayList<>();
	private static List<Task> events = new ArrayList<>();
	private static List<Task> deadlines = new ArrayList<>(); 
	
	public static void init() {
		floatingTasks = DataParser.deserialize(FileHandler.readFromFile(storageDir.floatingTask), FLOATING_TASK);
		events = DataParser.deserialize(FileHandler.readFromFile(storageDir.event), EVENT);
		deadlines = DataParser.deserialize(FileHandler.readFromFile(storageDir.deadline), DEADLINE_TASK);
	}
	
	public static void addTask(Task task, TASK_TYPE type) {
		switch (type) {
			case FLOATING_TASK: floatingTasks.add(task); break;
			case EVENT: events.add(task); break;
			case DEADLINE_TASK: deadlines.add(task); break;
		}
	}
	
	public static List<Task> getAllTask(TASK_TYPE type) {
		switch (type) {
			case FLOATING_TASK: return floatingTasks;
			case EVENT: return events;
			case DEADLINE_TASK: return deadlines;
			default: return new ArrayList<>();
		}
	}
	
	public static Task getTaskById(int id) {
		for (Task event:events) if (event.getTaskID() == id){
			return event;
		}
		for (Task event:floatingTasks) if (event.getTaskID() == id){
			return event;
		}
		for (Task event:deadlines) if (event.getTaskID() == id){
			return event;
		}
		return null;
	}
	
	public static void delete(int id) {
		Task task = getTaskById(id);
		if (task == null) {
			return;
		}
		events.remove(task);
		floatingTasks.remove(task);
		deadlines.remove(task);
	}
	
	public static void delete(List<Integer> ids) {
		for (int id : ids) {
			delete(id);
		}
	}
	
	public static void saveAllTask() {
		FileHandler.writeToFile(storageDir.floatingTask, DataParser.serialize(floatingTasks, FLOATING_TASK));
		FileHandler.writeToFile(storageDir.event, DataParser.serialize(events, EVENT));
		FileHandler.writeToFile(storageDir.deadline, DataParser.serialize(deadlines, DEADLINE_TASK));
	}
}

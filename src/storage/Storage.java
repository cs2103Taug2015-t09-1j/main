package storage;

import java.util.ArrayList;
import java.util.List;

import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.Task;

public class Storage {
	public static interface storageDir {
		String floatingTask = "Floating.txt";
		String deadline = "Deadline.txt";
		String event = "Event.txt";
	}
	
	public static List<Task> getAllTask() {
		List<FloatingTask> floatingTasks = DataParser.deserialize(FileHandler.readFromFile(storageDir.floatingTask));
		List<Event> events = DataParser.deserialize(FileHandler.readFromFile(storageDir.event));
		List<DeadlineTask> deadlines = DataParser.deserialize(FileHandler.readFromFile(storageDir.deadline));
		List<Task> allTask = new ArrayList<>();
		allTask.addAll(floatingTasks);
		allTask.addAll(events);
		allTask.addAll(deadlines);
		return allTask;
	}
	
	public static void saveAllTask(List<Task> tasks) {
		List<FloatingTask> floatingTasks = new ArrayList<>();
		List<Event> events = new ArrayList<>();
		List<DeadlineTask> deadlines = new ArrayList<>();
		for(Task task : tasks) {
			if (task instanceof FloatingTask) {
				floatingTasks.add((FloatingTask)task);
			} else if (task instanceof Event) {
				events.add((Event)tasks);
			} else if (task instanceof DeadlineTask) {
				deadlines.add((DeadlineTask)task);
			}
		}
		FileHandler.writeToFile(storageDir.floatingTask, DataParser.serialize(floatingTasks));
		FileHandler.writeToFile(storageDir.event, DataParser.serialize(events));
		FileHandler.writeToFile(storageDir.deadline, DataParser.serialize(deadlines));
	}
}

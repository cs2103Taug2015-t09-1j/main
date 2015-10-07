package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.*;
import parser.Parser;

public class Logic {

	public static List<Task> tasks;
	
	public static void init() {
	}
	
	public static Vector<Task> processCommand(String command) {
		String commandType = Parser.getFirstWord(command);
		command = Parser.removeFirstWord(command);
		switch (commandType) {
			case "add": return addTask(command); 
			case "update": return updateTask(command);
			case "display": return display(command);
			case "delete": return deleteTask(command);
			case "search": break;
			default: break;
		}
		return null;
	}
	
	private static Vector<Task> addTask(String command) {
		Vector<Task> tasks = new Vector<>(); 
		tasks.addElement(Parser.getTask(command));
		return tasks;
	}
	
	private static Vector<Task> updateTask(String command) {
		Vector<Task> tasks = new Vector<>();
		int id = Parser.getTaskId(command);
		String desc = Parser.removeFirstWord(command);
		Task task = findTaskById(id);
		if (task == null) {
			return tasks;
		}
		task.setTaskDesc(desc);
		tasks.addElement(task);
		return tasks;
	}
	
	private static Vector<Task> display(String command) {
		return new Vector<Task>(tasks);
	}
	
	private static Vector<Task> deleteTask(String command) {
		List<Integer> deletedIds = Parser.getTaskIds(command);
		for (int i = 0; i < tasks.size(); i++) if (deletedIds.contains(tasks.get(i).getTaskID())){
			tasks.remove(i);
		}
		return new Vector<Task>(tasks); 
	}
	
	private static Vector<Task> search(String command) {
		Vector<Task> matchedTasks = new Vector<>();
		for (Task task: tasks) if (task.getTaskDesc().indexOf(command) != -1) {
			matchedTasks.addElement(task);
		}
		return matchedTasks;
	}
	
	private static Task findTaskById(int id) {
		for (Task task: tasks) {
			if (task.getTaskID() == id) return task;
		}
		return null;
	}
}
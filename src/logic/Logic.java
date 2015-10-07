package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import models.*;
import parser.Parser;
import storage.Storage;
import ui.MainGUI;

public class Logic {

	public static List<Task> tasks;
	
	// need to be call when the app start
	public static void init() {
		tasks = Storage.getAllTask();
		List<Task> _tasks = tasks;
		int maxId = 0;
		for (Task task: tasks) {
			maxId = Math.max(maxId, task.getTaskID());
		}
		Task.nextId.set(maxId);
	}
	
	public static void processCommand(String command, MainGUI uiRef) {
		Vector<Task> data = new Vector<>();
		String message = null;
		
		String commandType = Parser.getFirstWord(command);
		command = Parser.removeFirstWord(command);
		switch (commandType) {
			case "add": data = addTask(command); break;
			case "update": data = updateTask(command); break;
			case "display": data = display(command); break;
			case "delete": data = deleteTask(command); break;
			case "search": data = search(command); break;
			case "done": data = markTask(command, true); break;
			case "undone": data = markTask(command, false); break;
			default: message = "Incorrect format!";
		}
		
		Storage.saveAllTask(tasks);
		
		if (message == null) {
			uiRef.updateTables(data, "All");
		}
	}
	
	private static Vector<Task> addTask(String command) {
		Vector<Task> result = new Vector<>();
		Task newTask = Parser.getTask(command);
		result.addElement(newTask);
		tasks.add(newTask);
		System.out.println(tasks.size());
		return result;
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
	
	private static Vector<Task> markTask(String command, boolean isDone)  {
		List<Integer> markIds = Parser.getTaskIds(command);
		for (Task task: tasks) if (markIds.contains(task.getTaskID())){
			task.setDone(isDone);
		}
		return new Vector<Task>(tasks); 
	}
	
	private static Task findTaskById(int id) {
		for (Task task: tasks) {
			if (task.getTaskID() == id) return task;
		}
		return null;
	}
}
package storage;

import java.util.ArrayList;
import java.util.List;

import model.Todo;

public class Storage {
	
	private String storeDir = null;
	private static volatile  Storage instance = null;
	private List<Todo> todos;
	
	public static Storage getInstance(String storeDir) {
        if (instance == null) {
            synchronized (Storage.class) {
                if (instance == null) {
                    instance = new Storage(storeDir);
                }
            }
        }
        return instance;
	}
	
	public Storage(String storeDir) {
		this.storeDir = storeDir;
		this.todos = new ArrayList<>();
	}
	
	public void addNewTask(Todo todo) {
		this.todos.add(todo);
	}
	
	public List<Todo> getAll() {
		return this.todos;
	}
}

package test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import storage.DataParser;
import storage.Storage;
import model.Factory;
import model.Todo;

public class StorageTest {

	@Test
	public void test() {
		Storage storage = new Storage("C:/Users/Hiep/Desktop/Temporary Code/todokoro.txt");
		storage.addNewTask(Factory.createNewDeadline("doing tmr", 12345));
		storage.addNewTask(Factory.createNewDeadline("hehehe", 4191989));
		String content = DataParser.serialize(storage.getAll());
		List<Todo> todos = DataParser.deserialize(content);
		System.out.println(todos.size());
	}

}

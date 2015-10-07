package test;

import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;

import models.Task;
import storage.Storage;

public class StorageTest {

	@Test
	public void test() {
		List<Task> tasks = Storage.getAllTask();
		System.out.println(tasks.size());
	}

}

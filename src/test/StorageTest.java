package test;

import static models.EnumTypes.TASK_TYPE.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import models.Event;
import models.Task;
import storage.Storage;

public class StorageTest {

	@Test
	public void test() {
		/*DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   //get current date time with Date()
		Date date = new Date();
		System.out.println(date.toString());
		Event event = new Event(date, date, "Task description", false);
		Storage.getInstance().addTask(event, EVENT);
		Storage.getInstance().saveAllTask();
		Storage.getInstance().init();
		List<Task> events = Storage.getInstance().getAllTask(EVENT);
		System.out.println(((Event)events.get(0)).getFromDate().toString());*/
	}
	
	@Test 
	public void testStorageInit() {
		String workingDir = System.getProperty("user.dir");
		System.out.println("Current working directory : " + workingDir);

		Storage storage = Storage.getInstance();
		storage.init();
		
		storage.saveAllTask();
	}

}

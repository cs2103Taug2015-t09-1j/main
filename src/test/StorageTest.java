package test;

import static org.junit.Assert.*;
import static models.Commands.TASK_TYPE.*;

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
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   //get current date time with Date()
		Date date = new Date();
		System.out.println(date.toString());
		Event event = new Event(date, date, "Task description", false);
		Storage.addTask(event, EVENT);
		Storage.saveAllTask();
		Storage.init();
		List<Task> events = Storage.getAllTask(EVENT);
		System.out.println(((Event)events.get(0)).getFromDate().toString());
	}

}

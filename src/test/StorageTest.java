package test;

import static models.EnumTypes.TASK_TYPE.*;
import static org.junit.Assert.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.ocpsoft.prettytime.shade.net.fortuna.ical4j.model.parameter.Dir;

import models.Event;
import models.Task;
import storage.Storage;
import sun.awt.RepaintArea;
import storage.DirectoryHandler;
import storage.FileHandler;

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
		workingDir = DirectoryHandler.fixDir(workingDir);

		Storage storage = Storage.getInstance();
		storage.init();
		
		assertEquals(workingDir, FileHandler.readFromFile("config.txt"));
	}
	
	@Test 
	public void testFileHandler() {
		String content = "Some content";
		FileHandler.writeToFile("tem.txt", content);
		String readBackContent = FileHandler.readFromFile("tem.txt");
		assertEquals(content, readBackContent);
	}
	
	@Test 
	public void testChangeDir() {
		Storage storage = Storage.getInstance();
		String newDir = "F:\\TEM";
		storage.setStoreDir(newDir);
		assertEquals(DirectoryHandler.fixDir(newDir), FileHandler.readFromFile("config.txt"));
	}

}

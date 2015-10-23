package test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import main.models.EnumTypes.TASK_TYPE;
import main.models.Event;
import main.models.Task;
import main.storage.Storage;

public class ModelTest {

	@Test
	public void testCloneTask() {
		List<Task> tasks = Storage.getInstance().getAllTask(TASK_TYPE.EVENT);
		Event event = (Event)tasks.get(0);
		Event eventClone = (Event)event.clone();
		Date d1 = event.getFromDate(),
			 d2 = eventClone.getFromDate();
		if (d1 == d2) {
			System.out.println("equal");
		} else {
			System.out.println("not equal");
		}
	}
}

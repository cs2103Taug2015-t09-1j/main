package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import main.logic.Add;
import main.model.EnumTypes;
import main.model.EnumTypes.COMMAND_TYPE;
import main.model.ParsedObject;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;

public class AddTest {
	
	@Test
	public void main() {
		testExecute();
		testRedo();
		testUndo();
	}

	public void testExecute() {
		Calendar cal = Calendar.getInstance();
		cal.set(2015, Calendar.NOVEMBER, 29);
		Date date = cal.getTime();
		Deadline task = new Deadline(date, "hello", false);
		ArrayList<Task> obj = new ArrayList<Task>();
		obj.add(task);
		ParsedObject deadline = new ParsedObject(null, null, EnumTypes.TASK_TYPE.DEADLINE, obj);
		Add add = Add.getInstance();
		assertTrue(add.execute(deadline));
		
		Event task1 = new Event(date, date, "lol", false);
		obj.clear();
		obj.add(task1);
		ParsedObject singleEvent = new ParsedObject(null, null, EnumTypes.TASK_TYPE.SINGLE_DATE_EVENT, obj);
		assertTrue(add.execute(singleEvent));
		
		Calendar cal1 = Calendar.getInstance();
		cal1.set(2015, Calendar.DECEMBER, 20);
		Date date1 = cal.getTime();
		Event task2 = new Event(date, date1, "wow", false);
		obj.clear();
		obj.add(task2);
		ParsedObject doubleEvent = new ParsedObject(null, null, EnumTypes.TASK_TYPE.DOUBLE_DATE_EVENT, obj);
		assertTrue(add.execute(doubleEvent));
		
		Todo task3 = new Todo("lawl", false);
		obj.clear();
		obj.add(task3);
		ParsedObject todo = new ParsedObject(null, null, EnumTypes.TASK_TYPE.TODO, obj);
		assertTrue(add.execute(todo));
		
		ParsedObject invalid = new ParsedObject(null, null, EnumTypes.TASK_TYPE.INVALID, obj);
		assertFalse(add.execute(invalid));
		
	}

	public void testUndo() {
		Todo todo = new Todo("hello", false);
		Add add = Add.getInstance();
		assertFalse(add.undo(todo));
	}

	public void testRedo() {
		Todo todo = new Todo("hello", false);
		Add add = Add.getInstance();
		assertTrue(add.redo(todo));
	}

}

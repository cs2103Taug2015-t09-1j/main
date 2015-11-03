package test;

import static org.junit.Assert.*;

import org.junit.Test;

import main.logic.Logic;
import main.model.EnumTypes.TASK_TYPE;
import main.storage.Storage;

public class LogicStorageIntegrateTest {

	@Test
	public void testAdd() {
		
		//Logic.init();
		Logic logic = Logic.getInstance();
		Storage storage = Storage.getInstance();
		
		int todoCnt = storage.getAllTask(TASK_TYPE.TODO).size();
		int deadlineCnt = storage.getAllTask(TASK_TYPE.DEADLINE).size();
		int evenCnt = storage.getAllTask(TASK_TYPE.EVENT).size();
		
		System.out.println(todoCnt + " " + deadlineCnt + " " + evenCnt);
		
		logic.processCommand("new todo");
		logic.processCommand("new deadline by 5pm");
		logic.processCommand("new event from 5pm to 6pm");
		
		int newTodoCnt = storage.getAllTask(TASK_TYPE.TODO).size();
		int newDeadlineCnt = storage.getAllTask(TASK_TYPE.DEADLINE).size();
		int newEvenCnt = storage.getAllTask(TASK_TYPE.EVENT).size();
		
		assertEquals(todoCnt + 1, newTodoCnt);
		assertEquals(deadlineCnt + 1, newDeadlineCnt);
		assertEquals(evenCnt + 1, newEvenCnt);
		
	}
	
	@Test
	public void testUpdate() {
		Logic logic = Logic.getInstance();
		Storage storage = Storage.getInstance();
		int id = 1;
		String newContent = "data has changed";
		
		logic.processCommand(String.format("update %d 4 %s", id, newContent));
		
		String newContentFromStorage = storage.getTaskByID(1).getTaskDesc();
		
		assertEquals(newContent, newContentFromStorage);
		
	}
}
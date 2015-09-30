package model;

public class Factory {
	
	private static int lastId = 0;
	
	public static Todo createNewEvent(String content, long startTime, long endTime) {
		lastId++;
		return new Todo(lastId, Const.EVENT_CODE, content, startTime, endTime, false);
	}
	
	public static Todo createNewDeadline(String content, long deadline) {
		lastId++;
		return new Todo(lastId, Const.DEADLINE_CODE, content, 0, deadline, false);
	}
	
	public static Todo createNewFloatingTask(String content) {
		lastId++;
		return new Todo(lastId, Const.FLOATING_TASK_CODE, content, 0, 0, false);
	}
	
}

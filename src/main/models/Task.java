/**
 *
 */
package main.models;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dalton
 *
 */
public abstract class Task implements Cloneable {
	private static AtomicInteger nextId = new AtomicInteger();
	//private final int taskID;
	private int taskID;
	private String taskDesc;
	private boolean isDone;

	public Task(String taskDesc, boolean isDone) {
		this.taskID = nextId.incrementAndGet();
		this.taskDesc = taskDesc;
		this.isDone = isDone;
	}

	@Override
	public Task clone() {
		try {
			return (Task)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskId(int taskID) {
		this.taskID = taskID;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}
	
	public static void setNextId(int initState) {
		nextId = new AtomicInteger(initState);
	}
}
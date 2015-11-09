/**
 *
 */
package main.model.taskModels;

import java.util.concurrent.atomic.AtomicInteger;

import main.model.EnumTypes.TASK_TYPE;

/**
 * The Class Task.
 *
 * @@author Dalton
 */
public abstract class Task implements Cloneable {

	// nextId is used to create an auto increment index for new task
	private static AtomicInteger nextId = new AtomicInteger();

	private int taskID;
	private String taskDesc;
	private boolean isDone;

	/**
	 * Instantiates a new task.
	 *
	 * @param taskDesc	the task desc
	 * @param isDone	the is done
	 */
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

	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * Sets the task id.
	 *
	 * @param taskID	the new task id
	 */
	public void setTaskId(int taskID) {
		this.taskID = taskID;
	}

	/**
	 * Gets the task desc.
	 *
	 * @return the task desc
	 */
	public String getTaskDesc() {
		return taskDesc;
	}

	/**
	 * Sets the task desc.
	 *
	 * @param taskDesc	the new task desc
	 */
	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	/**
	 * Checks if is done.
	 *
	 * @return true, if is done
	 */
	public boolean isDone() {
		return isDone;
	}

	/**
	 * Sets the done.
	 *
	 * @param isDone	the new done
	 */
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public TASK_TYPE getType() {
		return null;
	}

	/**
	 * Sets the next id.
	 *
	 * @param initState	the new next id
	 */
	public static void setNextId(int initState) {
		nextId = new AtomicInteger(initState);
	}
}
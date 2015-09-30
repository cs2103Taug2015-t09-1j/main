package ui;

public abstract class Task {
	protected int taskID;
	protected String taskDesc;
	protected boolean isDone;

	public Task(int taskID, String taskDesc, boolean isDone) {
		this.taskID = taskID;
		this.taskDesc = taskDesc;
		this.isDone = isDone;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
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
}
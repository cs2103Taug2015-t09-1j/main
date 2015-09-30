package model;

public class Todo {
	private int id;
	private int type;
	private String content;
	private long startTime, endTime;
	private boolean isCompleted; 
	
	public Todo(int id, int type, String content, long startTime, long endTime, boolean isCompleted) {
		this.id = id;
		this.type = type;
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isCompleted = isCompleted;
	}
	
	public long getStartTime() {
		return this.startTime;
	}
	
	public long getEndTime() {
		return this.endTime;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getType() {
		return this.type;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public boolean isCompleted() {
		return this.isCompleted;
	}
	
}

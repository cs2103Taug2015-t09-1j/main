/**
 *
 */
package models;
import java.util.Date;

/**
 * @author Dalton
 *
 */
public class Event extends Task{
	private String startTime;
	private String endTime;

	public Event(String startTime, String endTime, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}

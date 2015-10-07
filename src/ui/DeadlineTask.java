/**
 *
 */
package ui;
import java.time.LocalDateTime;

/**
 * @author Dalton
 *
 */
public class DeadlineTask extends Task{
	private LocalDateTime date;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	public DeadlineTask(int taskID, LocalDateTime date, LocalDateTime startTime, LocalDateTime endTime, String taskDesc, boolean isDone) {
		super(taskID, taskDesc, isDone);
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
}

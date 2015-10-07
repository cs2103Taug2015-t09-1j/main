/**
 *
 */
package models;

/**
 * @author Dalton
 *
 */
public class DeadlineTask extends Task {
	private String date;

	public DeadlineTask(String date, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}

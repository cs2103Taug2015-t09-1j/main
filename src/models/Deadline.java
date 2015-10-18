/**
 *
 */
package models;

import java.util.Date;

/**
 * @author Dalton
 *
 */
public class Deadline extends Task {
	private Date date;

	public Deadline(Date date, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}

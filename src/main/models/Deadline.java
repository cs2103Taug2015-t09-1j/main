/**
 *
 */
package main.models;

import java.util.Date;

import main.models.EnumTypes.TASK_TYPE;

/**
 * @author Dalton
 *
 */
public class Deadline extends Task {
	private static final TASK_TYPE type = TASK_TYPE.DEADLINE;
	private Date date;

	public Deadline(Date date, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.date = date;
	}
	
	@Override
	public Deadline clone() {
		Deadline deadline = (Deadline)super.clone();
		deadline.setDate((Date)deadline.getDate().clone());
		return deadline;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public TASK_TYPE getType() {
		return type;
	}
}

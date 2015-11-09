/**
 *
 */
package main.model.taskModels;

import java.util.Date;

import main.model.EnumTypes;
import main.model.EnumTypes.TASK_TYPE;

/**
 * The Class Deadline.
 *
 * @@author Dalton
 */
public class Deadline extends Task {
	private static final TASK_TYPE type = TASK_TYPE.DEADLINE;
	private Date date;

	/**
	 * Instantiates a new deadline.
	 *
	 * @param date		the date
	 * @param taskDesc	the task desc
	 * @param isDone	the is done boolean
	 */
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

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date
	 *            the new date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}
}

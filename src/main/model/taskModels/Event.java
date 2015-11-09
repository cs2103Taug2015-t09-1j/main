/**
 *
 */
package main.model.taskModels;
import java.util.Date;

import main.model.EnumTypes;
import main.model.EnumTypes.TASK_TYPE;

/**
 * The Class Event.
 *
 * @@author Dalton
 */
public class Event extends Task {
	private static final TASK_TYPE type = TASK_TYPE.EVENT;
	private Date fromDate;
	private Date toDate;

	/**
	 * Instantiates a new event.
	 *
	 * @param fromDate	the from date
	 * @param toDate	the to date
	 * @param taskDesc	the task desc
	 * @param isDone	the is done
	 */
	public Event(Date fromDate, Date toDate, String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	@Override
	public Event clone() {
		Event event = (Event)super.clone();
		event.setFromDate((Date)event.getFromDate().clone());
		event.setToDate((Date)event.getToDate().clone());
		return event;
	}

	/**
	 * Gets the from date.
	 *
	 * @return the from date
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * Sets the from date.
	 *
	 * @param fromDate	the new from date
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * Gets the to date.
	 *
	 * @return the to date
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * Sets the to date.
	 *
	 * @param toDate	the new to date
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}
}

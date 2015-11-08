/**
 *
 */
package main.model.taskModels;
import java.util.Date;

import main.model.EnumTypes;
import main.model.EnumTypes.TASK_TYPE;

/**
 * @@author Dalton
 *
 */
public class Event extends Task {
	private static final TASK_TYPE type = TASK_TYPE.EVENT;
	private Date fromDate;
	private Date toDate;

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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}
}

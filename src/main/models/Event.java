/**
 *
 */
package main.models;
import java.util.Date;

/**
 * @author Dalton
 *
 */
public class Event extends Task {
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
}

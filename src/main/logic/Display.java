package main.logic;

import main.model.ParsedObject;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import main.model.EnumTypes.TASK_TYPE;
import main.storage.Storage;

public class Display extends Command {

	private static Display display = null;
	private static Storage storage = Storage.getInstance();
	private TASK_TYPE taskType;

	private Display() {
	}

	public static Display getInstance(TASK_TYPE type) {
		if (display == null) {
			display = new Display();
		}
		display.taskType = type;
		return display;
	}

	public TASK_TYPE getTaskType() {
		return this.taskType;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		return true;
	}

	public List<List<Task>> process(ParsedObject obj) {
		//List<Task> result = new ArrayList<>();
		List<List<Task>> result = new ArrayList<List<Task>>();
		List<Task> tasks = storage.getAllTask(TASK_TYPE.ALL);
		List<Task> deadlines = new ArrayList();
		List<Task> events = new ArrayList();

		switch (obj.getCommandType()) {
		case DISPLAY_ALL:
			deadlines = storage.getAllTask(TASK_TYPE.DEADLINE);
			events = storage.getAllTask(TASK_TYPE.EVENT);
			message = "Displaying all tasks.";
			break;
		case DISPLAY_ON:
			for (Object dateObj : obj.getObjects()) {
				Date checkDate = (Date) dateObj;
				message = "Displaying all tasks on " + new SimpleDateFormat("EEE, dd MMM yyyy").format(checkDate) + ".";
				for (Task task : tasks) {
					//boolean isSatisfy = false;
					switch (task.getType()) {
					case EVENT:
						if (isOn(checkDate, ((Event) task).getFromDate())
								|| isOn(checkDate, ((Event) task).getToDate())) {
							//isSatisfy = true;
							events.add(task);
						}
						break;
					case DEADLINE:
						if (isOn(checkDate, ((Deadline) task).getDate())) {
							//isSatisfy = true;
							deadlines.add(task);
						}
						break;
					default:
						break;
					}
					/*if (isSatisfy) {
						result.add(task);
					}*/
				}
			}
			break;
		case DISPLAY_BETWEEN:
			Date fromDate = (Date) obj.getObjects().get(0);
			Date toDate = (Date) obj.getObjects().get(1);
			message = "Displaying all tasks between " + new SimpleDateFormat("EEE, dd MMM yyyy").format(fromDate) + " and " + new SimpleDateFormat("EEE, dd MMM yyyy").format(toDate) + ".";
			for (Task task : tasks) {
				//boolean isSatisfy = false;
				switch (task.getType()) {
				case EVENT:
					if (isBetween(fromDate, toDate, ((Event) task).getFromDate()) || isBetween(fromDate, toDate, ((Event) task).getToDate())) {
						//isSatisfy = true;
						events.add(task);
					}
					break;
				case DEADLINE:
					if (isBetween(fromDate, toDate, ((Deadline) task).getDate())) {
						//isSatisfy = true;
						deadlines.add(task);
					}
					break;
				default:
					break;
				}
				/*if (isSatisfy) {
					result.add(task);
				}*/
			}
			break;
		case INVALID:
			message = "Invalid parameters for display command.";
			return null;
		default:
			message = "No matching tasks found.";
			return null;
		}
		result.add(deadlines);
		result.add(events);

		return result;
	}

	private boolean isBetween(Date left, Date right, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date startDate = resetTime((Date)left.clone(), true);
		Date endDate = resetTime((Date)right.clone(), false);

		if (cur.compareTo(startDate) >= 0 && cur.compareTo(endDate) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isOn(Date date, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date comparedDate = resetTime((Date)date.clone(), true);

		if (currentDate.compareTo(comparedDate) == 0) {
			return true;
		} else {
			return false;
		}
	}

	private Date resetTime(Date d, boolean isStartOfDay) {
		Calendar date = Calendar.getInstance();
		date.setTime(d);
		if (isStartOfDay) {
			date.set(Calendar.HOUR_OF_DAY, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);
		} else {
			date.set(Calendar.HOUR_OF_DAY, 23);
			date.set(Calendar.MINUTE, 59);
			date.set(Calendar.SECOND, 59);
			date.set(Calendar.MILLISECOND, 999);
		}

		return date.getTime();
	}
}

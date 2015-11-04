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

import main.model.EnumTypes.PARAM_TYPE;
import main.model.EnumTypes.TASK_TYPE;
import main.storage.Storage;

/**
 * @@author Hiep
 *
 */
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
		List<Task> deadlines = new ArrayList<>();
		List<Task> events = new ArrayList<>();
		List<Task> todos = new ArrayList<>();

		if (obj.getParamType() != null) {
			List<Integer> ids = storage.getIdByCategory(obj.getObjects());
			for (int id : ids) {
				Task task = storage.getTaskByID(id);
				if (task != null) {
					switch (task.getType()) {
					case EVENT:
						events.add(task);
						break;
					case DEADLINE:
						deadlines.add(task);
						break;
					case TODO:
						todos.add(task);
						break;
					} 
						
				}
			}
			message = "Tasks are displayed.";
		} else {
			Date fromDate, toDate;
			switch (obj.getCommandType()) {
			case DISPLAY_ON:
				for (Object dateObj : obj.getObjects()) {
					Date checkDate = (Date) dateObj;
					message = "Displaying all tasks on " + new SimpleDateFormat("EEE, dd MMM yyyy").format(checkDate) + ".";
					for (Task task : tasks) {
						switch (task.getType()) {
						case EVENT:
							if (isOn(checkDate, ((Event) task).getFromDate())
									|| isOn(checkDate, ((Event) task).getToDate())) {
								events.add(task);
							}
							break;
						case DEADLINE:
							if (isOn(checkDate, ((Deadline) task).getDate())) {
								deadlines.add(task);
							}
							break;
						default:
							break;
						}
					}
				}
				break;
			case DISPLAY_ON_BETWEEN:
				fromDate = (Date) obj.getObjects().get(0);
				toDate = (Date) obj.getObjects().get(1);
				message = "Displaying all tasks between " + new SimpleDateFormat("EEE, dd MMM yyyy").format(fromDate) + " and " + new SimpleDateFormat("EEE, dd MMM yyyy").format(toDate) + ".";
				for (Task task : tasks) {
					switch (task.getType()) {
					case EVENT:
						if (isOnBetween(fromDate, toDate, ((Event) task).getFromDate()) || isBetween(fromDate, toDate, ((Event) task).getToDate())) {
							events.add(task);
						}
						break;
					case DEADLINE:
						if (isOnBetween(fromDate, toDate, ((Deadline) task).getDate())) {
							deadlines.add(task);
						}
						break;
					default:
						break;
					}
				}
				break;
			case DISPLAY_BETWEEN:
				fromDate = (Date) obj.getObjects().get(0);
				toDate = (Date) obj.getObjects().get(1);
				message = "Displaying all tasks between " + new SimpleDateFormat("EEE, dd MMM yyyy, h:mm a").format(fromDate) + " and " + new SimpleDateFormat("EEE, dd MMM yyyy, h:mm a").format(toDate) + ".";
				for (Task task : tasks) {
					switch (task.getType()) {
					case EVENT:
						if (isBetween(fromDate, toDate, ((Event) task).getFromDate()) || isBetween(fromDate, toDate, ((Event) task).getToDate())) {
							events.add(task);
						}
						break;
					case DEADLINE:
						if (isBetween(fromDate, toDate, ((Deadline) task).getDate())) {
							deadlines.add(task);
						}
						break;
					default:
						break;
					}
				}
				break;
			case INVALID:
				message = "Invalid parameters for display command.";
				return null;
			default:
				message = "No matching tasks found.";
				return null;
			}
			todos = storage.getAllTask(TASK_TYPE.TODO);
		}
		
		result.add(deadlines);
		result.add(events);
		result.add(todos);

		return result;
	}

	private boolean isOnBetween(Date left, Date right, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date startDate = resetTime((Date)left.clone(), true);
		Date endDate = resetTime((Date)right.clone(), false);

		if (cur.compareTo(startDate) >= 0 && cur.compareTo(endDate) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isBetween(Date left, Date right, Date cur) {
		return (left.compareTo(cur) <= 0 && cur.compareTo(right) <= 0);
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

/*
 *
 */
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

import main.model.EnumTypes;
import main.model.EnumTypes.TASK_TYPE;
import main.storage.Storage;

/**
 * The Class Display.
 *
 * @@author Hiep
 */
public class Display extends Command {

	private static Display display = null;
	private static Storage storage = null;
	private TASK_TYPE taskType;

	/**
	 * Instantiates a new display.
	 */
	private Display() {
		storage = Storage.getInstance();
	}

	/**
	 * Gets the single instance of Display.
	 *
	 * @param type		the type
	 * @return 			single instance of Display
	 */
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

	/**
	 * Process.
	 *
	 * @param obj	the obj
	 * @return 		the list
	 */
	public List<List<Task>> process(ParsedObject obj) {
		List<List<Task>> result = new ArrayList<List<Task>>();
		List<Task> tasks = storage.getAllTask(TASK_TYPE.ALL);
		List<Task> deadlines = new ArrayList<>();
		List<Task> events = new ArrayList<>();
		List<Task> todos = new ArrayList<>();

		if (obj.getParamType() != null && obj.getParamType() == EnumTypes.PARAM_TYPE.CATEGORY) {
			displayByCategories(obj, todos, events, deadlines);
		} else {
			if (!displayByTime(obj, tasks, todos, events, deadlines)) {
				return null;
			}
		}

		result.add(deadlines);
		result.add(events);
		result.add(todos);

		return result;
	}

	/**
	 * Display by categories.
	 *
	 * @param obj		the obj
	 * @param todos		the todos
	 * @param events	the events
	 * @param deadlines	the deadlines
	 */
	public void displayByCategories(ParsedObject obj, List<Task> todos, List<Task> events, List<Task> deadlines) {
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
	}

	/**
	 * Display by time.
	 *
	 * @param obj		the obj
	 * @param tasks		the tasks
	 * @param todos		the todos
	 * @param events	the events
	 * @param deadlines	the deadlines
	 * @return 			true, if successful
	 */
	public boolean displayByTime(ParsedObject obj, List<Task> tasks, List<Task> todos, List<Task> events, List<Task> deadlines) {
		Date fromDate, toDate;
		switch (obj.getCommandType()) {
		case DISPLAY_ON:
			message = "Displaying all tasks on ";
			for (int i = 0; i < obj.getObjects().size(); i++) {
				Date checkDate = (Date) obj.getObjects().get(i);
				message += new SimpleDateFormat("EEE, dd MMM yyyy").format(checkDate) + ((i < obj.getObjects().size()-1) ? ", " : "");

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
			message += ".";
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
			return false;
		default:
			message = "No matching tasks found.";
			return false;
		}
		todos = storage.getAllTask(TASK_TYPE.TODO);
		return true;
	}

	/**
	 * Checks if is on between.
	 *
	 * @param left	the left
	 * @param right	the right
	 * @param cur	the cur
	 * @return 		true, if is on between
	 */
	private boolean isOnBetween(Date left, Date right, Date cur) {
		Date startDate = resetTime((Date)left.clone(), true);
		Date endDate = resetTime((Date)right.clone(), false);

		if (cur.compareTo(startDate) >= 0 && cur.compareTo(endDate) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if is between.
	 *
	 * @param left	the left
	 * @param right	the right
	 * @param cur	the cur
	 * @return 		true, if is between
	 */
	private boolean isBetween(Date left, Date right, Date cur) {
		return (left.compareTo(cur) <= 0 && cur.compareTo(right) <= 0);
	}

	/**
	 * Checks if is on.
	 *
	 * @param date	the date
	 * @param cur	the cur
	 * @return 		true, if is on
	 */
	private boolean isOn(Date date, Date cur) {
		Date currentDate = resetTime((Date)cur.clone(), true);
		Date comparedDate = resetTime((Date)date.clone(), true);

		if (currentDate.compareTo(comparedDate) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reset time.
	 * @@author Dalton
	 *
	 * @param d				the d
	 * @param isStartOfDay	the is start of day
	 * @return 				the date
	 */
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

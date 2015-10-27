package main.logic;

import main.model.ParsedObject;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;

import java.util.ArrayList;
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

	public List<Task> process(ParsedObject obj) {
		List<Task> result = new ArrayList<>();
		List<Task> tasks = storage.getAllTask(obj.getTaskType());

		switch (obj.getCommandType()) {
		case DISPLAY_ON:
			for (Object dateObj : obj.getObjects()) {
				Date checkDate = (Date) dateObj;
				for (Task task : tasks) {
					boolean isSatisfy = false;
					switch (task.getType()) {
					case EVENT:
						if (isOn(checkDate, ((Event) task).getFromDate())
								|| isOn(checkDate, ((Event) task).getToDate())) {
							isSatisfy = true;
						}
						break;
					case DEADLINE:
						if (isOn(checkDate, ((Deadline) task).getDate())) {
							isSatisfy = true;
						}
						break;
					default:
						break;
					}
					if (isSatisfy) {
						result.add(task);
					}
				}
			}
			break;
		case DISPLAY_BETWEEN:
			Date fromDate = (Date) obj.getObjects().get(0);
			Date toDate = (Date) obj.getObjects().get(1);
			for (Task task : tasks) {
				boolean isSatisfy = false;
				switch (task.getType()) {
				case EVENT:
					if (isBetween(fromDate, toDate, ((Event) task).getFromDate()) || isBetween(fromDate, toDate, ((Event) task).getToDate())) {
						isSatisfy = true;
					}
					break;
				case DEADLINE:
					if (isBetween(fromDate, toDate, ((Deadline) task).getDate())) {
						isSatisfy = true;
					}
					break;
				default:
					break;
				}
				if (isSatisfy) {
					result.add(task);
				}
			}
			break;
		default:
			break;
		}
		return result;
	}

	private static boolean isBetween(Date left, Date right, Date cur) {
		return true;
	}

	private static boolean isOn(Date date, Date cur) {
		return true;
	}

}

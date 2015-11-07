package main.storage;

import java.util.Date;
import java.util.List;

import main.model.EnumTypes.CATEGORY;
import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;

public class TaskChecker {
	public static boolean isSatisfied(List<CATEGORY> categories, Task task) {
		Date curDate = new Date();
		for (CATEGORY category: categories) {
			switch (category) {
			case ALL: 
				break;	 
			case COMPLETED: 
				if (!task.isDone()) {
					return false;
				}
				break;
			case UNCOMPLETED:
				if (task.isDone()) {
					return false;
				}
				break;
			case EXPIRED:
				switch (task.getType()) {
				case TODO: 
					break;
				case EVENT:
					if (((Event)task).getToDate().compareTo(curDate) >= 0) {
						return false;
					}
					break;
				case DEADLINE:
					if (((Deadline)task).getDate().compareTo(curDate) >= 0) {
						return false;
					}
					break;
				default:
					return false;
				}
				break;
			case NONEXPIRED:
				switch (task.getType()) {
				case TODO: 
					break;
				case EVENT:
					if (((Event)task).getToDate().compareTo(curDate) < 0) {
						return false;
					}
					break;
				case DEADLINE:
					if (((Deadline)task).getDate().compareTo(curDate) < 0) {
						return false;
					}
					break;
				default:
					return false;
				}
				break;
			default: 
			}
		}
		return true;
	}
}

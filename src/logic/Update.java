/**
 *
 */
package logic;

import java.util.ArrayList;

import models.Deadline;
import models.Event;
import models.ParsedObject;
import models.Task;
import models.Todo;
import parser.MainParser;
import storage.Storage;

/**
 * @author Dalton
 *
 */
public class Update {
	private static final MainParser parser = MainParser.getInstance();

	public static boolean update(ParsedObject obj) {
		ArrayList<String> params = obj.getObjects();
		Task t = Storage.getTaskByID(parser.parseInteger(params.get(0)));
		if (t != null) {
			if (t instanceof Event) {
				return updateEvent((Event) t, params);
			} else if (t instanceof Todo) {
				return updateTodo((Todo) t, params);
			} else if (t instanceof Deadline) {
				return updateDeadline((Deadline) t, params);
			}
		}
		return false;
	}

	private static boolean updateEvent(Event evt, ArrayList<String> params) {
		switch (params.get(1)) {
			case "2":
				try {
					evt.setFromDate(parser.parseDates(params.get(2)).get(0));
				} catch (Exception e) {
					return false;
				}
				break;
			case "3":
				try {
					evt.setToDate(parser.parseDates(params.get(2)).get(0));
				} catch (Exception e) {
					return false;
				}
				break;
			case "4":
				evt.setTaskDesc(params.get(2));
				break;
			default:
				return false;
		}
		return true;
	}

	private static boolean updateTodo(Todo t, ArrayList<String> params) {
		switch (params.get(1)) {
			case "2":
				t.setTaskDesc(params.get(2));
				break;
			default:
				return false;
		}
		return true;
	}

	private static boolean updateDeadline(Deadline d, ArrayList<String> params) {
		switch (params.get(1)) {
			case "2":
				try {
					d.setDate(parser.parseDates(params.get(2)).get(0));
				} catch (Exception e) {
					return false;
				}
				break;
			case "3":
				d.setTaskDesc(params.get(2));
				break;
			default:
				return false;
		}
		return true;
	}
}

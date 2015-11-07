/**
 *
 */
package main.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.corba.se.impl.util.Version;

import main.model.EnumTypes;
import main.model.ParsedObject;
import main.model.VersionModel;
import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Deadline;
import main.model.taskModels.Event;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.parser.Parser;
import main.storage.Storage;

/**
 * @@author Dalton
 *
 */
public class Add extends Command {
	private static final Parser parser = Parser.getInstance();
	private static final Storage storage = Storage.getInstance();
	private static final VersionControl vControl = VersionControl.getInstance();
	private static Add add = null;

	private Add() {}

	public static Add getInstance() {
		if (add == null) {
			add = new Add();
		}
		return add;
	}

	@Override
	public boolean execute(ParsedObject obj) {
		assert obj != null;
		ArrayList<Task> tasks = obj.getObjects();
		if (!tasks.isEmpty()) {
			switch (obj.getTaskType()) {
				case SINGLE_DATE_EVENT:
					Event sEvt = (Event)tasks.get(0);
					storage.addTask(sEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);
					addNewTask(sEvt);
					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "\"" + sEvt.getTaskDesc() + "\" has been successfully added as an Event on " + parser.formatDate(sEvt.getFromDate(), "EEE, d MMM yyyy") + " at " + parser.formatDate(sEvt.getFromDate(), "h:mm a") + ".";

					return true;
				case DOUBLE_DATE_EVENT:
					Event dEvt = (Event)tasks.get(0);
					storage.addTask(dEvt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.EVENT);

					addNewTask(dEvt);

					taskType = EnumTypes.TASK_TYPE.EVENT;
					message = "\"" + dEvt.getTaskDesc() + "\" has been successfully added as an Event from " + parser.formatDate(dEvt.getFromDate(), "EEE, d MMM yyyy h:mm a") + " to " + parser.formatDate(dEvt.getToDate(), "EEE, d MMM yyyy h:mm a") + ".";

					return true;
				case TODO:
					Todo flt = (Todo)tasks.get(0);
					storage.addTask(flt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.TODO);
					addNewTask(flt);

					taskType = EnumTypes.TASK_TYPE.TODO;
					message = "\"" + flt.getTaskDesc() + "\" has been successfully added as a Todo task.";
					return true;
				case DEADLINE:
					Deadline dt = (Deadline)tasks.get(0);
					storage.addTask(dt);
					storage.saveTaskType(EnumTypes.TASK_TYPE.DEADLINE);

					addNewTask(dt);

					taskType = EnumTypes.TASK_TYPE.DEADLINE;
					Date d = dt.getDate();
					message = "\"" + dt.getTaskDesc() + "\" has been successfully added as a Deadline task that must be completed by " + parser.formatDate(d, "h:mm aa") + " on " + parser.formatDate(d, "EEE, d MMM yyyy") + ".";

					return true;
				default:
					taskType = EnumTypes.TASK_TYPE.INVALID;
					message = "Add command has failed.";
					return false;
			}
		}
		
		taskType = EnumTypes.TASK_TYPE.INVALID;
		message = "Add command has failed.";
		return false;
	}

	public boolean undo(Task task) {
		return storage.delete(task.getTaskID());
	}

	public boolean redo(Task task) {
		return storage.addTask(task);
	}
	
	private void addNewTask(Task task) {
		vControl.addNewData(new VersionModel.AddModel(task));
	}
}

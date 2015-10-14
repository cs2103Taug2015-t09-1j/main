/**
 *
 */
package models;

import java.util.ArrayList;
import models.Commands.*;

/**
 * @author Dalton
 *
 */
public class ParsedObject {
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	private ArrayList objects;

	public ParsedObject() {
	}

	public ParsedObject(COMMAND_TYPE commandType, TASK_TYPE taskType, ArrayList objects) {
		this.commandType = commandType;
		this.taskType = taskType;
		this.objects = objects;
	}

	/**
	 * @return the commandType
	 */
	public COMMAND_TYPE getCommandType() {
		return commandType;
	}

	/**
	 * @param commandType the commandType to set
	 */
	public void setCommandType(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	/**
	 * @return the taskType
	 */
	public TASK_TYPE getTaskType() {
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}

	/**
	 * @return the tasks
	 */
	public ArrayList getObjects() {
		return objects;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(ArrayList objects) {
		this.objects = objects;
	}
}

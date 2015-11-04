/**
 *
 */
package main.model;

import java.util.ArrayList;

import main.model.EnumTypes.*;

/**
 * @@author Dalton
 *
 */
public class ParsedObject {
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	private ArrayList objects;
	private PARAM_TYPE paramType = PARAM_TYPE.ID;

	public ParsedObject() {}

	public ParsedObject(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	public ParsedObject(COMMAND_TYPE commandType, PARAM_TYPE paramType, ArrayList objects) {
		this.commandType = commandType;
		this.paramType = paramType;
		this.objects = objects;
	}

	public ParsedObject(COMMAND_TYPE commandType, PARAM_TYPE paramType, TASK_TYPE taskType, ArrayList objects) {
		this.commandType = commandType;
		this.taskType = taskType;
		this.objects = objects;
		this.paramType = paramType;
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

	public PARAM_TYPE getParamType() {
		return this.paramType;
	}

	public void setParamType(PARAM_TYPE paramType) {
		this.paramType = paramType;
	}
}

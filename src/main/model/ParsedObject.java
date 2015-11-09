/**
 *
 */
package main.model;

import java.util.ArrayList;

import main.model.EnumTypes.*;

/**
 * The Class ParsedObject.
 * The object that is created to store all the information in order to execute the commands
 * after the command has been parsed by the Parser.
 *
 * @@author Dalton
 */
public class ParsedObject {
	private COMMAND_TYPE commandType;
	private TASK_TYPE taskType;
	private ArrayList objects;
	private PARAM_TYPE paramType;

	/**
	 * Instantiates a new parsed object.
	 */
	public ParsedObject() {}

	/**
	 * Instantiates a new parsed object.
	 *
	 * @param commandType	the command type
	 */
	public ParsedObject(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	/**
	 * Instantiates a new parsed object.
	 *
	 * @param commandType	the command type
	 * @param paramType		the param type
	 * @param objects		the objects
	 */
	public ParsedObject(COMMAND_TYPE commandType, PARAM_TYPE paramType, ArrayList objects) {
		this.commandType = commandType;
		this.paramType = paramType;
		this.objects = objects;
	}

	/**
	 * Instantiates a new parsed object.
	 *
	 * @param commandType	the command type
	 * @param paramType		the param type
	 * @param taskType		the task type
	 * @param objects		the objects
	 */
	public ParsedObject(COMMAND_TYPE commandType, PARAM_TYPE paramType, TASK_TYPE taskType, ArrayList objects) {
		this.commandType = commandType;
		this.taskType = taskType;
		this.objects = objects;
		this.paramType = paramType;
	}


	/**
	 * Gets the command type.
	 *
	 * @return the command type
	 */
	public COMMAND_TYPE getCommandType() {
		return commandType;
	}

	/**
	 * Sets the command type.
	 *
	 * @param commandType	the new command type
	 */
	public void setCommandType(COMMAND_TYPE commandType) {
		this.commandType = commandType;
	}

	/**
	 * Gets the task type.
	 *
	 * @return the task type
	 */
	public TASK_TYPE getTaskType() {
		return taskType;
	}

	/**
	 * Sets the task type.
	 *
	 * @param taskType	the new task type
	 */
	public void setTaskType(TASK_TYPE taskType) {
		this.taskType = taskType;
	}

	/**
	 * Gets the objects.
	 *
	 * @return the objects
	 */
	public ArrayList getObjects() {
		return objects;
	}

	/**
	 * Sets the tasks.
	 *
	 * @param objects	the new tasks
	 */
	public void setTasks(ArrayList objects) {
		this.objects = objects;
	}

	/**
	 * Gets the param type.
	 *
	 * @return the param type
	 */
	public PARAM_TYPE getParamType() {
		return this.paramType;
	}

	/**
	 * Sets the param type.
	 *
	 * @param paramType	the new param type
	 */
	public void setParamType(PARAM_TYPE paramType) {
		this.paramType = paramType;
	}
}

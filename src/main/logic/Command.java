/**
 *
 */
package main.logic;

import main.model.EnumTypes;
import main.model.ParsedObject;

/**
 * The Class Command.
 * Parent class of all command classes.
 *
 * @@author Dalton
 */
public abstract class Command {
	protected EnumTypes.TASK_TYPE taskType;
	protected String message = "";

	/**
	 * Execute.
	 *
	 * @param obj	the obj
	 * @return 		true, if successful
	 */
	public abstract boolean execute(ParsedObject obj);

	/**
	 * Gets the feedback message.
	 *
	 * @return the feedback message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets the task type.
	 *
	 * @return the task type
	 */
	public EnumTypes.TASK_TYPE getTaskType() {
		return this.taskType;
	}
}

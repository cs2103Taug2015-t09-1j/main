/**
 *
 */
package logic;

import models.EnumTypes;
import models.ParsedObject;

/**
 * @author Dalton
 *
 */
public abstract class Command {
	protected EnumTypes.TASK_TYPE taskType;
	protected String message;

	public abstract boolean execute(ParsedObject obj);

	public String getMessage() {
		return this.message;
	}

	public EnumTypes.TASK_TYPE getTaskType() {
		return this.taskType;
	}
}

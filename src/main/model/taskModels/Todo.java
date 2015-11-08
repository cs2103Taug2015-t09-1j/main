/**
 *
 */
package main.model.taskModels;

import main.model.EnumTypes.TASK_TYPE;

/**
 * @@author Dalton
 *
 */
public class Todo extends Task {
	private static final TASK_TYPE type = TASK_TYPE.TODO;

	public Todo(String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}

}

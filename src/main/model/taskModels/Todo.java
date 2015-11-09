/**
 *
 */
package main.model.taskModels;

import main.model.EnumTypes.TASK_TYPE;

/**
 * The Class Todo.
 *
 * @@author Dalton
 */
public class Todo extends Task {
	private static final TASK_TYPE type = TASK_TYPE.TODO;

	/**
	 * Instantiates a new todo.
	 *
	 * @param taskDesc	the task desc
	 * @param isDone	the is done
	 */
	public Todo(String taskDesc, boolean isDone) {
		super(taskDesc, isDone);
	}

	@Override
	public TASK_TYPE getType() {
		return type;
	}

}

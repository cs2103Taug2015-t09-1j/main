/**
 *
 */
package models;

/**
 * @author Dalton
 *
 */
public class Commands {
	public static enum COMMAND_TYPE {
		ADD, DELETE, UPDATE, SEARCH, DISPLAY, UNDO, INVALID
	};

	public static enum TASK_TYPE {
		SINGLE_DATE_EVENT, DOUBLE_DATE_EVENT, EVENT, DEADLINE_TASK, FLOATING_TASK, ALL, INVALID
	};
}
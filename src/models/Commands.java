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
		ADD, DELETE, UPDATE, SEARCH, UNDO, INVALID
	};

	public static enum ADD_TYPE {
		SINGLE_DATE_EVENT, DOUBLE_DATE_EVENT, DEADLINE_TASK, FLOATING_TASK, INVALID
	};
}

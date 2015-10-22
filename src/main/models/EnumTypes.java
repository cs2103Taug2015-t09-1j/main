/**
 *
 */
package main.models;

/**
 * @author Dalton
 *
 */
public class EnumTypes {
	public static enum COMMAND_TYPE {
		ADD, DELETE, UPDATE, SEARCH, DISPLAY, UNDO, REDO, EXIT, INVALID
	};

	public static enum TASK_TYPE {
		SINGLE_DATE_EVENT, DOUBLE_DATE_EVENT, EVENT, DEADLINE, TODO, ALL, INVALID
	};


}
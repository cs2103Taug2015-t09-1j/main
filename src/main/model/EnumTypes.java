/**
 *
 */
package main.model;

/**
 * The Class EnumTypes.
 * Contains all the ENUMs that are used to determine the different types.
 *
 * @@author Dalton
 */
public class EnumTypes {

	public static enum COMMAND_TYPE {
		ADD, DELETE, UPDATE, SEARCH, DISPLAY, UNDO, REDO, EXIT, INVALID, DONE, UNDONE, DONE_UNDONE, DISPLAY_ON, DISPLAY_BETWEEN, DISPLAY_ON_BETWEEN, DISPLAY_ALL
	};

	public static enum TASK_TYPE {
		SINGLE_DATE_EVENT, DOUBLE_DATE_EVENT, EVENT, DEADLINE, TODO, ALL, INVALID
	};

	public static enum PARAM_TYPE {
		ID, CATEGORY, TIME, TASK, DATE, INTEGER, STRING
	};

	public static enum CATEGORY {
		COMPLETED, UNCOMPLETED, EXPIRED, NONEXPIRED, ALL
	};

}
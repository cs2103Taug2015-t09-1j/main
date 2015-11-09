/*
 *
 */
package main.model;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Task;

/**
 * The Class ObserverEvent.
 *
 * @@author Hiep
 */
public class ObserverEvent {
	public static int CHANGE_MESSAGE_CODE = 0;
	public static int CHANGE_TABLE_CODE = 1;
	public static int CHANGE_USER_INPUT_CODE = 2;

	private int code;
	private Object payload;

	/**
	 * Instantiates a new observer event.
	 *
	 * @param code
	 *            the code
	 * @param payload
	 *            the payload
	 */
	public ObserverEvent(int code, Object payload) {
		this.code = code;
		this.payload = payload;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Gets the payload.
	 *
	 * @return the payload
	 */
	public Object getPayload() {
		return this.payload;
	}

	/**
	 * The Class EInput.
	 *
	 * @@author Hiep
	 */
	public static class EInput {
		private String command;

		/**
		 * Instantiates a new e input.
		 *
		 * @param command
		 *            the command
		 */
		public EInput(String command) {
			this.command = command;
		}

		/**
		 * Gets the command.
		 *
		 * @return the command
		 */
		public String getCommand() {
			return this.command;
		}
	}

	/**
	 * The Class EMessage.
	 *
	 * @@author Hiep
	 */
	public static class EMessage {
		private String message;

		/**
		 * Instantiates a new e message.
		 *
		 * @param message
		 *            the message
		 */
		public EMessage(String message) {
			this.message = message;
		}

		/**
		 * Gets the message.
		 *
		 * @return the message
		 */
		public String getMessage() {
			return this.message;
		}
	}

	/**
	 * The Class ETasks.
	 *
	 * @@author Hiep
	 */
	public static class ETasks {
		private List<Task> tasks = new ArrayList<>();
		private TASK_TYPE taskType;
		private boolean shouldSwitch = false;

		/**
		 * Instantiates a new e tasks.
		 *
		 * @param tasks
		 *            the tasks
		 * @param taskType
		 *            the task type
		 * @param shouldSwitch
		 *            the should switch
		 */
		public ETasks(List<Task> tasks, TASK_TYPE taskType, boolean shouldSwitch) {
			this.taskType = taskType;
			this.tasks = tasks;
			this.shouldSwitch = shouldSwitch;
		}

		/**
		 * Gets the tasks.
		 *
		 * @return the tasks
		 */
		public List<Task> getTasks() {
			return this.tasks;
		}

		/**
		 * Gets the task type.
		 *
		 * @return the task type
		 */
		public TASK_TYPE getTaskType() {
			return this.taskType;
		}

		/**
		 * Should switch.
		 *
		 * @return true, if successful
		 */
		public boolean shouldSwitch() {
			return this.shouldSwitch;
		}
	}
}

package main.model;

import static main.model.EnumTypes.TASK_TYPE.*;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Task;

public class ObserverEvent {
	public static int CHANGE_MESSAGE_CODE = 0;
	public static int CHANGE_TABLE_CODE = 1;
	public static int CHANGE_USER_INPUT_CODE = 2;

	private int code;
	private Object payload;

	public ObserverEvent(int code, Object payload) {
		this.code = code;
		this.payload = payload;
	}

	public int getCode() {
		return this.code;
	}

	public Object getPayload() {
		return this.payload;
	}

	public static class EInput {
		private String command;

		public EInput(String command) {
			this.command = command;
		}

		public String getCommand() {
			return this.command;
		}
	}

	public static class EMessage {
		private String message;

		public EMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return this.message;
		}
	}

	public static class ETasks {
		private List<Task> tasks = new ArrayList<>();
		private TASK_TYPE taskType;

		public ETasks(List<Task> tasks, TASK_TYPE taskType) {
			this.taskType = taskType;
			this.tasks = tasks;
		}

		public List<Task> getTasks() {
			return this.tasks;
		}

		public TASK_TYPE getTaskType() {
			return this.taskType;
		}
	}
}

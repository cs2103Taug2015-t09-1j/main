package main.model;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes.COMMAND_TYPE;
import main.model.taskModels.Task;

//@@author Hiep
public abstract class VersionModel {
	private final COMMAND_TYPE cmdType;

	public COMMAND_TYPE getCmdType() {
		return this.cmdType;
	}

	public VersionModel(COMMAND_TYPE cmdType) {
		this.cmdType = cmdType;
	}

	public static class AddModel extends VersionModel{
		private Task task = null;
		public AddModel(Task task) {
			super(COMMAND_TYPE.ADD);
			this.task = task;
		}
		public Task getTask() {
			return this.task;
		}
	}

	public static class DeleteModel extends VersionModel {

		private List<Task> tasks = new ArrayList<>();

		public DeleteModel(List<Task> tasks) {
			super(COMMAND_TYPE.DELETE);
			this.tasks = tasks;
		}

		public List<Task> getTasks() {
			return this.tasks;
		}

	}

	public static class UpdateModel extends VersionModel {

		private Task oldTask = null, newTask = null;

		public UpdateModel(Task oldTask, Task newTask) {
			super(COMMAND_TYPE.UPDATE);
			this.oldTask = oldTask;
			this.newTask = newTask;
		}

		public Task getOldTask() {
			return this.oldTask;
		}

		public Task getNewTask() {
			return this.newTask;
		}
	}

	public static class ChangeStatusModel extends VersionModel {

		private List<Integer> ids = new ArrayList<>();
		private List<Boolean> oldStatuses = new ArrayList<>();
		private boolean newStatus = true;

		public ChangeStatusModel(List<Integer> ids, List<Boolean> oldStatuses, boolean newStatus) {
			super(COMMAND_TYPE.DONE_UNDONE);
			this.ids = ids;
			this.oldStatuses = oldStatuses;
			this.newStatus = newStatus;
		}

		public List<Integer> getIds() {
			return this.ids;
		}

		public List<Boolean> getOldStatuses() {
			return this.oldStatuses;
		}

		public boolean getNewStatus() {
			return this.newStatus;
		}
	}

}

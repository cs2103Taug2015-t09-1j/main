/*
 *
 */
package main.model;

import java.util.ArrayList;
import java.util.List;

import main.model.EnumTypes.COMMAND_TYPE;
import main.model.taskModels.Task;

/**
 * The Class VersionModel.
 *
 * @@author Hiep
 */
public abstract class VersionModel {
	private final COMMAND_TYPE cmdType;

	/**
	 * Gets the cmd type.
	 *
	 * @return the cmd type
	 */
	public COMMAND_TYPE getCmdType() {
		return this.cmdType;
	}

	/**
	 * Instantiates a new version model.
	 *
	 * @param cmdType
	 *            the cmd type
	 */
	public VersionModel(COMMAND_TYPE cmdType) {
		this.cmdType = cmdType;
	}

	/**
	 * The Class AddModel.
	 *
	 * @@author Hiep
	 */
	public static class AddModel extends VersionModel{
		private Task task = null;

		/**
		 * Instantiates a new adds the model.
		 *
		 * @param task
		 *            the task
		 */
		public AddModel(Task task) {
			super(COMMAND_TYPE.ADD);
			this.task = task;
		}

		/**
		 * Gets the task.
		 *
		 * @return the task
		 */
		public Task getTask() {
			return this.task;
		}
	}

	/**
	 * The Class DeleteModel.
	 *
	 * @@author Hiep
	 */
	public static class DeleteModel extends VersionModel {

		private List<Task> tasks = new ArrayList<>();

		/**
		 * Instantiates a new delete model.
		 *
		 * @param tasks
		 *            the tasks
		 */
		public DeleteModel(List<Task> tasks) {
			super(COMMAND_TYPE.DELETE);
			this.tasks = tasks;
		}

		/**
		 * Gets the tasks.
		 *
		 * @return the tasks
		 */
		public List<Task> getTasks() {
			return this.tasks;
		}

	}

	/**
	 * The Class UpdateModel.
	 *
	 * @@author Hiep
	 */
	public static class UpdateModel extends VersionModel {

		private Task oldTask = null, newTask = null;

		/**
		 * Instantiates a new update model.
		 *
		 * @param oldTask
		 *            the old task
		 * @param newTask
		 *            the new task
		 */
		public UpdateModel(Task oldTask, Task newTask) {
			super(COMMAND_TYPE.UPDATE);
			this.oldTask = oldTask;
			this.newTask = newTask;
		}

		/**
		 * Gets the old task.
		 *
		 * @return the old task
		 */
		public Task getOldTask() {
			return this.oldTask;
		}

		/**
		 * Gets the new task.
		 *
		 * @return the new task
		 */
		public Task getNewTask() {
			return this.newTask;
		}
	}

	/**
	 * The Class ChangeStatusModel.
	 *
	 * @@author Hiep
	 */
	public static class ChangeStatusModel extends VersionModel {

		private List<Integer> ids = new ArrayList<>();
		private List<Boolean> oldStatuses = new ArrayList<>();
		private boolean newStatus = true;

		/**
		 * Instantiates a new change status model.
		 *
		 * @param ids
		 *            the ids
		 * @param oldStatuses
		 *            the old statuses
		 * @param newStatus
		 *            the new status
		 */
		public ChangeStatusModel(List<Integer> ids, List<Boolean> oldStatuses, boolean newStatus) {
			super(COMMAND_TYPE.DONE_UNDONE);
			this.ids = ids;
			this.oldStatuses = oldStatuses;
			this.newStatus = newStatus;
		}

		/**
		 * Gets the ids.
		 *
		 * @return the ids
		 */
		public List<Integer> getIds() {
			return this.ids;
		}

		/**
		 * Gets the old statuses.
		 *
		 * @return the old statuses
		 */
		public List<Boolean> getOldStatuses() {
			return this.oldStatuses;
		}

		/**
		 * Gets the new status.
		 *
		 * @return the new status
		 */
		public boolean getNewStatus() {
			return this.newStatus;
		}
	}

}

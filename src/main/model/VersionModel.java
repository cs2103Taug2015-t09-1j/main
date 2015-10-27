package main.model;

import main.model.EnumTypes.COMMAND_TYPE;
import main.model.taskModels.Task;

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

}

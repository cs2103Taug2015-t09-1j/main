/**
 *
 */
package models;

import java.util.ArrayList;

/**
 * @author Dalton
 *
 */
public class ParsedObject {
	private String command;
	private String commandType;
	private ArrayList<String> commandInfo;
	private String message;

	public ParsedObject() {
	}

	public ParsedObject(String command, String commandType, ArrayList<String> commandInfo, String message) {
		this.command = command;
		this.commandType = commandType;
		this.commandInfo = commandInfo;
		this.message = message;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public ArrayList<String> getCommandInfo() {
		return commandInfo;
	}

	public void setCommandInfo(ArrayList<String> commandInfo) {
		this.commandInfo = commandInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

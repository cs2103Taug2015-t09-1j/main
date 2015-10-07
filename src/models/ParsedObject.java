/**
 *
 */
package models;

/**
 * @author Dalton
 *
 */
public class ParsedObject {
	private Task t;
	private String type;
	private String msg;

	public ParsedObject(Task t, String type, String msg) {
		this.t = t;
		this.type = type;
		this.msg = msg;
	}

	public Task getTask() {
		return t;
	}

	public void setTask(Task t) {
		this.t = t;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

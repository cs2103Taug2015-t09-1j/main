/**
 *
 */
package main.ui;

import java.util.ArrayList;

import main.logic.Logic;

/**
 * @author Dalton
 *
 */
public class InputHistory {
	private static InputHistory inputHistory = null;
	private static int pointer;
	private static ArrayList<String> history;

	private InputHistory() {}

	public static InputHistory getInstance() {
		if (inputHistory == null) {
			inputHistory = new InputHistory();
			history = new ArrayList<String>();
			pointer = 0;
		}
		return inputHistory;
	}

	public void addInputHistory(String input) {
		if (!input.trim().isEmpty()) {
			history.add(input);
			pointer = history.size();
		}
	}

	public String getPreviousInput() {
		if (history.size() > 0 && pointer > 0) {
			pointer -= 1;
		}
		return history.get(pointer);
	}

	public String getNextInput() {
		if (history.size() > 0 && pointer < history.size()-1) {
			pointer += 1;
		}
		return history.get(pointer);
	}
}

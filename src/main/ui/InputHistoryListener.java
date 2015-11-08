/**
 *
 */
package main.ui;

import java.util.ArrayList;

import main.logic.Logic;

/**
 * @@author Dalton
 *
 */
public class InputHistoryListener {
	private static InputHistoryListener inputHistory = null;
	private static ArrayList<String> history = null;
	private static int pointer;

	private InputHistoryListener() { }

	public static InputHistoryListener getInstance() {
		if (inputHistory == null) {
			inputHistory = new InputHistoryListener();
			history = new ArrayList<String>();
			pointer = 0;
		}
		return inputHistory;
	}

	public void saveInputHistory(String input) {
		if (!input.trim().isEmpty()) {
			history.add(input);
			pointer = history.size();
		}
	}

	public String getPreviousInput() {
		if (history.size() > 0 && pointer > 0) {
			pointer -= 1;
			return history.get(pointer);
		}
		return null;
	}

	public String getNextInput() {
		if (history.size() > 0 && pointer < history.size()-1) {
			pointer += 1;
			return history.get(pointer);
		} else {
			return null;
		}

	}
}

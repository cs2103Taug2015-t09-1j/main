/**
 *
 */
package main.ui;

import java.util.ArrayList;

import main.logic.Logic;

/**
 * The Class InputHistoryHandler. 
 * Stores and handles the history of user inputs.
 *
 * @@author Dalton
 */
public class InputHistoryHandler {
	private static InputHistoryHandler inputHistory = null;
	private static ArrayList<String> history = null;
	private static int pointer;

	private InputHistoryHandler() { }

	/**
	 * Gets the single instance of InputHistoryHandler.
	 *
	 * @return 	single instance of InputHistoryHandler
	 */
	public static InputHistoryHandler getInstance() {
		if (inputHistory == null) {
			inputHistory = new InputHistoryHandler();
			history = new ArrayList<String>();
			pointer = 0;
		}
		return inputHistory;
	}

	/**
	 * Saves the current input and shifts the pointer to prepare for the next input.
	 *
	 * @param input		the current input of the user
	 */
	public void saveInputHistory(String input) {
		if (!input.trim().isEmpty()) {
			history.add(input);
			pointer = history.size();
		}
	}

	/**
	 * Shifts the pointer backward by 1 index if possible and return the current value which
	 * contains the previous user input.
	 *
	 * @return 	the previous input of the user
	 */
	public String getPreviousInput() {
		if (history.size() > 0 && pointer > 0) {
			pointer -= 1;
			return history.get(pointer);
		}
		return null;
	}

	/**
	 * Shifts the pointer forward by 1 index if possible and return the current value which
	 * contains the next user input.
	 *
	 * @return 	the next input of the user
	 */
	public String getNextInput() {
		if (history.size() > 0 && pointer < history.size()-1) {
			pointer += 1;
			return history.get(pointer);
		} else {
			return null;
		}

	}
}

/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.TextArea;
import java.util.Observable;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import main.parser.Parser;
import main.storage.LogFileHandler;

/**
 * The listener for receiving inputFeedback events.
 * Detects and highlights specific text input by the user and returns feedback.
 *
 * @@author Dalton
 */
public class InputFeedbackListener {
	private JTextPane tpInput = null;
	private JTextArea taMessage = null;
	private static Parser parser = null;
	private static InputFeedbackListener feedbackListener = null;
	private static Pattern pattern = null;
	private static Matcher matcher = null;
	private static final Logger logger = Logger.getLogger(InputFeedbackListener.class.getName());

	private static final Color NORMAL_FONT_COLOUR = Color.BLACK;
	private static final Color KEYWORD_FONT_COLOUR = Color.decode("#19D14A");
	private static final Color WARNING_FONT_COLOUR = Color.decode("#FF5757");

	private static final SimpleAttributeSet defaultSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet keywordSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet warningSet = new SimpleAttributeSet();

	private static final String[] commandSyntaxPrompt = {"Command aliases: update, modify, edit, /u, /m, /e  change     Legend: | means OR, * means OPTIONAL\n"
														+ "Command syntax: update {taskID} {columnID} {value}",
														  "Command aliases: delete, del, remove, /d, /r     Legend: | means OR, * means OPTIONAL\n"
														+ "Command syntax: delete {taskID} *((to|-) {taskID}) | *(!)expired | *(!)completed",
														  "Command aliases: display, show, view, /sh, /v     Legend: | means OR, * means OPTIONAL\n"
														+ "Command syntax: display {date} *(to {date}) | time {date time} to {date time} | *(!)expired | *(!)completed",
														  "Command aliases: undo, back     Legend: | means OR, * means OPTIONAL\n"
														+ "Command syntax: undo *{numberOfCommands}",
														  "Command aliases: redo, forward     Legend: | means OR, * means OPTIONAL\n"
														+ "Command syntax: redo *{numberOfCommands}",
														  "Command aliases: exit, quit\n"
														+ "Command syntax: exit",
														  "Command aliases: undone, !done, incomplete     Legend: | means OR, * means OPTIONAL\n"
														+ "Command Syntax: undone {taskID} *((to|-) {taskID}) | *(!)expired | *(!)completed",
														  "Command aliases: done, complete     Legend: | means OR, * means OPTIONAL\n"
														+ "Command syntax: done {taskID} *((to|-) {taskID}) | *(!)expired | *(!)completed"};

	private static final String TIME_REGEX = "\\s*(((\\d+\\s+(minutes|min|seconds|sec|hours))|[0-9](am|pm|a.m.|p.m.)?|1[0-2](am|pm|a.m.|p.m.)?)|"
											+ "(0[0-9]|1[0-9]|2[0-3])\\:?([0-5][0-9]))\\:?([0-5][0-9])?(am|pm|a.m.|p.m.|h|\\shours)?\\s*";
	private static final String TIME_MESSAGE = "Numbers in the Task Description may be parsed as dates. "
											+ "Surround your Task Description with double quotes if you do not want it to be parsed. (e.g. \"Lunch at 18 Chefs with John\" from 12 to 1pm)";

	private static final String MONTH_REGEX = "\\s*(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|"
        									+ "Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?)\\s*";
	private static final String MONTH_MESSAGE = "Months in the Task Description may be parsed as dates. "
											+ "Surround your Task Description with double quotes if you do not want it to be parsed. (e.g. \"Lunch with April\" from 12 to 1pm)";

	private static final String DAY_REGEX = "\\s*(?:(Mon|Tue(?:s)|Wed(?:nes)|Thur(?:s)|Fri|Sat(?:ur)|Sun)(?:day)?)\\s*";
	private static final String DAY_MESSAGE = "Days in the Task Description may be parsed as dates. "
											+ "Surround your Task Description with double quotes if you do not want it to be parsed. (e.g. \"Lunch with Wednesday\" from 12 to 1pm)";

	/**
	 * Instantiates a new input feedback listener and sets up the logger.
	 */
	private InputFeedbackListener() {
		parser = Parser.getInstance();
		StyleConstants.setForeground(defaultSet, NORMAL_FONT_COLOUR);
		StyleConstants.setForeground(keywordSet, KEYWORD_FONT_COLOUR);
		StyleConstants.setForeground(warningSet, WARNING_FONT_COLOUR);
		LogFileHandler.getInstance().addLogFileHandler(logger);
	}

	/**
	 * Gets the single instance of InputFeedbackListener.
	 *
	 * @return single instance of InputFeedbackListener
	 */
	public static InputFeedbackListener getInstance() {
		if (feedbackListener == null) {
			feedbackListener = new InputFeedbackListener();
		}
		return feedbackListener;
	}

	/**
	 * Setup References to the UI components.
	 *
	 * @param tpInput		reference of tpInput
	 * @param taMessage		reference of taMessage
	 */
	public void setupReferences(JTextPane tpInput, JTextArea taMessage) {
		this.tpInput = tpInput;
		this.taMessage = taMessage;
	}

	/**
	 * Highlight text of matching cases.
	 */
	public void highlightText() {
		String input = tpInput.getText();
        tpInput.getStyledDocument().setCharacterAttributes(0, input.length(), defaultSet, true);

        if (!input.isEmpty()) {
        	taMessage.setText(null);
        }

        String[][] commandArrays = getCommandArrays();

        highlightCommandKeyword(commandArrays, input);
        highlightWarningCases(input, TIME_REGEX, TIME_MESSAGE);
        highlightWarningCases(input, MONTH_REGEX, MONTH_MESSAGE);
        highlightWarningCases(input, DAY_REGEX, DAY_MESSAGE);
    }

	/**
	 * Search through the input string and highlight any keywords matching to the commands in the command arrays.
	 *
	 * @param commandArrays	the array of command arrays kept by the Parser
	 * @param input			the input
	 */
	private void highlightCommandKeyword(String[][] commandArrays, String input) {
		for (int i = 0; i < commandArrays.length; i++) {
        	for (String keyword : commandArrays[i]) {
        		pattern = Pattern.compile("(?ui)^" + keyword + "\\s+");
                matcher = pattern.matcher(input);
                while (matcher.find()) {
                	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), keyword.length(), keywordSet, true);
                	taMessage.setText(commandSyntaxPrompt[i]);
                }
        	}
        }
	}

	/**
	 * Retrieve command arrays from the Parser.
	 *
	 * @return 	array of command arrays
	 */
	private String[][] getCommandArrays() {
		return new String[][] {parser.getCommandKeywordList("update"), parser.getCommandKeywordList("delete"),
								parser.getCommandKeywordList("display"), parser.getCommandKeywordList("undo"),
								parser.getCommandKeywordList("redo"), parser.getCommandKeywordList("exit"),
								parser.getCommandKeywordList("undone"), parser.getCommandKeywordList("done")};
	}

	/**
	 * Highlight warning cases.
	 *
	 * @param input		the user input
	 * @param regex		the regex for parsing
	 * @param message	the message to display to the user
	 */
	private void highlightWarningCases(String input, String regex, String message) {
		pattern = Pattern.compile("(?ui)" + regex);
        matcher = pattern.matcher(input);
        while (matcher.find()) {
        	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.group().length(), warningSet, true);
        	taMessage.setText(message);
        }
	}
}

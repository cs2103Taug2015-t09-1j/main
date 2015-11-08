/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.TextArea;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import main.parser.Parser;

/**
 * @author Dalton
 *
 */
public class InputFeedbackHandler {
	private JTextPane tpInput = null;
	private JTextArea taMessage = null;
	private static Parser parser = null;
	private static InputFeedbackHandler feedbackHandler = null;
	private static Pattern pattern = null;
	private static Matcher matcher = null;
	private static final Logger logger = Logger.getLogger(InputFeedbackHandler.class.getName());

	private static final Color NORMAL_FONT_COLOUR = Color.BLACK;
	private static final Color KEYWORD_FONT_COLOUR = Color.decode("#19D14A");
	private static final Color WARNING_FONT_COLOUR = Color.decode("#FF5757");

	private static final SimpleAttributeSet defaultSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet keywordSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet warningSet = new SimpleAttributeSet();

	private static final String[] commandSyntaxPrompt = {"Command aliases: update, modify, edit, /u, /m, /e  change\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): update {taskID} {columnID} {value}",
														  "Command aliases: delete, del, remove, /d, /r\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): delete {taskID} *((to|-) {taskID})",
														  "Command aliases: display, show, view, /sh, /v (| means OR, * means OPTIONAL )\n"
														+ "Command syntax: display (expired|!expired|completed|!completed) | {date} *(to {date})",
														  "Command aliases: undo, back\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): undo *{numberOfCommands}",
														  "Command aliases: redo, forward\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): redo *{numberOfCommands}",
														  "Command aliases: exit, quit\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): exit",
														  "Command aliases: undone, !done, incomplete\n"
														+ "Command Syntax( | means OR, * means OPTIONAL ): undone {taskID} *((to|-) {taskID})",
														  "Command aliases: done, complete\n"
														+ "Command syntax( | means OR, * means OPTIONAL ): done {taskID} *((to|-) {taskID})"};

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

	private InputFeedbackHandler() {
		parser = Parser.getInstance();
		StyleConstants.setForeground(defaultSet, NORMAL_FONT_COLOUR);
		StyleConstants.setForeground(keywordSet, KEYWORD_FONT_COLOUR);
		StyleConstants.setForeground(warningSet, WARNING_FONT_COLOUR);
	}

	public static InputFeedbackHandler getInstance() {
		if (feedbackHandler == null) {
			feedbackHandler = new InputFeedbackHandler();
		}
		return feedbackHandler;
	}

	public void setupPointers(JTextPane tpInput, JTextArea taMessage) {
		this.tpInput = tpInput;
		this.taMessage = taMessage;
	}

	public void highlightText() {
		String input = tpInput.getText();
        tpInput.getStyledDocument().setCharacterAttributes(0, input.length(), defaultSet, true);

        if (!input.isEmpty()) {
        	taMessage.setText(null);
        }

        String[][] commands = {parser.getCommandKeywordList("update"), parser.getCommandKeywordList("delete"),
								parser.getCommandKeywordList("display"), parser.getCommandKeywordList("undo"),
								parser.getCommandKeywordList("redo"), parser.getCommandKeywordList("exit"),
								parser.getCommandKeywordList("undone"), parser.getCommandKeywordList("done")};

        highlightCommandKeyword(commands, input);
        highlightWarningCases(input, TIME_REGEX, TIME_MESSAGE);
        highlightWarningCases(input, MONTH_REGEX, MONTH_MESSAGE);
        highlightWarningCases(input, DAY_REGEX, DAY_MESSAGE);
    }

	private void highlightCommandKeyword(String[][] commands, String input) {
		for (int i = 0; i < commands.length; i++) {
        	for (String keyword : commands[i]) {
        		pattern = Pattern.compile("(?ui)^" + keyword + "\\s+");
                matcher = pattern.matcher(input);
                while (matcher.find()) {
                	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), keyword.length(), keywordSet, true);
                	taMessage.setText(commandSyntaxPrompt[i]);
                }
        	}
        }
	}

	private void highlightWarningCases(String input, String regex, String message) {
		pattern = Pattern.compile("(?ui)" + regex);
        matcher = pattern.matcher(input);
        while (matcher.find()) {
        	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), matcher.group().length(), warningSet, true);
        	taMessage.setText(message);
        }
	}
}

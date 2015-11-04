/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.TextArea;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import main.model.ObserverEvent;
import main.parser.Parser;

/**
 * @author Dalton
 *
 */
public class InputFeedbackHandler extends Observable {
	private static InputFeedbackHandler feedbackHandler = null;
	private JTextPane tpInput = null;
	private JTextArea taMessage = null;
	private static Parser parser = null;
	private static final Color normalTextColour = Color.BLACK;
	private static final Color highlightedTextColour = Color.decode("#19D14A");
	private static final SimpleAttributeSet defaultSet = new SimpleAttributeSet();
	private static final SimpleAttributeSet customSet = new SimpleAttributeSet();
	private InputFeedbackHandler() {}

	public static InputFeedbackHandler getInstance() {
		if (feedbackHandler == null) {
			feedbackHandler = new InputFeedbackHandler();
			getInstance().addObserver(MainGUI.getInstance());
			parser = Parser.getInstance();
		}
		return feedbackHandler;
	}

	public void setupPointers(JTextPane tpInput, JTextArea taMessage) {
		this.tpInput = tpInput;
		this.taMessage = taMessage;
	}

	public void highlightText() {
		String[][] commands = {parser.getCommandKeywordList("update"), parser.getCommandKeywordList("delete"),
								parser.getCommandKeywordList("display"),parser.getCommandKeywordList("undo"),
								parser.getCommandKeywordList("redo"),parser.getCommandKeywordList("exit"),
								parser.getCommandKeywordList("undone"),parser.getCommandKeywordList("done")};
		String[] commandMessagePrompt = {"Command aliases: update, modify, edit, /u, /m, /e  change\nCommand syntax( | means OR, * means OPTIONAL ): update {taskID} {columnID} {value}",
										"Command aliases: delete, del, remove, /d, /r\nCommand syntax( | means OR, * means OPTIONAL ): delete {taskID} *((to|-) {taskID})",
										"Command aliases: display, show, view, /sh, /v (| means OR, * means OPTIONAL )\nCommand syntax: display (expired|!expired|completed|!completed) | {date} *(to {date})",
										"Command aliases: undo, back\nCommand syntax( | means OR, * means OPTIONAL ): undo *{numberOfCommands}",
										"Command aliases: redo, forward\nCommand syntax( | means OR, * means OPTIONAL ): redo *{numberOfCommands}",
										"Command aliases: exit, quit\nCommand syntax( | means OR, * means OPTIONAL ): exit",
										"Command aliases: undone, !done, incomplete\nCommand Syntax( | means OR, * means OPTIONAL ): undone {taskID} *((to|-) {taskID})",
										"Command aliases: done, complete\nCommand syntax( | means OR, * means OPTIONAL ): done {taskID} *((to|-) {taskID})"};
		String[] days = {"mon", "tue", "wed", "thurs", "fri", "sat", "sun", "monday", "tueday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        String input = tpInput.getText();

        StyleConstants.setForeground(defaultSet, normalTextColour);
        tpInput.getStyledDocument().setCharacterAttributes(0, input.length(), defaultSet, true);

        StyleConstants.setForeground(customSet, highlightedTextColour);

        if (!input.isEmpty()) {
        	taMessage.setText(null);
        }

        for (int i = 0; i < commands.length; i++) {
        	for (String keyword : commands[i]) {
        		Pattern pattern = Pattern.compile("(?ui)^" + keyword + "\\W");
                Matcher matcher = pattern.matcher(input);
                while (matcher.find()) {
                	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), keyword.length(), customSet, true);
                	taMessage.setText(commandMessagePrompt[i]);
                }
        	}
        }

        for (String day : days) {
            Pattern pattern = Pattern.compile("(?ui)" + day + "\\s+(\\d+|(?:Jan(?:uary)?|Feb(?:ruary)?|"
            									+ "Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|"
            									+ "Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?))");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
            	tpInput.getStyledDocument().setCharacterAttributes(matcher.start(), day.length(), customSet, true);
            	taMessage.setText("Special case detected. Please surround your task description with double quotes to prevent parsing errors. (e.g. \"lunch with john\" at 1pm)");
            }
        }
    }
}

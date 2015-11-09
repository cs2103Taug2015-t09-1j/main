/*
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * The Class HelpList.
 *
 * @@author Jia Qi
 */
public class HelpList extends JPanel {

	private static final long serialVersionUID = 1L;
	private ListSelectionModel listSelectionModel;
	private JTextPane text;
	private JList<String> list;
	private final String[] featureList = { "Adding a task", "Deleting task(s)", "Updating task information",
			"Viewing task(s) on a date", "Marking a task as done/undone", "Undo-ing/Redo-ing the previous action(s)",
			"Hotkeys" };

	private static final String WELCOME_MESSAGE = "Welcome to Todokoro";
	private static final String COMMAND_LIST_LABEL = "Command List";
	private static final String COMMAND_DETAIL_LABEL = "Command Details";
	private static final String WELCOME_DOC_MESSAGE = "Welcome to TodoKoro Help List!";
	private static final String EMPTY_STRING = "";
	private static final String SCROLL_UP_HELP_KEY = "Scroll Up Help List";
	private static final String SROLL_DOWN_HELP_KEY = "Scroll Down Help List";

	/**
	 * Instantiates a new help list.
	 */
	public HelpList() {

		setLayout(null);

		JScrollPane listPane = new JScrollPane();
		listPane.setBounds(6, 30, 259, 138);
		listPane.setBorder(getCompoundBorder(4, 4, 4, 4));
		this.add(listPane);
		// contentPane.add(listPane);

		list = new JList<String>();
		list.setFont(new Font("Avenir Next", Font.PLAIN, 12));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(new AbstractListModel<String>() {

			private static final long serialVersionUID = 1L;
			String[] featureList = HelpList.this.featureList;

			public int getSize() {
				return featureList.length;
			}

			public String getElementAt(int index) {
				return featureList[index];
			}
		});

		list.setSelectedIndex(-1);
		list.requestFocus();

		listSelectionModel = list.getSelectionModel();
		listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
		listPane.setViewportView(list);

		JScrollPane textPane = new JScrollPane();
		textPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textPane.setBounds(6, 194, 259, 406);
		textPane.setBorder(getCompoundBorder(4, 4, 4, 4));

		this.add(textPane);

		text = new JTextPane();
		text.setBounds(6, 194, 259, 406);
		text.setEditable(false);
		text.setFont(new Font("Dialog", Font.PLAIN, 12));
		text.setText(WELCOME_MESSAGE);

		textPane.setViewportView(text);

		JLabel lblCommandList = new JLabel(COMMAND_LIST_LABEL);
		lblCommandList.setFont(new Font("Dialog", Font.BOLD, 14));
		lblCommandList.setBounds(6, 11, 250, 14);

		this.add(lblCommandList);

		JLabel lblNewLabel = new JLabel(COMMAND_DETAIL_LABEL);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel.setBounds(6, 172, 250, 21);
		add(lblNewLabel);
		// this.add(label);

		InputMap im = list.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = list.getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), SCROLL_UP_HELP_KEY);
		am.put(SCROLL_UP_HELP_KEY, new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				textPane.getVerticalScrollBar().setValue(textPane.getVerticalScrollBar().getValue() - 100);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), SROLL_DOWN_HELP_KEY);
		am.put(SROLL_DOWN_HELP_KEY, new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				textPane.getVerticalScrollBar().setValue(textPane.getVerticalScrollBar().getValue() + 100);
			}
		});
	}

	/**
	 * Gets the help list focus.
	 *
	 * @return the help list focus
	 */
	public void getHelpListFocus() {
		list.requestFocusInWindow();
	}

	/**
	 * Gets the compound border.
	 *
	 * @param top
	 *            the top
	 * @param left
	 *            the left
	 * @param bottom
	 *            the bottom
	 * @param up
	 *            the up
	 * @return the compound border
	 */
	private Border getCompoundBorder(int top, int left, int bottom, int up) {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(top, left, bottom, up);
		return new CompoundBorder(rounded, empty);
	}

	/**
	 * The Class SharedListSelectionHandler.
	 *
	 * @@author Yan Mingwei, Dalton
	 */
	class SharedListSelectionHandler implements ListSelectionListener {

		/* (non-Javadoc)
		 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
		 */
		public void valueChanged(ListSelectionEvent e) {
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();

			SimpleAttributeSet highlight = new SimpleAttributeSet();
			SimpleAttributeSet highlight_1 = new SimpleAttributeSet();

			highlight = new SimpleAttributeSet();
			StyleConstants.setBold(highlight, true);
			StyleConstants.setForeground(highlight, Color.red);

			highlight_1 = new SimpleAttributeSet();
			StyleConstants.setBold(highlight_1, true);
			StyleConstants.setForeground(highlight_1, Color.blue);

			Document doc = text.getStyledDocument();

			if (lsm.isSelectionEmpty()) {

				text.setText(EMPTY_STRING);

			} else {
				// Find out which indexes are selected.
				int minIndex = lsm.getMinSelectionIndex();
				int maxIndex = lsm.getMaxSelectionIndex();
				for (int i = minIndex; i <= maxIndex; i++) {
					if (lsm.isSelectedIndex(i)) {

						switch (i) {
						case -1:
							text.setText(WELCOME_DOC_MESSAGE);
							break;

						case 0:
							text.setText(EMPTY_STRING);
							try {
								doc.insertString(doc.getLength(), "Add Events:\n", highlight);
								doc.insertString(doc.getLength(), "Add a task with a specific date/time\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "task description + date/time\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "watch movie on wed;\n"
																 + "watch movie 30 sept;\n"
																 + "tomorrow watch movie at 3pm;\n"
																 + "watch movie from 3pm to 5pm;\n\n", null);
								doc.insertString(doc.getLength(), "Add Todos\n", highlight);
								doc.insertString(doc.getLength(), "Add a task without a specific date/time\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "task description\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "watch movie;\n", null);
								doc.insertString(doc.getLength(), "do past year exam paper;\n\n", null);
								doc.insertString(doc.getLength(), "Add Deadlines: \n", highlight);
								doc.insertString(doc.getLength(), "Add a task with a specific due-date/time\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "task description + due/due on/due by/by + due-date/time\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "pay utility fee by fri;\n", null);
								doc.insertString(doc.getLength(), "Final Report due by 30 sep ;", null);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						case 1:
							text.setText(EMPTY_STRING);
							try {

								doc.insertString(doc.getLength(), "Single Delete:\n", highlight);
								doc.insertString(doc.getLength(), "delete a task\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "delete/del + taskId\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "delete 5\n", null);
								doc.insertString(doc.getLength(), "del 5\n\n", null);

								doc.insertString(doc.getLength(), "Multiple Delete:\n", highlight);
								doc.insertString(doc.getLength(), "delete several tasks in one time\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "delete + taskId + taskId +...\n", null);
								doc.insertString(doc.getLength(), "delete + taskId-taskId\n", null);
								doc.insertString(doc.getLength(), "delete + all\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "delete 5 8 9 23\n", null);
								doc.insertString(doc.getLength(), "delete 5-23\n", null);
								doc.insertString(doc.getLength(), "delete all\n\n", null);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						case 2:
							text.setText(EMPTY_STRING);
							try {

								doc.insertString(doc.getLength(), "Update:\n", highlight);
								doc.insertString(doc.getLength(), "update a task information\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "update + taskId + update column number + update infromarion\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "update 2 4 watch movie with Jason;\n", null);
								doc.insertString(doc.getLength(), "update 2 1 Oct 23;\n", null);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						case 3:
							text.setText(EMPTY_STRING);
							try {

								doc.insertString(doc.getLength(), "View task(s) on one day: \n", highlight);
								doc.insertString(doc.getLength(), "view task(s) on a specific date/view all tasks\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "view + date / view + all\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "view today;\n", null);
								doc.insertString(doc.getLength(), "view nov 3;\n\n", null);

								doc.insertString(doc.getLength(), "View all tasks: \n", highlight);
								doc.insertString(doc.getLength(), "view all Event tasks (if you are on Events Tag) / view all Deadline tasks (if you are on Deadlines Tag)\n", null);
								doc.insertString(doc.getLength(), "Syntax:", highlight_1);
								doc.insertString(doc.getLength(), "view + all\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "view all;\n", null);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						case 4:
							text.setText(EMPTY_STRING);
							try {

								doc.insertString(doc.getLength(), "Done/Undone: \n", highlight);
								doc.insertString(doc.getLength(), "mark a done task as undone\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "done/undone + taskId\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "done 1\nundone 1\n", null);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						case 5:
							text.setText(EMPTY_STRING);
							try {

								doc.insertString(doc.getLength(), "Undo-ing/Redo-ing a single action: \n", highlight);
								doc.insertString(doc.getLength(), "undo/redo the privious action\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "undo/redo\n" , null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "undo\nredu\n", null);

								doc.insertString(doc.getLength(), "Undo-ing/Redo-ing multiple actions: \n", highlight);
								doc.insertString(doc.getLength(), "undo/redo the privious actions\n", null);
								doc.insertString(doc.getLength(), "Syntax:\n", highlight_1);
								doc.insertString(doc.getLength(), "undo/redo + number\n", null);
								doc.insertString(doc.getLength(), "Examples:\n", highlight_1);
								doc.insertString(doc.getLength(), "undo 20\nredo 10\n", null);
							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						case 6:
							text.setText(EMPTY_STRING);
							try {
								doc.insertString(doc.getLength(), "Switching within task tags: \n", highlight);
								doc.insertString(doc.getLength(), "Events Tag: \n", highlight_1);
								doc.insertString(doc.getLength(), "Ctrl+1", null);
								doc.insertString(doc.getLength(), "Todos Tag: \n", highlight_1);
								doc.insertString(doc.getLength(), "Ctrl+2\n", null);
								doc.insertString(doc.getLength(), "Deadlines Tag: \n", highlight_1);
								doc.insertString(doc.getLength(), "Ctrl+3\n\n", null);

								doc.insertString(doc.getLength(), "Getting help list: \n", highlight);
								doc.insertString(doc.getLength(), "F1\n\n", null);

								doc.insertString(doc.getLength(), "Switching to simple mode: \n", highlight);
								doc.insertString(doc.getLength(), "F2\n\n", null);

								doc.insertString(doc.getLength(), "Choosing save direction: \n", highlight);
								doc.insertString(doc.getLength(), "F3\n\n", null);

								doc.insertString(doc.getLength(), "Switching tables: \n", highlight);
								doc.insertString(doc.getLength(), "F4\n\n", null);

								doc.insertString(doc.getLength(), "Exiting save direction: \n", highlight);
								doc.insertString(doc.getLength(), "Esc\n\n", null);

								doc.insertString(doc.getLength(), "Switching between themes: \n", highlight);
								doc.insertString(doc.getLength(), "Crtrl + ~\n\n", null);

								doc.insertString(doc.getLength(), "Scrolling down the task screen: \n", highlight);
								doc.insertString(doc.getLength(), "F5\n\n", null);

								doc.insertString(doc.getLength(), "Scrolling up the task screen: \n", highlight);
								doc.insertString(doc.getLength(), "F6\n\n", null);

							} catch (BadLocationException e1) {
								e1.printStackTrace();
							}

							break;

						}
						text.setCaretPosition(0);
					}
				}
			}
		}
	}
}

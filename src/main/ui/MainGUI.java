/**
 *
 */
package main.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import main.logic.ChangeDirectory;
import main.logic.Logic;
import main.model.EnumTypes;
import main.model.ObserverEvent;
import main.model.tableModels.DeadlinesTableModel;
import main.model.tableModels.EventsTableModel;
import main.model.tableModels.TodosTableModel;
import main.model.taskModels.Task;
import main.storage.LogFileHandler;

import javax.swing.JTextArea;

/**
 * The Class MainGUI.
 * Displays the main GUI of the application that interacts with the user
 * and handles the additional graphical/hotkey functions.
 *
 * @@author Dalton
 */
public class MainGUI extends Observable implements Observer {

	private static MainGUI mainGUI;

	private JFrame frmTodokoro;
	private JPanel inputPanel;
	private JTextField tfFilter;
	private JTextPane tpUserInput;
	private JLabel lblFilter;
	private JTable eventsTable, todosTable, deadlinesTable;
	private JTabbedPane tabbedPane;
	private JScrollPane eventsScrollPane, todosScrollPane, deadlinesScrollPane;
	private TableRowSorter<?> eventsSorter, todosSorter, deadlinesSorter;
	private JTextArea taStatusMessage;

	private static final Logger logger = Logger.getLogger(MainGUI.class.getName());
	private static HelpList helpList = null;
	private static InputHistoryHandler history = null;
	private static InputFeedbackListener feedback = null;
	private static EventsTableModel etm = null;
	private static TodosTableModel ttm = null;
	private static DeadlinesTableModel dtm = null;

	private static final int FRAME_WIDTH = 768;
	private static final int FRAME_HEIGHT = 640;
	private static final int FRAME_X_LOC = 0;
	private static final int FRAME_Y_LOC = 23;
	private static final float FRAME_OPACITY = 1f;

	private static final int MINI_MODE_HEIGHT = 137;
	private static final int MINI_MODE_WIDTH = 762;
	private static final int MINI_MODE_X_LOC = 0;
	private static final int MINI_MODE_Y_LOC = 475;

	private static final int MINI_MODE_FRAME_WIDTH = 768;
	private static final int MINI_MODE_FRAME_HEIGHT = 167;
	private static final float MINI_MODE_FRAME_OPACITY = 0.9f;

	private static final int HELP_LIST_MODE_WIDTH = 270;
	private static final int HELP_LIST_MODE_HEIGHT = 600;

	private static final int HELP_LIST_MODE_FRAME_WIDTH = 1044;
	private static final int HELP_LIST_MODE_FRAME_HEIGHT = 640;

	private static final int INPUT_TEXTPANE_HEIGHT = 38;
	private static final int INPUT_TEXTPANE_WIDTH = 738;
	private static final int INPUT_TEXTPANE_X_LOC = 12;
	private static final int INPUT_TEXTPANE_Y_LOC = 87;

	private static final String FILTER_LABEL_FONT = "Dialog UI";
	private static final int FILTER_LABEL_FONT_STYLE = Font.BOLD;
	private static final int FILTER_LABEL_FONT_SIZE = 14;

	private static final String STATUS_TEXTAREA_FONT = "Dialog UI";
	private static final int STATUS_TEXTAREA_FONT_STYLE = Font.BOLD;
	private static final int STATUS_TEXTAREA_FONT_SIZE = 14;

	private static final String INPUT_TEXTPANE_FONT = "Segoe UI Semibold";
	private static final int INPUT_TEXTPANE_FONT_STYLE = Font.BOLD;
	private static final int INPUT_TEXTPANE_FONT_SIZE = 20;

	private static final int TABLE_ROW_HEIGHT = 50;

	private static final String[] themes = {"bernstein.BernsteinLookAndFeel", "noire.NoireLookAndFeel",
											"smart.SmartLookAndFeel", "mint.MintLookAndFeel", "mcwin.McWinLookAndFeel"};
	private static int themeIndex = 0;

	private static boolean isMiniMode = false;
	private static boolean isDisplayingHelpList = false;

	/**
	 * The main method.
	 *
	 * @param args	the arguments
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainGUI = MainGUI.getInstance();
					mainGUI.sendInstanceToModels();
					mainGUI.addObserver(Logic.getInstance());
				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Gets the single instance of MainGUI.
	 *
	 * @return single instance of MainGUI
	 */
	public static MainGUI getInstance() {
		if (mainGUI == null) {
			return new MainGUI();
		}
		return mainGUI;
	}

	/**
	 * Instantiates a new main GUI and sets up the logger.
	 */
	private MainGUI() {
		try {
			initialise();
			LogFileHandler.getInstance().addLogFileHandler(logger);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Initialise the main components of the GUI.
	 */
	private void initialise() {
		try {
			setupMainFrame();
			setupPanels();
			setupTextPanes();
			setupTabbedPane();
			setupTableModels();
			setupTables();
			setupTableSorters();
			setupFilterLabel();
			setupStatusMessageTextArea();
			setupInputHistoryHandler();
			setupInputFeedbackReferences();
			setupHelpList();
			setupKeyBinds();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Setup main frame.
	 */
	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setAlwaysOnTop(true);
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(FRAME_X_LOC, FRAME_Y_LOC, FRAME_WIDTH, FRAME_HEIGHT);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);
		frmTodokoro.setVisible(true);

		// Set focus to user input textpane on windowOpened event
		frmTodokoro.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				tpUserInput.requestFocusInWindow();
			}
		});
	}

	/**
	 * Setup panels.
	 */
	private void setupPanels() {
		inputPanel = new JPanel();
		inputPanel.setBounds(MINI_MODE_X_LOC, MINI_MODE_Y_LOC, MINI_MODE_WIDTH, MINI_MODE_HEIGHT);
		inputPanel.setLayout(null);
		frmTodokoro.getContentPane().add(inputPanel);
	}

	/**
	 * Setup text panes.
	 */
	private void setupTextPanes() {
		tpUserInput = new JTextPane();
		inputPanel.add(tpUserInput);
		tpUserInput.setFont(generateFont(INPUT_TEXTPANE_FONT, INPUT_TEXTPANE_FONT_STYLE, INPUT_TEXTPANE_FONT_SIZE));
		tpUserInput.setBounds(INPUT_TEXTPANE_X_LOC, INPUT_TEXTPANE_Y_LOC, INPUT_TEXTPANE_WIDTH, INPUT_TEXTPANE_HEIGHT);
		tpUserInput.setBorder(createCompoundBorder(4, 4, 0, 4));
		tpUserInput.setFocusAccelerator('e');
		tpUserInput.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				// Not used
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				handleTextChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				handleTextChanged();
			}
		});

		tfFilter = new JTextField();
		tfFilter.setBounds(594, 12, 156, 26);
		frmTodokoro.getContentPane().add(tfFilter);
		tfFilter.setColumns(10);
		tfFilter.setBorder(createCompoundBorder(0, 4, 0, 4));
		tfFilter.setFocusAccelerator('f');
		tfFilter.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
		    	filterTables();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				filterTables();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterTables();
			}
		});
	}

	/**
	 * Setup tabbed pane.
	 */
	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setBounds(12, 8, 738, 465);
		eventsScrollPane = new JScrollPane();
		todosScrollPane = new JScrollPane();
		deadlinesScrollPane = new JScrollPane();
		eventsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		todosScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		deadlinesScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events (1)</b></body></html>", null, eventsScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos (2)</b></body></html>", null, todosScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Deadlines (3)</b></body></html>", null, deadlinesScrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	/**
	 * Setup table models.
	 */
	private void setupTableModels() {
		etm = EventsTableModel.getInstance();
		ttm = TodosTableModel.getInstance();
		dtm = DeadlinesTableModel.getInstance();
	}

	/**
	 * Setup tables.
	 */
	private void setupTables() {
		setupDeadlinesTable();
		setupTodosTable();
		setupEventsTable();
	}

	/**
	 * Setup table sorters.
	 */
	private void setupTableSorters() {
		eventsSorter = new TableRowSorter<EventsTableModel>(etm);
		eventsTable.setRowSorter(eventsSorter);
		eventsSorter.toggleSortOrder(1);

		todosSorter = new TableRowSorter<TodosTableModel>(ttm);
		todosTable.setRowSorter(todosSorter);
		todosSorter.toggleSortOrder(2);

		deadlinesSorter = new TableRowSorter<DeadlinesTableModel>(dtm);
		deadlinesTable.setRowSorter(deadlinesSorter);
		deadlinesSorter.toggleSortOrder(1);
	}

	/**
	 * Setup filter label.
	 */
	private void setupFilterLabel() {
		lblFilter = new JLabel("Filter:");
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(generateFont(FILTER_LABEL_FONT, FILTER_LABEL_FONT_STYLE, FILTER_LABEL_FONT_SIZE));
		lblFilter.setBounds(530, 16, 60, 16);
		frmTodokoro.getContentPane().add(lblFilter);
	}

	/**
	 * Setup status message text area.
	 */
	private void setupStatusMessageTextArea() {
		taStatusMessage = new JTextArea(2, 20);
		taStatusMessage.setBounds(12, 4, 738, 75);
		taStatusMessage.setWrapStyleWord(true);
		taStatusMessage.setLineWrap(true);
		taStatusMessage.setOpaque(false);
		taStatusMessage.setEditable(false);
		taStatusMessage.setFocusable(false);
		taStatusMessage.setBackground(UIManager.getColor("Label.background"));
		taStatusMessage.setFont(generateFont(STATUS_TEXTAREA_FONT, STATUS_TEXTAREA_FONT_STYLE, STATUS_TEXTAREA_FONT_SIZE));
		taStatusMessage.setBorder(new TitledBorder(createCompoundBorder(0, 5, 0, 5), "Status Message", 0, 0, generateFont("Dialog UI", Font.BOLD, 14)));
		inputPanel.add(taStatusMessage);
	}

	/**
	 * Initialise input history handler.
	 */
	private void setupInputHistoryHandler() {
		history = InputHistoryHandler.getInstance();
	}

	/**
	 * Setup input feedback references.
	 */
	private void setupInputFeedbackReferences() {
		feedback = InputFeedbackListener.getInstance();
		feedback.setupReferences(tpUserInput, taStatusMessage);
	}

	/**
	 * Setup help list panel and add it to the main frame.
	 */
	private void setupHelpList() {
		helpList = new HelpList();
		helpList.setBounds(FRAME_WIDTH-10, 0, HELP_LIST_MODE_WIDTH, HELP_LIST_MODE_HEIGHT);
		helpList.setVisible(true);
		frmTodokoro.getContentPane().add(helpList);
	}

	/**
	 * Setup key binds.
	 */
	@SuppressWarnings("serial")
	private void setupKeyBinds() {
		InputMap im = frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = frmTodokoro.getRootPane().getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Toggle Help List");
		am.put("Toggle Help List", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				toggleHelpList();
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "Toggle Mini Mode");
		am.put("Toggle Mini Mode", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				toggleMiniMode();
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "Change Directory");
		am.put("Change Directory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				new ChangeDirectory(frmTodokoro);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "Cycle Tabs");
		am.put("Cycle Tabs", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cycleTabs();
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "Scroll Up");
		am.put("Scroll Up", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				scrollTable("UP");
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "Scroll Down");
		am.put("Scroll Down", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				scrollTable("DOWN");
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, InputEvent.CTRL_DOWN_MASK), "Cycle Themes");
		am.put("Cycle Themes", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				cycleThemes();
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Send Command");
		tpUserInput.getActionMap().put("Send Command", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sendUserInput(tpUserInput.getText().trim());
				history.saveInputHistory(tpUserInput.getText().trim());
				tpUserInput.setText(null);
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Load Previous Input");
		tpUserInput.getActionMap().put("Load Previous Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loadInput("PREVIOUS");
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Load Next Input");
		tpUserInput.getActionMap().put("Load Next Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loadInput("NEXT");
			}
		});
	}

	/**
	 * Send instance of UI to table models.
	 */
	private void sendInstanceToModels() {
		dtm.setUIInstance(mainGUI);
		etm.setUIInstance(mainGUI);
		ttm.setUIInstance(mainGUI);
	}

	/**
	 * Load next/previous input from input history.
	 *
	 * @param position	the position of the input
	 */
	private void loadInput(String position) {
		String input = null;
		if (position.equals("NEXT")) {
			input = history.getNextInput();
		} else if (position.equals("PREVIOUS")) {
			input = history.getPreviousInput();
		}

		if (input != null) {
			tpUserInput.setText(input);
		}
	}

	/**
	 * Toggle help list.
	 */
	private void toggleHelpList() {
		if (isDisplayingHelpList) {
			tpUserInput.requestFocusInWindow();
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
		} else {
			if (isMiniMode) {
				toggleMiniMode();
			}
			helpList.getHelpListFocus();
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), HELP_LIST_MODE_FRAME_WIDTH, HELP_LIST_MODE_FRAME_HEIGHT);
		}

		isDisplayingHelpList = !isDisplayingHelpList;
	}

	/**
	 * Toggle mini-mode.
	 */
	private void toggleMiniMode() {
		if (isMiniMode) {
			tabbedPane.setVisible(true);
			lblFilter.setVisible(true);
			tfFilter.setVisible(true);
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
			frmTodokoro.setOpacity(FRAME_OPACITY);
			inputPanel.setBounds(0, 475, MINI_MODE_WIDTH, MINI_MODE_HEIGHT);
		} else {
			tabbedPane.setVisible(false);
			lblFilter.setVisible(false);
			tfFilter.setVisible(false);
			frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), MINI_MODE_FRAME_WIDTH, MINI_MODE_FRAME_HEIGHT);
			frmTodokoro.setOpacity(MINI_MODE_FRAME_OPACITY);
			inputPanel.setBounds(0, 0, MINI_MODE_WIDTH, MINI_MODE_HEIGHT);
			isDisplayingHelpList = false;
		}

		isMiniMode = !isMiniMode;
	}

	/**
	 * Cycle through tabs.
	 * Alternative hotkey to select tabs for Mac as Mnemonics are not supported.
	 */
	private void cycleTabs() {
		if (tabbedPane.getSelectedIndex() == 2) {
			tabbedPane.setSelectedIndex(0);
		} else {
			tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex()+1);
		}
	}

	/**
	 * Cycle through themes.
	 */
	private void cycleThemes() {
		themeIndex++;
		themeIndex = themeIndex%themes.length;

		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (InstantiationException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (UnsupportedLookAndFeelException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		SwingUtilities.updateComponentTreeUI(frmTodokoro);

		// Reset row height of tables after updating components
		eventsTable.setRowHeight(TABLE_ROW_HEIGHT);
		todosTable.setRowHeight(TABLE_ROW_HEIGHT);
		deadlinesTable.setRowHeight(TABLE_ROW_HEIGHT);
	}

	/**
	 * Scroll up/down table entries.
	 *
	 * @param direction		the scrolling direction
	 */
	private void scrollTable(String direction) {
		if (direction.equals("UP")) {
			switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()+todosScrollPane.getHeight()-24);
					break;
				case 2:
					deadlinesScrollPane.getVerticalScrollBar().setValue(deadlinesScrollPane.getVerticalScrollBar().getValue()+deadlinesScrollPane.getHeight()-24);
					break;
				default:
					// Impossible case
					assert false : tabbedPane.getSelectedIndex();
			}
		} else if (direction.equals("DOWN")) {
			switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()-todosScrollPane.getHeight()-24);
					break;
				case 2:
					deadlinesScrollPane.getVerticalScrollBar().setValue(deadlinesScrollPane.getVerticalScrollBar().getValue()-deadlinesScrollPane.getHeight()-24);
					break;
				default:
					// Impossible case
					assert false : tabbedPane.getSelectedIndex();
			}
		}
	}

	/**
	 * Setup events table.
	 */
	private void setupEventsTable() {
		eventsTable = new JTable();
		eventsTable.setName("Events");
		eventsTable.setModel(etm);
		setupTableProperties(eventsTable);
		setupRenderersAndEditors(eventsTable);
		setupDimensions(eventsTable);
		eventsScrollPane.setViewportView(eventsTable);
	}

	/**
	 * Setup todos table.
	 */
	private void setupTodosTable() {
		todosTable = new JTable();
		todosTable.setName("Todos");
		todosTable.setModel(ttm);
		setupTableProperties(todosTable);
		setupRenderersAndEditors(todosTable);
		setupDimensions(todosTable);
		todosScrollPane.setViewportView(todosTable);
	}

	/**
	 * Setup deadlines table.
	 */
	private void setupDeadlinesTable() {
		deadlinesTable = new JTable();
		deadlinesTable.setName("Deadlines");
		deadlinesTable.setModel(dtm);
		setupTableProperties(deadlinesTable);
		setupRenderersAndEditors(deadlinesTable);
		setupDimensions(deadlinesTable);
		deadlinesScrollPane.setViewportView(deadlinesTable);
	}

	/**
	 * Sets the up custom renderers and editors.
	 *
	 * @param table		the tables to assign renderers and editors
	 */
	private void setupRenderersAndEditors(JTable table) {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		CustomCellRenderer customRenderer = new CustomCellRenderer();
		CustomDateCellEditor customDateEditor = new CustomDateCellEditor();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		table.setDefaultRenderer(Integer.class, centerRenderer);
		table.setDefaultRenderer(String.class, customRenderer);
		table.setDefaultEditor(String.class, new CustomStringCellEditor());

		if (table.getName().equals("Events") || table.getName().equals("Deadlines")) {
			table.setDefaultRenderer(Date.class, customRenderer);
			table.setDefaultEditor(Date.class, customDateEditor);
		}
	}

	/**
	 * Filter row.
	 *
	 * @param sorter	the table sorter
	 * @param index		the index of starting column to sort
	 */
	private void filterRow(TableRowSorter<?> sorter, int index) {
		RowFilter<Object, Object> rowFilter = null;
		List<RowFilter<Object, Object>> rowFilters = new ArrayList<RowFilter<Object, Object>>();

		try {
			String searchTerms = tfFilter.getText();

			if (searchTerms.equals("done")) {
				rowFilter = RowFilter.regexFilter("^true$");
			} else if (searchTerms.equals("!done") || searchTerms.equals("not done") || searchTerms.equals("undone")) {
				rowFilter = RowFilter.regexFilter("^false$");
			} else {
				String[] termsArr = searchTerms.split(" ");

				for (int i = 0; i < termsArr.length; i++) {
					rowFilters.add(RowFilter.regexFilter("(?iu)" + termsArr[i], index, index+1));
				}

				rowFilter = RowFilter.andFilter(rowFilters);
			}
		} catch (PatternSyntaxException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		sorter.setRowFilter(rowFilter);
	}

	/**
	 * Filter tables.
	 */
	private void filterTables() {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	filterRow(eventsSorter, 3);
				filterRow(todosSorter, 1);
				filterRow(deadlinesSorter, 2);
		    }
	    });
	}

	/**
	 * Update tables.
	 *
	 * @param type				the type of task
	 * @param tasks				the list of tasks
	 * @param shouldFocusTab	the boolean to determine which tab to focus to after updating
	 */
	public void updateTables(EnumTypes.TASK_TYPE type, List<Task> tasks, boolean shouldFocusTab) {
		switch (type) {
			case EVENT:
				etm.setTasks(tasks);
				etm.fireTableDataChanged();
				if (shouldFocusTab) {
					tabbedPane.setSelectedIndex(0);
				}
				break;
			case TODO:
				ttm.setTasks(tasks);
				ttm.fireTableDataChanged();
				if (shouldFocusTab) {
					tabbedPane.setSelectedIndex(1);
				}
				break;
			case DEADLINE:
				dtm.setTasks(tasks);
				dtm.fireTableDataChanged();
				if (shouldFocusTab) {
					tabbedPane.setSelectedIndex(2);
				}
				break;
			default:
				// Impossible case
				assert false : type;
		}
	}

	/**
	 * Sets up the table dimensions.
	 *
	 * @param table		the table to specify dimensions
	 */
	private void setupDimensions(JTable table) {
		table.setRowHeight(TABLE_ROW_HEIGHT);
		table.getColumnModel().getColumn(0).setMaxWidth(45);

		switch (table.getName()) {
		case "Events":
			table.getColumnModel().getColumn(1).setMinWidth(132);
			table.getColumnModel().getColumn(1).setMaxWidth(132);
			table.getColumnModel().getColumn(2).setMinWidth(132);
			table.getColumnModel().getColumn(2).setMaxWidth(132);
			table.getColumnModel().getColumn(3).setMinWidth(377);
			table.getColumnModel().getColumn(3).setMaxWidth(682);
			table.getColumnModel().getColumn(4).setMaxWidth(50);
			break;
		case "Todos":
			table.getColumnModel().getColumn(1).setMinWidth(641);
			table.getColumnModel().getColumn(2).setMaxWidth(50);
			break;
		case "Deadlines":
			table.getColumnModel().getColumn(1).setMinWidth(132);
			table.getColumnModel().getColumn(1).setMaxWidth(132);
			table.getColumnModel().getColumn(2).setMinWidth(509);
			table.getColumnModel().getColumn(3).setMaxWidth(50);
			break;
		}
	}

	/**
	 * Sets up the table properties.
	 *
	 * @param table		the table to specify properties
	 */
	private void setupTableProperties(JTable table) {
		table.getTableHeader().setReorderingAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
	}

	/**
	 * Generate font.
	 *
	 * @param font	the font
	 * @param style	the style
	 * @param size	the size
	 * @return 		the font
	 */
	private Font generateFont(String font, int style, int size) {
		return new Font(font, style, size);
	}

	/**
	 * Create a rounded compound border.
	 *
	 * @param top		the top margin
	 * @param left		the left margin
	 * @param bottom	the bottom margin
	 * @param right		the right margin
	 * @return the compound border
	 */
	private Border createCompoundBorder(int top, int left, int bottom, int right) {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(top, left, bottom, right);
		return new CompoundBorder(rounded, empty);
	}

	/**
	 * Update status message.
	 *
	 * @param msg	the new status message
	 */
	public void updateStatusMsg(String msg) {
    	taStatusMessage.setText(msg);
	}

	/**
	 * Notify observers that input value has changed.
	 *
	 * @param command	the input command
	 */
	public void sendUserInput(String command) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_USER_INPUT_CODE, new ObserverEvent.EInput(command)));
	}

	/**
	 * Update method that is called upon receiving change notification by observable
	 */
	@Override
	public void update(Observable observable, Object event) {
		ObserverEvent OEvent = (ObserverEvent) event;

		if (OEvent.getCode() == ObserverEvent.CHANGE_MESSAGE_CODE) {
			ObserverEvent.EMessage eMessage = (ObserverEvent.EMessage) OEvent.getPayload();
			updateStatusMsg(eMessage.getMessage());
			return;
		}

		if (OEvent.getCode() == ObserverEvent.CHANGE_TABLE_CODE) {
			ObserverEvent.ETasks eTasks = (ObserverEvent.ETasks) OEvent.getPayload();
			updateTables(eTasks.getTaskType(), eTasks.getTasks(), eTasks.shouldSwitch());
			return;
		}
	}

	/**
	 * Handle the change in text and invoke later to prevent clashes with other threads
	 */
    private void handleTextChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	try {
            		feedback.highlightText();
            	} catch (Exception e) {
        			logger.log(Level.SEVERE, e.getMessage(), e);
        		}
            }
        });
    }
}

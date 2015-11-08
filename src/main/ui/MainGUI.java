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
import javax.swing.JTextArea;

/**
 * @@author Dalton
 *
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
	private JScrollPane eventsScrollPane, todosScrollPane, deadlineTasksScrollPane;
	private TableRowSorter eventsSorter, todosSorter, deadlinesSorter;
	private JTextArea taStatusMessage;

	private static final Logger logger = Logger.getLogger(MainGUI.class.getName());
	private static InputHistoryHandler history = null;
	private static InputFeedbackHandler feedback = null;
	private static EventsTableModel etm = null;
	private static TodosTableModel ttm = null;
	private static DeadlinesTableModel dtm = null;

	private static final int FRAME_WIDTH = 768;
	//private static final int FRAME_WIDTH = 1024;
	private static final int FRAME_HEIGHT = 640;
	private static final float FRAME_OPACITY = 1f;

	private static final int INPUT_PANEL_HEIGHT = 137;
	private static final int INPUT_PANEL_WIDTH = 762;

	private static final int FRAME_SIMPLE_MODE_WIDTH = 768;
	private static final int FRAME_SIMPLE_MODE_HEIGHT = 167;
	private static final float FRAME_SIMPLE_MODE_OPACITY = 0.9f;

	private static final int FRAME_HELP_LIST_WIDTH = 1024;
	private static final int FRAME_HELP_LIST_HEIGHT = 640;

	private static final int TABLE_FONT_SIZE = 14;
	private static final int LABEL_FONT_SIZE = 15;
	private static final int TABLE_ROW_HEIGHT = 50;

	private static final String[] themes = {"bernstein.BernsteinLookAndFeel", "noire.NoireLookAndFeel", "smart.SmartLookAndFeel", "mint.MintLookAndFeel", "mcwin.McWinLookAndFeel"};
	private static int themeIndex = 0;
	private static boolean isNormalMode = true;
	private static boolean isHelpMode = false;

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

	public static MainGUI getInstance() {
		if (mainGUI == null) {
			return new MainGUI();
		}
		return mainGUI;
	}

	private MainGUI() {
		try {
			initialise();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialise() throws Exception {
		setupMainFrame();
		setupPanels();
		setupTabbedPane();
		setupTableModels();
		setupTables();
		setupTableSorters();
		setupTextPane();
		setupFilterLabel();
		setupStatusMessageTextArea();
		setupInputHistoryHandler();
		setupInputFeedbackPointers();
		setupKeyBinds();
	}

	private void setupInputHistoryHandler() {
		history = InputHistoryHandler.getInstance();
	}

	private void setupInputFeedbackPointers() {
		feedback = InputFeedbackHandler.getInstance();
		feedback.setupPointers(tpUserInput, taStatusMessage);
	}

	private void setupTableModels() {
		etm = EventsTableModel.getInstance();
		ttm = TodosTableModel.getInstance();
		dtm = DeadlinesTableModel.getInstance();
	}

	private void sendInstanceToModels() {
		dtm.setUIInstance(mainGUI);
		etm.setUIInstance(mainGUI);
		ttm.setUIInstance(mainGUI);
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setAlwaysOnTop(true);
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(0, 23, FRAME_WIDTH, FRAME_HEIGHT);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);

		frmTodokoro.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				tpUserInput.requestFocusInWindow();
			}
		});

		frmTodokoro.setVisible(true);
	}

	private void setupKeyBinds() {
		InputMap im = frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap am = frmTodokoro.getRootPane().getActionMap();

		GetHelpList demo = new GetHelpList();
		demo.setBounds(768, 0, 240, 600);
		demo.setVisible(true);
		frmTodokoro.getContentPane().add(demo);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Help List");
		am.put("Help List", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (isHelpMode) {
					tpUserInput.requestFocusInWindow();
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
				} else {
					if (!isNormalMode) {
						tabbedPane.setVisible(true);
						lblFilter.setVisible(true);
						tfFilter.setVisible(true);
						frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
						frmTodokoro.setOpacity(FRAME_OPACITY);
						inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
						isNormalMode = true;
					}
					demo.requestListFocus();
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_HELP_LIST_WIDTH, FRAME_HELP_LIST_HEIGHT);
				}

				isHelpMode = !isHelpMode;
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "Simple Mode");
		am.put("Simple Mode", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (isNormalMode) {
					tabbedPane.setVisible(false);
					lblFilter.setVisible(false);
					tfFilter.setVisible(false);
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_SIMPLE_MODE_WIDTH, FRAME_SIMPLE_MODE_HEIGHT);
					frmTodokoro.setOpacity(FRAME_SIMPLE_MODE_OPACITY);
					inputPanel.setBounds(0, 0, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
					isHelpMode = false;
				} else {
					tabbedPane.setVisible(true);
					lblFilter.setVisible(true);
					tfFilter.setVisible(true);
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
					frmTodokoro.setOpacity(FRAME_OPACITY);
					inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
				}

				isNormalMode = !isNormalMode;
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "Change Directory");
		am.put("Change Directory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ChangeDirectory cd = new ChangeDirectory(frmTodokoro);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, InputEvent.CTRL_DOWN_MASK), "Cycle Themes");
		am.put("Cycle Themes", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (themeIndex == themes.length-1) {
					themeIndex = 0;
				} else {
					themeIndex++;
				}
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
				} catch (ClassNotFoundException e1) {
					logger.log(Level.SEVERE, e.getActionCommand(), e);
				} catch (InstantiationException e1) {
					logger.log(Level.SEVERE, e.getActionCommand(), e);
				} catch (IllegalAccessException e1) {
					logger.log(Level.SEVERE, e.getActionCommand(), e);
				} catch (UnsupportedLookAndFeelException e1) {
					logger.log(Level.SEVERE, e.getActionCommand(), e);
				}
				SwingUtilities.updateComponentTreeUI(frmTodokoro);
				eventsTable.setRowHeight(TABLE_ROW_HEIGHT);
				todosTable.setRowHeight(TABLE_ROW_HEIGHT);
				deadlinesTable.setRowHeight(TABLE_ROW_HEIGHT);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK), "Cycle Tabs");
		am.put("Cycle Tabs", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex() == 2) {
					tabbedPane.setSelectedIndex(0);
				} else {
					tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex()+1);
				}
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK), "Scroll Down Tables");
		am.put("Scroll Down Tables", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				case 2:
					deadlineTasksScrollPane.getVerticalScrollBar().setValue(deadlineTasksScrollPane.getVerticalScrollBar().getValue()+eventsScrollPane.getHeight()-24);
					break;
				}
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.CTRL_DOWN_MASK), "Scroll Up Tables");
		am.put("Scroll Up Tables", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				switch (tabbedPane.getSelectedIndex()) {
				case 0:
					eventsScrollPane.getVerticalScrollBar().setValue(eventsScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				case 1:
					todosScrollPane.getVerticalScrollBar().setValue(todosScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				case 2:
					deadlineTasksScrollPane.getVerticalScrollBar().setValue(deadlineTasksScrollPane.getVerticalScrollBar().getValue()-eventsScrollPane.getHeight()-24);
					break;
				}
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Send Command");
		tpUserInput.getActionMap().put("Send Command", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				sendUserInput(tpUserInput.getText().trim());
				history.addInputHistory(tpUserInput.getText());
				tpUserInput.setText(null);
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "Previous Input");
		tpUserInput.getActionMap().put("Previous Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String prevInput = history.getPreviousInput();
				if (prevInput != null) {
					tpUserInput.setText(prevInput);
				}
			}
		});

		tpUserInput.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "Next Input");
		tpUserInput.getActionMap().put("Next Input", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String nextInput = history.getNextInput();
				if (nextInput != null) {
					tpUserInput.setText(nextInput);
				}
			}
		});
	}

	private void setupPanels() {
		inputPanel = new JPanel();
		inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
		inputPanel.setLayout(null);
		frmTodokoro.getContentPane().add(inputPanel);
	}

	private void setupTextPane() {
		tpUserInput = new JTextPane();
		inputPanel.add(tpUserInput);
		tpUserInput.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		tpUserInput.setBounds(12, 87, 738, 38);
		tpUserInput.setBorder(getCompoundBorder(4, 4, 0, 4));
		tpUserInput.setFocusAccelerator('e');
		tpUserInput.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {

			}

			public void insertUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	InputFeedbackHandler.getInstance().highlightText();
			        }
				});
			}

			public void removeUpdate(DocumentEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	InputFeedbackHandler.getInstance().highlightText();
			        }
				});
			}
		});

		tfFilter = new JTextField();
		tfFilter.setBounds(594, 12, 156, 26);
		frmTodokoro.getContentPane().add(tfFilter);
		tfFilter.setColumns(10);
		tfFilter.setBorder(getCompoundBorder(0, 4, 0, 4));
		tfFilter.setFocusAccelerator('f');
		tfFilter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				rowFilter(eventsSorter, 3);
				rowFilter(todosSorter, 1);
				rowFilter(deadlinesSorter, 2);
			}

			public void insertUpdate(DocumentEvent e) {
				rowFilter(eventsSorter, 3);
				rowFilter(todosSorter, 1);
				rowFilter(deadlinesSorter, 2);
			}

			public void removeUpdate(DocumentEvent e) {
				rowFilter(eventsSorter, 3);
				rowFilter(todosSorter, 1);
				rowFilter(deadlinesSorter, 2);
			}
		});
	}

	private Border getCompoundBorder(int top, int left, int bottom, int up) {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(top, left, bottom, up);
		return new CompoundBorder(rounded, empty);
	}

	private void setupFilterLabel() {
		lblFilter = new JLabel("Filter:");
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		//lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblFilter.setFont(new Font("Dialog UI", Font.BOLD, 14));
		lblFilter.setBounds(530, 16, 60, 16);
		frmTodokoro.getContentPane().add(lblFilter);
	}

	private void setupStatusMessageTextArea() {
		taStatusMessage = new JTextArea(2, 20);
		taStatusMessage.setBounds(12, 4, 738, 75);
		taStatusMessage.setWrapStyleWord(true);
		taStatusMessage.setLineWrap(true);
		taStatusMessage.setOpaque(false);
		taStatusMessage.setEditable(false);
		taStatusMessage.setFocusable(false);
		taStatusMessage.setBackground(UIManager.getColor("Label.background"));
		//taStatusMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
		taStatusMessage.setFont(new Font("Dialog UI", Font.BOLD, 14));
		//taStatusMessage.setBorder(new TitledBorder(getCompoundBorder(0, 5, 0, 5), "Status Message", 0, 0, new Font("Segoe UI", Font.BOLD, 14)));
		taStatusMessage.setBorder(new TitledBorder(getCompoundBorder(0, 5, 0, 5), "Status Message", 0, 0, new Font("Dialog UI", Font.BOLD, 14)));
		inputPanel.add(taStatusMessage);
	}

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

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(null);
		tabbedPane.setBounds(12, 8, 738, 465);
		eventsScrollPane = new JScrollPane();
		todosScrollPane = new JScrollPane();
		deadlineTasksScrollPane = new JScrollPane();
		eventsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		todosScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		deadlineTasksScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events (1)</b></body></html>", null, eventsScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos (2)</b></body></html>",
							null, todosScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Deadlines (3)</b></body></html>",
							null, deadlineTasksScrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	private void setupTables() {
		setupDeadlineTasksTable();
		setupTodosTable();
		setupEventsTable();
	}

	private void setupEventsTable() {
		eventsTable = new JTable();
		eventsTable.setName("Events");
		eventsTable.setModel(etm);
		setupTableProperties(eventsTable);
		setupRenderersAndEditors(eventsTable);
		setupDimensions(eventsTable);
		eventsScrollPane.setViewportView(eventsTable);
	}

	private void setupTodosTable() {
		todosTable = new JTable();
		todosTable.setName("Todos");
		todosTable.setModel(ttm);
		setupTableProperties(todosTable);
		setupRenderersAndEditors(todosTable);
		setupDimensions(todosTable);
		todosScrollPane.setViewportView(todosTable);
	}

	private void setupDeadlineTasksTable() {
		deadlinesTable = new JTable();
		deadlinesTable.setName("Deadlines");
		deadlinesTable.setModel(dtm);
		setupTableProperties(deadlinesTable);
		setupRenderersAndEditors(deadlinesTable);
		setupDimensions(deadlinesTable);
		deadlineTasksScrollPane.setViewportView(deadlinesTable);
	}

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

	private void rowFilter(TableRowSorter<?> sorter, int index) {
		RowFilter<Object, Object> rowFilter = null;
		List<RowFilter<Object, Object>> rowfilterList = new ArrayList<RowFilter<Object, Object>>();

		try {
			String text = tfFilter.getText();

			if (text.equals("done")) {
				rowFilter = RowFilter.regexFilter("^true$");
			} else if (text.equals("!done") || text.equals("not done") || text.equals("undone")) {
				rowFilter = RowFilter.regexFilter("^false$");
			} else {
				String[] textArray = text.split(" ");

				for (int i = 0; i < textArray.length; i++) {
					rowfilterList.add(RowFilter.regexFilter("(?iu)" + textArray[i], index, index+1));
				}

				rowFilter = RowFilter.andFilter(rowfilterList);
			}
		} catch (PatternSyntaxException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		sorter.setRowFilter(rowFilter);
	}

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
				// impossible case
		}
	}

	private void setupDimensions(JTable table) {
		table.setRowHeight(TABLE_ROW_HEIGHT);
		table.getColumnModel().getColumn(0).setMaxWidth(45);

		switch (table.getName()) {
		case "Events":
			table.getColumnModel().getColumn(1).setMinWidth(130);
			table.getColumnModel().getColumn(1).setMaxWidth(130);
			table.getColumnModel().getColumn(2).setMinWidth(130);
			table.getColumnModel().getColumn(2).setMaxWidth(130);
			table.getColumnModel().getColumn(3).setMinWidth(379);
			table.getColumnModel().getColumn(3).setMaxWidth(684);
			table.getColumnModel().getColumn(4).setMaxWidth(50);
			break;
		case "Todos":
			table.getColumnModel().getColumn(1).setMinWidth(639);
			table.getColumnModel().getColumn(2).setMaxWidth(50);
			break;
		case "Deadlines":
			table.getColumnModel().getColumn(1).setMinWidth(130);
			table.getColumnModel().getColumn(1).setMaxWidth(130);
			table.getColumnModel().getColumn(2).setMinWidth(507);
			table.getColumnModel().getColumn(3).setMaxWidth(50);
			break;
		}
	}

	private void setupTableProperties(JTable table) {
		table.getTableHeader().setReorderingAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
	}

	public void updateStatusMsg(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
	        	taStatusMessage.setText(msg);
	        }
		});
	}

	public void sendUserInput(String command) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_USER_INPUT_CODE, new ObserverEvent.EInput(command)));
	}

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
}

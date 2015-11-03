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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
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
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

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
 * @author Dalton
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
	private static final InputHistory history = InputHistory.getInstance();
	private static EventsTableModel etm = EventsTableModel.getInstance();
	private static TodosTableModel ttm = TodosTableModel.getInstance();
	private static DeadlinesTableModel dtm = DeadlinesTableModel.getInstance();

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

	private static Color normalTextColour = Color.BLACK;
	private static Color highlightedTextColour = Color.RED;

	private static String[] themes = {"bernstein.BernsteinLookAndFeel", "noire.NoireLookAndFeel", "smart.SmartLookAndFeel", "mint.MintLookAndFeel", "mcwin.McWinLookAndFeel"};
	private static int themeIndex = 0;

	private static int tabIndex = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "LookAndFeel: " + e.toString(), e);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//getInstance().addObserver(Logic.getInstance());
					getInstance().addObserver(Logic.getInstance());
					getInstance().frmTodokoro.setVisible(true);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "EventQueue Invoke: " + e.toString(), e);
				}
			}
		});
	}

	public static MainGUI getInstance() {
		if (mainGUI == null) {
			mainGUI = new MainGUI();
		}
		return mainGUI;
	}

	/**
	 * Create the application.
	 */
	private MainGUI() {
		try {
			initialise();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "MainGui Constructor: " + e.toString(), e);
		}

		/*
		 * msgObserver.setOwner(this); tablesObserver.setOwner(this);
		 */
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialise() throws Exception {
		setupTableModels();
		setupMainFrame();
		setupPanels();
		setupTextFields();
		setupLabels();
		setupTabbedPane();
		setupTables();
		setupTableSorters();
	}

	private void setupTableModels() {
		dtm.setMainGui(this);
		etm.setMainGui(this);
		ttm.setMainGui(this);
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setAlwaysOnTop(true);
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);

		frmTodokoro.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				tpUserInput.requestFocusInWindow();
			}
		});

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "Simple Mode");
		frmTodokoro.getRootPane().getActionMap().put("Simple Mode", new AbstractAction() {
			boolean isNormalMode = false;

			public void actionPerformed(ActionEvent e) {
				if (isNormalMode) {
					tabbedPane.setVisible(true);
					lblFilter.setVisible(true);
					tfFilter.setVisible(true);
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
					frmTodokoro.setOpacity(FRAME_OPACITY);
					inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
				} else {
					tabbedPane.setVisible(false);
					lblFilter.setVisible(false);
					tfFilter.setVisible(false);
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_SIMPLE_MODE_WIDTH, FRAME_SIMPLE_MODE_HEIGHT);
					frmTodokoro.setOpacity(FRAME_SIMPLE_MODE_OPACITY);
					inputPanel.setBounds(0, 0, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
				}

				isNormalMode = !isNormalMode;
			}
		});

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "Change Directory");
		frmTodokoro.getRootPane().getActionMap().put("Change Directory", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				ChangeDirectory cd = new ChangeDirectory(frmTodokoro);
			}
		});

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, InputEvent.CTRL_DOWN_MASK), "Cycle Themes");
		frmTodokoro.getRootPane().getActionMap().put("Cycle Themes", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (themeIndex == themes.length-1) {
					themeIndex = 0;
				} else {
					themeIndex++;
				}
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf." + themes[themeIndex]);
					switch (themeIndex) {
						case 1:
							normalTextColour = Color.WHITE;
							highlightedTextColour = Color.ORANGE;
							break;
						default:
							normalTextColour = Color.BLACK;
							highlightedTextColour = Color.RED;
					}
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InstantiationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SwingUtilities.updateComponentTreeUI(frmTodokoro);
				eventsTable.setRowHeight(40);
				todosTable.setRowHeight(40);
				deadlinesTable.setRowHeight(40);
			}
		});

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK), "Cycle Tabs");
		frmTodokoro.getRootPane().getActionMap().put("Cycle Tabs", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (tabIndex == 2) {
					tabIndex = 0;
				} else {
					tabIndex++;
				}
				tabbedPane.setSelectedIndex(tabIndex);
			}
		});

		/*
		GetHelpList demo = new GetHelpList();
		demo.setBounds(768, 0, 240, 600);
		demo.setVisible(true);
		frmTodokoro.getContentPane().add(demo);

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "Help List");
		frmTodokoro.getRootPane().getActionMap().put("Help List", new AbstractAction() {
			boolean isSimpleMode = false;
			public void actionPerformed(ActionEvent e) {
				if (isSimpleMode) {
					tfUserInput.requestFocusInWindow();
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_WIDTH, FRAME_HEIGHT);
				} else {
					demo.requestListFocus();
					frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), FRAME_HELP_LIST_WIDTH, FRAME_HELP_LIST_HEIGHT);
				}

				isSimpleMode = !isSimpleMode;
				//GetHelpList.createAndShowGUI(frmTodokoro.getWidth(), 0);
			}
		});*/
	}

	private void highlightText() {
		String[] keywords = {"update", "delete", "display", "undo", "redo", "exit", "!done", "done"};
		String[] days = {"mon", "tue", "wed", "thurs", "fri", "sat", "sun", "monday", "tueday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        String input = tpUserInput.getText();

        SimpleAttributeSet defaultSet = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultSet, normalTextColour);
        tpUserInput.getStyledDocument().setCharacterAttributes(0, input.length(), defaultSet, true);
        SimpleAttributeSet customSet = new SimpleAttributeSet();
        StyleConstants.setForeground(customSet, highlightedTextColour);
        if (!tpUserInput.getText().isEmpty()) {
        	taStatusMessage.setText(null);
        }

        for (String keyword : keywords) {
            Pattern pattern = Pattern.compile("(?ui)^" + keyword);
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {

            	/*
                System.out.print("Start index: " + matcher.start());
                System.out.print(" End index: " + matcher.end());
                System.out.println(" Found: " + matcher.group());
                */

                tpUserInput.getStyledDocument().setCharacterAttributes(matcher.start(), keyword.length(), customSet, true);
            }
        }

        for (String day : days) {
            Pattern pattern = Pattern.compile("(?ui)" + day + "\\s+(\\d+|(?:Jan(?:uary)?|Feb(?:ruary)?|"
            									+ "Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|"
            									+ "Sep(?:tember)?|Oct(?:ober)?|(Nov|Dec)(?:ember)?))");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                tpUserInput.getStyledDocument().setCharacterAttributes(matcher.start(), day.length(), customSet, true);
                taStatusMessage.setText("Special case detected. Please surround your task description with double quotes to prevent parsing errors. (e.g. \"lunch with john\" at 1pm)");
            }
        }
    }

	private void setupPanels() {
		inputPanel = new JPanel();
		inputPanel.setBounds(0, 475, INPUT_PANEL_WIDTH, INPUT_PANEL_HEIGHT);
		inputPanel.setLayout(null);
		frmTodokoro.getContentPane().add(inputPanel);
	}

	private void setupTextFields() {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(4, 4, 0, 4);
		Border border = new CompoundBorder(rounded, empty);
		tpUserInput = new JTextPane();
		inputPanel.add(tpUserInput);
		tpUserInput.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
		tpUserInput.setBounds(12, 87, 738, 38);
		tpUserInput.setBorder(border);
		tpUserInput.setFocusAccelerator('e');

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

		tpUserInput.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {

			}

			public void insertUpdate(DocumentEvent e) {
				Runnable doHighlight = new Runnable() {
			        @Override
			        public void run() {
			        	highlightText();
			        }
			    };
			    SwingUtilities.invokeLater(doHighlight);
			}

			public void removeUpdate(DocumentEvent e) {
				Runnable doHighlight = new Runnable() {
			        @Override
			        public void run() {
			        	highlightText();
			        }
			    };
			    SwingUtilities.invokeLater(doHighlight);
			}
		});

		tfFilter = new JTextField();
		tfFilter.setBounds(594, 18, 156, 26);
		frmTodokoro.getContentPane().add(tfFilter);
		tfFilter.setColumns(10);
		Border inner = new EmptyBorder(0, 4, 0, 4);
		Border compBorder = new CompoundBorder(rounded, inner);
		tfFilter.setBorder(compBorder);
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

	private void setupLabels() {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(0, 5, 0, 5);
		Border border = new CompoundBorder(rounded, empty);
		TitledBorder titled = new TitledBorder(border, "Status Message", 0, 0, new Font("Segoe UI", Font.BOLD, 14));
		taStatusMessage = new JTextArea(2, 20);
		taStatusMessage.setBounds(12, 4, 738, 75);
		taStatusMessage.setWrapStyleWord(true);
		taStatusMessage.setLineWrap(true);
		taStatusMessage.setOpaque(false);
		taStatusMessage.setEditable(false);
		taStatusMessage.setFocusable(false);
		taStatusMessage.setBackground(UIManager.getColor("Label.background"));
		taStatusMessage.setFont(new Font("Segoe UI", Font.BOLD, 14));
		taStatusMessage.setBorder(titled);
		inputPanel.add(taStatusMessage);

		/*tpStatusMessage = new JTextPane();
		tpStatusMessage.setBounds(12, 4, 738, 75);
		tpStatusMessage.setOpaque(false);
		tpStatusMessage.setEditable(false);
		tpStatusMessage.setFocusable(false);
		tpStatusMessage.setMaximumSize(new Dimension(738, 75));
		tpStatusMessage.setBackground(UIManager.getColor("Label.background"));
		TitledBorder titled = new TitledBorder("Status Message");
		UIManager.put("TitledBorder.border", border);
		tpStatusMessage.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		tpStatusMessage.setBorder(titled);
		inputPanel.add(tpStatusMessage);*/

		//lblStatusMsg = new JLabel();
		//TitledBorder titled = new TitledBorder("Status Message");
		//UIManager.put("TitledBorder.border", new LineBorder(new Color(200,200,200), 2));
		//lblStatusMsg.setBorder(titled);
		//lblStatusMsg.setVerticalAlignment(SwingConstants.TOP);
		//lblStatusMsg.setHorizontalAlignment(SwingConstants.LEFT);
		//lblStatusMsg.setPreferredSize(new Dimension(1, 1));
		//lblStatusMsg.setVerticalAlignment(SwingConstants.CENTER);
		//lblStatusMsg.setHorizontalAlignment(SwingConstants.CENTER);
		//lblStatusMsg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		// lblStatusMsg.setBounds(67, 484, 683, 39);
		//lblStatusMsg.setBounds(298, 35, 738, 79);
		//inputPanel.add(lblStatusMsg);

		lblFilter = new JLabel("Filter:");
		lblFilter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblFilter.setBounds(530, 22, 60, 16);
		frmTodokoro.getContentPane().add(lblFilter);
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
		//tabbedPane.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
		tabbedPane.setBounds(12, 12, 738, 462);
		eventsScrollPane = new JScrollPane();
		todosScrollPane = new JScrollPane();
		deadlineTasksScrollPane = new JScrollPane();
		eventsScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		todosScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		deadlineTasksScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events (1)</b></body></html>",
							null, eventsScrollPane, null);
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
			logger.log(Level.SEVERE, "Row Filter:" + e.getMessage());
		}

		sorter.setRowFilter(rowFilter);
	}

	public void updateTables(EnumTypes.TASK_TYPE type, List<Task> tasks, boolean shouldSwitch) {
		switch (type) {
		case EVENT:
			etm.setTasks(tasks);
			etm.fireTableDataChanged();
			if (shouldSwitch) {
				tabbedPane.setSelectedIndex(0);
			}
			break;
		case TODO:
			ttm.setTasks(tasks);
			ttm.fireTableDataChanged();
			if (shouldSwitch) {
				tabbedPane.setSelectedIndex(1);
			}
			break;
		case DEADLINE:
			dtm.setTasks(tasks);
			dtm.fireTableDataChanged();
			if (shouldSwitch) {
				tabbedPane.setSelectedIndex(2);
			}
			break;
		default:
		}
	}

	private void setupDimensions(JTable table) {
		table.setRowHeight(40);
		table.getColumnModel().getColumn(0).setMaxWidth(45);

		switch (table.getName()) {
		case "Events":
			table.getColumnModel().getColumn(1).setMinWidth(123);
			table.getColumnModel().getColumn(1).setMaxWidth(123);
			table.getColumnModel().getColumn(2).setMinWidth(123);
			table.getColumnModel().getColumn(2).setMaxWidth(123);
			table.getColumnModel().getColumn(3).setMinWidth(393);
			table.getColumnModel().getColumn(3).setMaxWidth(700);
			table.getColumnModel().getColumn(4).setMaxWidth(50);
			break;
		case "Todos":
			table.getColumnModel().getColumn(1).setMinWidth(639);
			table.getColumnModel().getColumn(2).setMaxWidth(50);
			break;
		case "Deadlines":
			table.getColumnModel().getColumn(1).setMinWidth(123);
			table.getColumnModel().getColumn(1).setMaxWidth(123);
			table.getColumnModel().getColumn(2).setMinWidth(516);
			table.getColumnModel().getColumn(3).setMaxWidth(50);
			break;
		}
	}

	private void setupTableProperties(JTable table) {
		// table.setAutoCreateRowSorter(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		table.setShowVerticalLines(false);
		table.setFillsViewportHeight(true);
	}

	public void fakeInputComeIn(String command) {
		sendUserInput(command);
	}

	public void updateStatusMsg(String msg) {
		//lblStatusMsg.setText("<html><body style='width:100px'>" + msg + "</body></html>");
		taStatusMessage.setText(msg);
		//tpStatusMessage.setText(msg);
	}

	private void sendUserInput(String command) {
		setChanged();
		notifyObservers(new ObserverEvent(ObserverEvent.CHANGE_USER_INPUT_CODE, new ObserverEvent.EInput(command)));
	}

	@Override
	public void update(Observable observable, Object event) {

		ObserverEvent OEvent = (ObserverEvent) event;

		if (OEvent.getCode() == ObserverEvent.CHANGE_MESSAGE_CODE) {
			ObserverEvent.EMessage eMessage = (ObserverEvent.EMessage) OEvent.getPayload();
			//System.out.println(eMessage.getMessage());
			updateStatusMsg(eMessage.getMessage());
			return;
		}

		if (OEvent.getCode() == ObserverEvent.CHANGE_TABLE_CODE) {
			ObserverEvent.ETasks eTasks = (ObserverEvent.ETasks) OEvent.getPayload();
			//System.out.println(eTasks.getTaskType());
			updateTables(eTasks.getTaskType(), eTasks.getTasks(), eTasks.shouldSwitch());
			return;
		}

	}
}

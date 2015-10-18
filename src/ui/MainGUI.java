/**
 *
 */
package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import com.sun.glass.ui.Window;

import logic.Logic;
import models.Commands;
import models.DeadlinesTableModel;
import models.EventsTableModel;
import models.TodosTableModel;
import javax.swing.JPanel;

/**
 * @author Dalton
 *
 */
public class MainGUI {

	private JFrame frmTodokoro;
	JPanel simpleModePanel;
	private JTextField tfUserInput, tfFilter;
	private JLabel lblStatusMsg, lblFilter;
	private JTable eventsTable, todosTable, deadlinesTable;
	private JTabbedPane tabbedPane;
	private JScrollPane eventsScrollPane, todosScrollPane, deadlineTasksScrollPane;
	private JLabel lblStatus;
	private TableRowSorter eventsSorter, todosSorter, deadlinesSorter;
	private static final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	private static final CustomCellRenderer customRenderer = new CustomCellRenderer();
	private static final InputObservable inputObservable = InputObservable.getInstance();
	private static final MessageObserver msgObserver = MessageObserver.getInstance();
	private static final TableModelsObserver tablesObserver = TableModelsObserver.getInstance();
	private static final Logger logger = Logger.getLogger(MainGUI.class.getName());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "LookAndFeel: " + e.toString(), e);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frmTodokoro.setVisible(true);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "EventQueue Invoke: " + e.toString(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI() {
		try {
			initialize();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "MainGUI Constructor: " + e.toString(), e);
		}
		msgObserver.setOwner(this);
		tablesObserver.setOwner(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception {
		setupMainFrame();
		setupPanels();
		setupTextFields();
		setupLabels();
		setupTabbedPane();
		setupTables();
		setupTableSorters();
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setAlwaysOnTop(true);
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(0, 0, 768, 620);
		frmTodokoro.setLocationRelativeTo(null);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);

		frmTodokoro.addWindowListener(new WindowAdapter() {
		    public void windowOpened(WindowEvent e) {
		    	tfUserInput.requestFocusInWindow();
		    }
		});

		frmTodokoro.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "SimpleMode");
		frmTodokoro.getRootPane().getActionMap().put("SimpleMode", new AbstractAction() {
			boolean isSimpleMode = false;

            public void actionPerformed(ActionEvent e) {
            	if (isSimpleMode) {
	            	tabbedPane.setVisible(true);
	            	lblFilter.setVisible(true);
	            	tfFilter.setVisible(true);
	            	frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), 768, 620);
	            	frmTodokoro.setOpacity(1f);
	            	simpleModePanel.setBounds(0, 475, 762, 117);
            	} else {
            		tabbedPane.setVisible(false);
	            	lblFilter.setVisible(false);
	            	tfFilter.setVisible(false);
	            	frmTodokoro.setBounds(frmTodokoro.getX(), frmTodokoro.getY(), 768, 147);
	            	frmTodokoro.setOpacity(0.9f);
	            	simpleModePanel.setBounds(0, 0, 762, 117);
            	}

            	isSimpleMode = !isSimpleMode;
            }
        });
	}

	private void setupPanels() {
		simpleModePanel = new JPanel();
		simpleModePanel.setBounds(0, 475, 762, 117);
		frmTodokoro.getContentPane().add(simpleModePanel);
		simpleModePanel.setLayout(null);

	}

	private void setupTextFields() {
		Border rounded = new LineBorder(new Color(210,210,210), 3, true);
		Border empty = new EmptyBorder(0, 3, 0, 0);
		Border border = new CompoundBorder(rounded, empty);

		tfUserInput = new JTextField();
		simpleModePanel.add(tfUserInput);
		tfUserInput.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		tfUserInput.setBounds(12, 64, 738, 41);
		tfUserInput.setColumns(10);
		tfUserInput.setBorder(border);
		tfUserInput.setFocusAccelerator('e');

		tfUserInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inputObservable.sendUserInput(tfUserInput.getText());
				tfUserInput.setText(null);
			}
		});

		tfFilter = new JTextField();
		tfFilter.setBounds(594, 18, 156, 26);
		frmTodokoro.getContentPane().add(tfFilter);
		tfFilter.setColumns(10);
		tfFilter.setBorder(border);
		tfFilter.setFocusAccelerator('f');

		tfFilter.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            	rowFilter(eventsSorter);
            	rowFilter(todosSorter);
            	rowFilter(deadlinesSorter);
            }
            public void insertUpdate(DocumentEvent e) {
            	rowFilter(eventsSorter);
            	rowFilter(todosSorter);
            	rowFilter(deadlinesSorter);
            }
            public void removeUpdate(DocumentEvent e) {
            	rowFilter(eventsSorter);
            	rowFilter(todosSorter);
            	rowFilter(deadlinesSorter);
            }
        });
	}

	private void setupLabels() {
		lblStatusMsg = new JLabel("");
		lblStatusMsg.setVerticalAlignment(SwingConstants.TOP);
		lblStatusMsg.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatusMsg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		//lblStatusMsg.setBounds(67, 484, 683, 39);
		lblStatusMsg.setBounds(67, 12, 683, 39);
		//frmTodokoro.getContentPane().add(lblStatusMsg);
		simpleModePanel.add(lblStatusMsg);

		lblStatus = new JLabel("Status:");
		lblStatusMsg.setLabelFor(lblStatus);
		lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 15));
		//lblStatus.setBounds(12, 484, 53, 21);
		lblStatus.setBounds(12, 12, 53, 21);
		simpleModePanel.add(lblStatus);

		lblFilter = new JLabel("Filter:");
		lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblFilter.setBounds(551, 22, 39, 16);
		frmTodokoro.getContentPane().add(lblFilter);
	}

	private void setupTableSorters() {
		eventsSorter = new TableRowSorter<EventsTableModel>(new EventsTableModel());
		eventsTable.setRowSorter(eventsSorter);
		eventsSorter.toggleSortOrder(1);

		todosSorter = new TableRowSorter<TodosTableModel>(new TodosTableModel());
		todosTable.setRowSorter(todosSorter);
		todosSorter.toggleSortOrder(2);

		deadlinesSorter = new TableRowSorter<DeadlinesTableModel>(new DeadlinesTableModel());
		deadlinesTable.setRowSorter(deadlinesSorter);
		deadlinesSorter.toggleSortOrder(1);
	}

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
		tabbedPane.setBounds(12, 12, 738, 462);
		eventsScrollPane = new JScrollPane();
		todosScrollPane = new JScrollPane();
		deadlineTasksScrollPane = new JScrollPane();
		eventsScrollPane.getVerticalScrollBar().setPreferredSize (new Dimension(0,0));
		todosScrollPane.getVerticalScrollBar().setPreferredSize (new Dimension(0,0));
		deadlineTasksScrollPane.getVerticalScrollBar().setPreferredSize (new Dimension(0,0));
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events</b></body></html>", null, eventsScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos</b></body></html>", null, todosScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Deadlines</b></body></html>", null, deadlineTasksScrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	private void setupTables() {
		setupDeadlineTasksTable();
		setuptodosTable();
		setupEventsTable();
	}

	private void setupEventsTable() {
		eventsTable = new JTable();
		eventsTable.setName("Events");
		eventsTable.setCellSelectionEnabled(true);
		eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eventsTable.setAutoCreateRowSorter(true);
		eventsTable.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		eventsTable.setShowVerticalLines(false);
		eventsTable.setFillsViewportHeight(true);
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		eventsTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		eventsTable.setDefaultRenderer(Integer.class, centerRenderer);
		eventsTable.setDefaultRenderer(String.class, customRenderer);
		eventsTable.setDefaultRenderer(Date.class, customRenderer);
		eventsTable.setDefaultEditor(Date.class, new CustomDateCellEditor());
		eventsScrollPane.setViewportView(eventsTable);
		updateTableInfo(eventsTable, new EventsTableModel());
	}

	private void setuptodosTable() {
		todosTable = new JTable();
		todosTable.setName("Todos");
		todosTable.setCellSelectionEnabled(true);
		todosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		todosTable.setAutoCreateRowSorter(true);
		todosTable.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		todosTable.setShowVerticalLines(false);
		todosTable.setFillsViewportHeight(true);
		todosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		todosTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		todosTable.setDefaultRenderer(Integer.class, centerRenderer);
		todosTable.setDefaultRenderer(String.class, customRenderer);
		todosScrollPane.setViewportView(todosTable);
		updateTableInfo(todosTable, new TodosTableModel());
	}

	private void setupDeadlineTasksTable() {
		deadlinesTable = new JTable();
		deadlinesTable.setName("Deadlines");
		deadlinesTable.setCellSelectionEnabled(true);
		deadlinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		deadlinesTable.setAutoCreateRowSorter(true);
		deadlinesTable.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		deadlinesTable.setShowVerticalLines(false);
		deadlinesTable.setFillsViewportHeight(true);
		deadlinesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		deadlinesTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		deadlinesTable.setDefaultRenderer(Integer.class, centerRenderer);
		deadlinesTable.setDefaultRenderer(String.class, customRenderer);
		deadlinesTable.setDefaultRenderer(Date.class, customRenderer);
		deadlinesTable.setDefaultEditor(Date.class, new CustomDateCellEditor());
		deadlineTasksScrollPane.setViewportView(deadlinesTable);
		updateTableInfo(deadlinesTable, new DeadlinesTableModel());
	}

	private void rowFilter(TableRowSorter sorter) {
		RowFilter<Object, Object> rowFilter = null;
		List<RowFilter<Object,Object>> rowfilterList = new ArrayList<RowFilter<Object,Object>>();

		try {
			String text = tfFilter.getText();

			if (text.equals("done")) {
				rowFilter = RowFilter.regexFilter("^true");
		    } else if (text.equals("!done") || text.equals("not done") || text.equals("undone")) {
		    	rowFilter = RowFilter.regexFilter("^false");
		    } else {
		    	String[] textArray = text.split(" ");

			    for (int i = 0; i < textArray.length; i++) {
			    	rowfilterList.add(RowFilter.regexFilter("(?iu)" + textArray[i]));
			    }

			    rowFilter = RowFilter.andFilter(rowfilterList);
		    }
		} catch (PatternSyntaxException e) {
			logger.log(Level.SEVERE, "Row Filter:" + e.getMessage());
		}

		sorter.setRowFilter(rowFilter);
	}

	private void updateAllTables() {
		updateTableInfo(deadlinesTable, new DeadlinesTableModel());
		updateTableInfo(todosTable, new TodosTableModel());
		updateTableInfo(eventsTable, new EventsTableModel());
	}

	public void reloadTables(Commands.TASK_TYPE type) {
		switch (type) {
			case EVENT:
				updateTableInfo(eventsTable, new EventsTableModel());
				break;
			case TODO:
				updateTableInfo(todosTable, new TodosTableModel());
				break;
			case DEADLINE:
				updateTableInfo(deadlinesTable, new DeadlinesTableModel());
				break;
			default:
				updateAllTables();
		}
		setupTableSorters();
	}

	private void updateTableInfo(JTable table, Object model) {
		table.setModel((AbstractTableModel)model);
		setColWidth(table);
		setTabFocus(table);
	}

	private void setTabFocus(JTable table) {
		switch (table.getName()) {
			case "Events":
				tabbedPane.setSelectedIndex(0);
				//eventsTable.setRowSelectionInterval(0, 0);
				//eventsTable.requestFocusInWindow();
				break;
			case "Todos":
				tabbedPane.setSelectedIndex(1);
				//todosTable.setRowSelectionInterval(0, 0);
				//todosTable.requestFocusInWindow();
				break;
			case "Deadlines":
				tabbedPane.setSelectedIndex(2);
				//deadlineTasksTable.setRowSelectionInterval(0, 0);
				//deadlineTasksTable.requestFocusInWindow();
				break;
		}
	}

	private void setColWidth(JTable table) {
		switch (table.getName()) {
			case "Events":
				eventsTable.getColumnModel().getColumn(0).setMaxWidth(45);
				eventsTable.getColumnModel().getColumn(1).setMinWidth(123);
				eventsTable.getColumnModel().getColumn(1).setMaxWidth(123);
				eventsTable.getColumnModel().getColumn(2).setMinWidth(123);
				eventsTable.getColumnModel().getColumn(2).setMaxWidth(123);
				eventsTable.getColumnModel().getColumn(3).setMinWidth(393);
				eventsTable.getColumnModel().getColumn(3).setMaxWidth(700);
				eventsTable.getColumnModel().getColumn(4).setMaxWidth(50);
				break;
			case "Todos":
				todosTable.getColumnModel().getColumn(0).setMaxWidth(45);
				todosTable.getColumnModel().getColumn(1).setMinWidth(639);
				todosTable.getColumnModel().getColumn(2).setMaxWidth(50);
				break;
			case "Deadlines":
				deadlinesTable.getColumnModel().getColumn(0).setMaxWidth(45);
				deadlinesTable.getColumnModel().getColumn(1).setMinWidth(123);
				deadlinesTable.getColumnModel().getColumn(1).setMaxWidth(123);
				deadlinesTable.getColumnModel().getColumn(2).setMinWidth(516);
				deadlinesTable.getColumnModel().getColumn(3).setMaxWidth(50);
				break;
		}
	}

	public void updateStatusMsg(String msg) {
		lblStatusMsg.setText(msg);
	}
}

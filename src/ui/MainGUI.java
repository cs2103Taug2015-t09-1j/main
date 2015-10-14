/**
 *
 */
package ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import logic.Logic;
import models.DeadlineTask;
import models.DeadlineTasksTableModel;
import models.Event;
import models.EventsTableModel;
import models.FloatingTask;
import models.FloatingTasksTableModel;
import models.Task;

/**
 * @author Dalton
 *
 */
public class MainGUI {

	private JFrame frmTodokoro;
	private JTextField tfUserInput;
	private JLabel lblStatusMsg;
	private JTable eventsTable, floatingTasksTable, deadlineTasksTable;
	private JTabbedPane tabbedPane;
	private JScrollPane eventsScrollPane, floatingTasksScrollPane, deadlineTasksScrollPane;
	private JLabel lblStatus;
	private final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	private final Logic logic = Logic.getInstance();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
					window.frmTodokoro.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception {
		//Logic.init();
		setupMainFrame();
		setupTextField();
		setupStatusLabels();
		setupTabbedPane();
		setupTables();
	}

	private void setupTextField() {
		tfUserInput = new JTextField();
		tfUserInput.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		tfUserInput.setBounds(12, 539, 738, 41);
		tfUserInput.setColumns(10);
		Border rounded = new LineBorder(new Color(210,210,210), 3, true);
		Border empty = new EmptyBorder(0, 10, 0, 0);
		Border border = new CompoundBorder(rounded, empty);
		tfUserInput.setBorder(border);
		//tfUserInput.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.BLUE));
		frmTodokoro.getContentPane().add(tfUserInput);

		tfUserInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String statusMsg = logic.processCommand(tfUserInput.getText());
				updateStatusMsg(statusMsg);
				if (statusMsg.contains("Event")) {
					updateTable(eventsTable, new EventsTableModel(logic.getAllEvents()));
				} else if (statusMsg.contains("Todo")) {
					updateTable(floatingTasksTable, new FloatingTasksTableModel(logic.getAllFloatingTasks()));
				} else if (statusMsg.contains("Deadline")){
					updateTable(deadlineTasksTable, new DeadlineTasksTableModel(logic.getAllDeadlineTasks()));
				} else {
					updateAllTables();
				}
				tfUserInput.setText(null);
			}
		});
	}

	private void setupStatusLabels() {
		lblStatusMsg = new JLabel("");
		lblStatusMsg.setVerticalAlignment(SwingConstants.TOP);
		lblStatusMsg.setHorizontalAlignment(SwingConstants.LEFT);
		lblStatusMsg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		lblStatusMsg.setBounds(67, 488, 683, 39);
		frmTodokoro.getContentPane().add(lblStatusMsg);

		lblStatus = new JLabel("Status:");
		lblStatusMsg.setLabelFor(lblStatus);
		lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblStatus.setBounds(12, 487, 53, 21);
		frmTodokoro.getContentPane().add(lblStatus);
	}

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 12, 738, 464);
		eventsScrollPane = new JScrollPane();
		floatingTasksScrollPane = new JScrollPane();
		deadlineTasksScrollPane = new JScrollPane();
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events</b></body></html>", null, eventsScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos</b></body></html>", null, floatingTasksScrollPane, null);
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Deadlines</b></body></html>", null, deadlineTasksScrollPane, null);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(100, 100, 768, 620);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);

		frmTodokoro.addWindowListener(new WindowAdapter() {
		    public void windowOpened(WindowEvent e) {
		    	tfUserInput.requestFocusInWindow();
		    }
		});
	}

	private void setupTables() {
		setupDeadlineTasksTable();
		setupFloatingTasksTable();
		setupEventsTable();
	}

	/*public void updateSingleTable(ArrayList tasks, String type) {
		switch (type.toLowerCase()) {
		case "event":
			eventsTable.setModel(new EventsTableModel(tasks));
			break;
		case "floating":
			floatingTasksTable.setModel(new FloatingTasksTableModel(tasks));
			break;
		default:
			updateAllTables(tasks);
		}
	}*/

	private void setupEventsTable() {
		eventsTable = new JTable();
		eventsTable.setName("Events");
		eventsTable.setAutoCreateRowSorter(true);
		eventsTable.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		eventsTable.setShowVerticalLines(false);
		eventsTable.setShowGrid(false);
		eventsTable.setFillsViewportHeight(true);
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		eventsTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		eventsTable.setDefaultRenderer(Integer.class, centerRenderer);
		eventsTable.setDefaultRenderer(Date.class, centerRenderer);
		eventsTable.setDefaultRenderer(String.class, centerRenderer);
		eventsScrollPane.setViewportView(eventsTable);
		updateTable(eventsTable, new EventsTableModel(logic.getAllEvents()));
		setColWidth(eventsTable);
	}

	private void setupFloatingTasksTable() {
		floatingTasksTable = new JTable();
		floatingTasksTable.setName("Todos");
		floatingTasksTable.setAutoCreateRowSorter(true);
		floatingTasksTable.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		floatingTasksTable.setShowVerticalLines(false);
		floatingTasksTable.setShowGrid(false);
		floatingTasksTable.setFillsViewportHeight(true);
		floatingTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//floatingTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		floatingTasksTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		floatingTasksTable.setDefaultRenderer(Integer.class, centerRenderer);
		floatingTasksTable.setDefaultRenderer(Date.class, centerRenderer);
		floatingTasksTable.setDefaultRenderer(String.class, centerRenderer);
		floatingTasksScrollPane.setViewportView(floatingTasksTable);
		updateTable(floatingTasksTable, new FloatingTasksTableModel(logic.getAllFloatingTasks()));
		setColWidth(floatingTasksTable);
	}

	private void setupDeadlineTasksTable() {
		deadlineTasksTable = new JTable();
		deadlineTasksTable.setName("Deadlines");
		deadlineTasksTable.setAutoCreateRowSorter(true);
		deadlineTasksTable.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
		deadlineTasksTable.setShowVerticalLines(false);
		deadlineTasksTable.setShowGrid(false);
		deadlineTasksTable.setFillsViewportHeight(true);
		deadlineTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//deadlineTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		deadlineTasksTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		deadlineTasksTable.setDefaultRenderer(Integer.class, centerRenderer);
		deadlineTasksTable.setDefaultRenderer(Date.class, centerRenderer);
		deadlineTasksTable.setDefaultRenderer(String.class, centerRenderer);
		deadlineTasksScrollPane.setViewportView(deadlineTasksTable);
		updateTable(deadlineTasksTable, new DeadlineTasksTableModel(logic.getAllDeadlineTasks()));
		setColWidth(deadlineTasksTable);
	}

	private void updateAllTables() {
		updateTable(eventsTable, new EventsTableModel(logic.getAllEvents()));
		setColWidth(eventsTable);
		updateTable(floatingTasksTable, new FloatingTasksTableModel(logic.getAllFloatingTasks()));
		setColWidth(floatingTasksTable);
		updateTable(deadlineTasksTable, new DeadlineTasksTableModel(logic.getAllDeadlineTasks()));
		setColWidth(deadlineTasksTable);
	}

	private void updateTable(JTable table, Object model) {
		table.setModel((AbstractTableModel)model);
		setColWidth(table);
		setTabFocus(table);
	}

	private void setTabFocus(JTable table) {
		switch (table.getName()) {
		case "Events":
			tabbedPane.setSelectedIndex(0);
			break;
		case "Todos":
			tabbedPane.setSelectedIndex(1);
			break;
		case "Deadlines":
			tabbedPane.setSelectedIndex(2);
			break;
		}
	}

	private void setColWidth(JTable table) {
		switch (table.getName()) {
		case "Events":
			eventsTable.getColumnModel().getColumn(0).setMaxWidth(45);
			eventsTable.getColumnModel().getColumn(1).setMinWidth(115);
			eventsTable.getColumnModel().getColumn(1).setMaxWidth(115);
			eventsTable.getColumnModel().getColumn(2).setMinWidth(115);
			eventsTable.getColumnModel().getColumn(2).setMaxWidth(115);
			eventsTable.getColumnModel().getColumn(3).setMinWidth(409);
			eventsTable.getColumnModel().getColumn(3).setMaxWidth(700);
			eventsTable.getColumnModel().getColumn(4).setMaxWidth(50);
			eventsTable.getRowSorter().toggleSortOrder(1);
			eventsTable.getRowSorter().toggleSortOrder(1);
			break;
		case "Todos":
			floatingTasksTable.getColumnModel().getColumn(0).setMaxWidth(45);
			floatingTasksTable.getColumnModel().getColumn(1).setMinWidth(639);
			floatingTasksTable.getColumnModel().getColumn(2).setMaxWidth(50);
			floatingTasksTable.getRowSorter().toggleSortOrder(0);
			floatingTasksTable.getRowSorter().toggleSortOrder(0);
			break;
		case "Deadlines":
			deadlineTasksTable.getColumnModel().getColumn(0).setMaxWidth(45);
			deadlineTasksTable.getColumnModel().getColumn(1).setMinWidth(115);
			deadlineTasksTable.getColumnModel().getColumn(1).setMaxWidth(115);
			deadlineTasksTable.getColumnModel().getColumn(2).setMinWidth(524);
			deadlineTasksTable.getColumnModel().getColumn(3).setMaxWidth(50);
			deadlineTasksTable.getRowSorter().toggleSortOrder(1);
			deadlineTasksTable.getRowSorter().toggleSortOrder(1);
			break;
		}
	}

	public void updateStatusMsg(String msg) {
		lblStatusMsg.setText(msg);
	}
}

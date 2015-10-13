/**
 *
 */
package ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;

import logic.Logic;
import models.DeadlineTask;
import models.DeadlineTasksTableModel;
import models.Event;
import models.EventsTableModel;
import models.FloatingTask;
import models.FloatingTasksTableModel;
import models.ParsedObject;
import models.Task;
import parser.MainParser;

import java.time.LocalDateTime;
import java.util.*;

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
		tfUserInput.setFont(new Font("Segoe UI", Font.BOLD, 14));
		tfUserInput.setBounds(12, 508, 614, 29);
		tfUserInput.setColumns(10);
		tfUserInput.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.BLACK, Color.BLUE));
		frmTodokoro.getContentPane().add(tfUserInput);

		frmTodokoro.addWindowListener(new WindowAdapter() {
		    public void windowOpened(WindowEvent e) {
		    	tfUserInput.requestFocusInWindow();
		    }
		});

		tfUserInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//ParsedObject test = MainParser.getInstance().parseCommand(tfUserInput.getText());
				//System.out.println(test);
				logic.processCommand(tfUserInput.getText());
				lblStatusMsg.setText(null);
				tfUserInput.setText(null);
			}
		});
	}

	private void setupStatusLabels() {
		lblStatusMsg = new JLabel("");
		lblStatusMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatusMsg.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblStatusMsg.setBounds(67, 480, 559, 29);
		frmTodokoro.getContentPane().add(lblStatusMsg);

		lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblStatus.setBounds(12, 480, 43, 29);
		frmTodokoro.getContentPane().add(lblStatus);
	}

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 12, 614, 464);
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
		frmTodokoro.setBounds(100, 100, 644, 577);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);
	}

	private void setupTables() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		eventsTable = new JTable();
		eventsTable.setAutoCreateRowSorter(true);
		eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		eventsTable.setShowVerticalLines(false);
		eventsTable.setShowGrid(false);
		eventsTable.setFillsViewportHeight(true);
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		eventsTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		eventsTable.setDefaultRenderer(Integer.class, centerRenderer);
		eventsTable.setDefaultRenderer(LocalDateTime.class, centerRenderer);
		eventsTable.setDefaultRenderer(String.class, centerRenderer);
		eventsScrollPane.setViewportView(eventsTable);
		eventsTable.setModel(new EventsTableModel(new Vector<Event>()));
		eventsTable.getColumnModel().getColumn(0).setMaxWidth(45);
		eventsTable.getColumnModel().getColumn(1).setMinWidth(100);
		eventsTable.getColumnModel().getColumn(1).setMaxWidth(100);
		eventsTable.getColumnModel().getColumn(2).setMinWidth(100);
		eventsTable.getColumnModel().getColumn(2).setMaxWidth(100);
		eventsTable.getColumnModel().getColumn(3).setMinWidth(315);
		eventsTable.getColumnModel().getColumn(3).setMaxWidth(600);
		eventsTable.getColumnModel().getColumn(4).setMaxWidth(50);
		eventsTable.getRowSorter().toggleSortOrder(1);

		floatingTasksTable = new JTable();
		floatingTasksTable.setAutoCreateRowSorter(true);
		floatingTasksTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		floatingTasksTable.setShowVerticalLines(false);
		floatingTasksTable.setShowGrid(false);
		floatingTasksTable.setFillsViewportHeight(true);
		floatingTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//floatingTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		floatingTasksTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		floatingTasksTable.setDefaultRenderer(Integer.class, centerRenderer);
		floatingTasksTable.setDefaultRenderer(LocalDateTime.class, centerRenderer);
		floatingTasksTable.setDefaultRenderer(String.class, centerRenderer);
		floatingTasksScrollPane.setViewportView(floatingTasksTable);
		floatingTasksTable.setModel(new FloatingTasksTableModel(new Vector<FloatingTask>()));
		floatingTasksTable.getColumnModel().getColumn(0).setMaxWidth(45);
		floatingTasksTable.getColumnModel().getColumn(1).setMinWidth(515);
		floatingTasksTable.getColumnModel().getColumn(2).setMaxWidth(50);
		floatingTasksTable.getRowSorter().toggleSortOrder(0);

		deadlineTasksTable = new JTable();
		deadlineTasksTable.setAutoCreateRowSorter(true);
		deadlineTasksTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		deadlineTasksTable.setShowVerticalLines(false);
		deadlineTasksTable.setShowGrid(false);
		deadlineTasksTable.setFillsViewportHeight(true);
		deadlineTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//deadlineTasksTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		deadlineTasksTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		deadlineTasksTable.setDefaultRenderer(Integer.class, centerRenderer);
		deadlineTasksTable.setDefaultRenderer(LocalDateTime.class, centerRenderer);
		deadlineTasksTable.setDefaultRenderer(String.class, centerRenderer);
		deadlineTasksScrollPane.setViewportView(deadlineTasksTable);
		deadlineTasksTable.setModel(new DeadlineTasksTableModel(new Vector<DeadlineTask>()));
		deadlineTasksTable.getColumnModel().getColumn(0).setMaxWidth(45);
		deadlineTasksTable.getColumnModel().getColumn(1).setMinWidth(100);
		deadlineTasksTable.getColumnModel().getColumn(1).setMaxWidth(100);
		deadlineTasksTable.getColumnModel().getColumn(2).setMinWidth(415);
		deadlineTasksTable.getColumnModel().getColumn(3).setMaxWidth(50);
		deadlineTasksTable.getRowSorter().toggleSortOrder(1);
	}

	public void updateSingleTable(Vector tasks, String type) {
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
	}

	private void updateAllTables(Vector<Task> tasks) {
		Vector<Event> events = new Vector<Event>();
		Vector<FloatingTask> floatingTasks = new Vector<FloatingTask>();
		Vector<DeadlineTask> deadlineTasks = new Vector<DeadlineTask>();
		eventsTable.setModel(new EventsTableModel(events));
		floatingTasksTable.setModel(new FloatingTasksTableModel(floatingTasks));
		floatingTasksTable.setModel(new DeadlineTasksTableModel(deadlineTasks));
	}

	public void updateStatusMsg(String msg) {
		lblStatusMsg.setText(msg);
	}
}

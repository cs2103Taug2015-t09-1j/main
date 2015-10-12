/**
 *
 */
package ui;

import java.awt.EventQueue;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Window.Type;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import logic.Logic;
import models.FloatingTask;
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
	private JTable eventsTable, todosTable;
	private JTabbedPane tabbedPane;
	private JScrollPane eventsScrollPane, todosScrollPane;
	private JLabel lblStatus;

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
		setupMainFrame();
		setupTabbedPane();
		setupTextField();
		setupTasksTable();
	}

	private void setupTextField() {
		tfUserInput = new JTextField();
		tfUserInput.setFont(new Font("Dialog", Font.PLAIN, 14));
		tfUserInput.setBounds(12, 502, 614, 29);
		tfUserInput.setColumns(10);
		frmTodokoro.getContentPane().add(tfUserInput);

		JLabel lblStatusMsg = new JLabel("");
		lblStatusMsg.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatusMsg.setFont(new Font("Dialog", Font.BOLD, 13));
		lblStatusMsg.setBounds(67, 476, 559, 27);
		frmTodokoro.getContentPane().add(lblStatusMsg);

		lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Dialog", Font.BOLD, 13));
		lblStatus.setBounds(12, 474, 43, 29);
		frmTodokoro.getContentPane().add(lblStatus);


		frmTodokoro.addWindowListener(new WindowAdapter() {
		    public void windowOpened(WindowEvent e) {
		    	tfUserInput.requestFocusInWindow();
		    }
		});

		tfUserInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//updateTables(Logic.processCommand(tfUserInput.getText()));
				//List<Date> test = MainParser.ParseCommand(input);(tfUserInput.getText());
				ParsedObject test = MainParser.getInstance().parseCommand(tfUserInput.getText());
				//System.out.println(test);
				lblStatusMsg.setText(null);
				tfUserInput.setText(null);
				//tfUserInput.setText(MainParser.ParseCommand(tfUserInput.getText()));
			}
		});
	}

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 12, 614, 464);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(100, 100, 644, 571);
		frmTodokoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTodokoro.getContentPane().setLayout(null);
	}

	private void setupTasksTable() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();

		eventsScrollPane = new JScrollPane();
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Events</b></body></html>", null, eventsScrollPane, null);
		eventsTable = new JTable();
		eventsTable.setAutoCreateRowSorter(true);
		eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		eventsTable.setShowVerticalLines(false);
		eventsTable.setShowGrid(false);
		eventsTable.setFillsViewportHeight(true);
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		eventsTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		eventsTable.setDefaultRenderer(Integer.class, centerRenderer);
		eventsTable.setDefaultRenderer(LocalDateTime.class, centerRenderer);
		eventsTable.setDefaultRenderer(String.class, centerRenderer);
		eventsScrollPane.setViewportView(eventsTable);
		eventsTable.setModel(new TasksTableModel(new Vector<Task>(), "Event"));
		eventsTable.getColumnModel().getColumn(0).setMaxWidth(45);
		eventsTable.getColumnModel().getColumn(1).setMinWidth(100);
		eventsTable.getColumnModel().getColumn(1).setMaxWidth(100);
		eventsTable.getColumnModel().getColumn(2).setMinWidth(100);
		eventsTable.getColumnModel().getColumn(2).setMaxWidth(100);
		eventsTable.getColumnModel().getColumn(3).setMinWidth(200);
		eventsTable.getColumnModel().getColumn(4).setMaxWidth(40);
		//eventsTable.getColumnModel().getColumn(2).setMinWidth(70);
		//eventsTable.getColumnModel().getColumn(2).setMaxWidth(70);
		//eventsTable.getColumnModel().getColumn(3).setMinWidth(70);
		//eventsTable.getColumnModel().getColumn(3).setMaxWidth(70);
		//eventsTable.getColumnModel().getColumn(4).setMinWidth(200);
		//eventsTable.getColumnModel().getColumn(5).setMaxWidth(40);

		todosScrollPane = new JScrollPane();
		tabbedPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5><b>Todos</b></body></html>", null, todosScrollPane, null);
		todosTable = new JTable();
		todosTable.setAutoCreateRowSorter(true);
		todosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		todosTable.setShowVerticalLines(false);
		todosTable.setShowGrid(false);
		todosTable.setFillsViewportHeight(true);
		todosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		todosTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		todosTable.setRowHeight(40);
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		todosTable.setDefaultRenderer(Integer.class, centerRenderer);
		todosTable.setDefaultRenderer(LocalDateTime.class, centerRenderer);
		todosTable.setDefaultRenderer(String.class, centerRenderer);
		todosScrollPane.setViewportView(todosTable);
		todosTable.setModel(new TasksTableModel(new Vector<Task>(), "Floating"));
		todosTable.getColumnModel().getColumn(0).setMaxWidth(45);
		todosTable.getColumnModel().getColumn(1).setMinWidth(200);
		todosTable.getColumnModel().getColumn(2).setMaxWidth(40);
	}

	public void updateTables(Vector<Task> tasks, String type) {
		switch (type.toLowerCase()) {
		case "event":
			eventsTable.setModel(new TasksTableModel(tasks, type));
			break;
		case "floating":
			todosTable.setModel(new TasksTableModel(tasks, type));
			break;
		default:
			eventsTable.setModel(new TasksTableModel(new Vector<Task>(), "Event"));
			todosTable.setModel(new TasksTableModel(new Vector<Task>(), "Floating"));
		}
	}

	public void updateStatusMsg(String msg) {
		lblStatusMsg.setText(msg);
	}
}

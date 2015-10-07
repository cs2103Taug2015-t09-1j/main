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

import models.FloatingTask;
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
	private JTable eventsTable, todosTable;
	private JTabbedPane tabbedPane;
	private JScrollPane eventsScrollPane, todosScrollPane;
	//private Vector<Events> deadlineDummy;
	private Vector<FloatingTask> floatingDummy;

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
		setupTempVec();
		setupTasksTable();
		populateTables();
	}

	private void setupTextField() {
		tfUserInput = new JTextField();
		tfUserInput.setBounds(12, 488, 614, 26);
		tfUserInput.setColumns(10);
		frmTodokoro.getContentPane().add(tfUserInput);

		tfUserInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				populateTables();
				//List<Date> test = MainParser.ParseCommand(input);(tfUserInput.getText());
				//System.out.println(test);
				//tfUserInput.setText(null);
				//tfUserInput.setText(MainParser.ParseCommand(tfUserInput.getText()));
			}
		});
	}

	private void setupTabbedPane() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 12, 614, 464);
		frmTodokoro.getContentPane().add(tabbedPane);
	}

	private void setupTempVec() {
		//eventDummy = new Vector<Events>();
		floatingDummy = new Vector<FloatingTask>();
		for (int i = 0; i < 10; i++) {
			//eventDummy.add(new Events(i+1, new Date(), LocalDateTime.now(), "Test"+i, true));
			floatingDummy.add(new FloatingTask("Test"+i, false));
		}
	}

	private void setupMainFrame() {
		frmTodokoro = new JFrame();
		frmTodokoro.setTitle("Todokoro");
		frmTodokoro.setResizable(false);
		frmTodokoro.setBounds(100, 100, 644, 560);
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
	}

	private void populateTables() {
		eventsTable.setModel(new TasksTableModel(eventDummy, "Event"));
		eventsTable.getColumnModel().getColumn(0).setMaxWidth(45);
		eventsTable.getColumnModel().getColumn(1).setMinWidth(100);
		eventsTable.getColumnModel().getColumn(1).setMaxWidth(100);
		eventsTable.getColumnModel().getColumn(2).setMinWidth(70);
		eventsTable.getColumnModel().getColumn(2).setMaxWidth(70);
		eventsTable.getColumnModel().getColumn(3).setMinWidth(70);
		eventsTable.getColumnModel().getColumn(3).setMaxWidth(70);
		eventsTable.getColumnModel().getColumn(4).setMinWidth(200);
		eventsTable.getColumnModel().getColumn(5).setMaxWidth(40);

		todosTable.setModel(new TasksTableModel(floatingDummy, "Floating"));
		todosTable.getColumnModel().getColumn(0).setMaxWidth(45);
		todosTable.getColumnModel().getColumn(1).setMinWidth(200);
		todosTable.getColumnModel().getColumn(2).setMaxWidth(40);
	}
}

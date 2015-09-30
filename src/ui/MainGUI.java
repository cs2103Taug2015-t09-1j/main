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
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Dalton
 *
 */
public class MainGUI {

	private JFrame frame;
	private JTextField tfUserInput;
	private JTable eventsTable, todosTable;
	private Vector<DeadlineTask> dummy;
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
					window.frame.setVisible(true);
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
		setupTempVec();
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
		frame = new JFrame();
		frame.setType(Type.UTILITY);
		frame.setResizable(false);
		frame.setBounds(100, 100, 644, 560);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		tfUserInput = new JTextField();
		tfUserInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfUserInput.setText(null);
				System.out.println("some action");
				setupTasksTable();
			}
		});
		tfUserInput.setBounds(12, 488, 614, 26);
		frame.getContentPane().add(tfUserInput);
		tfUserInput.setColumns(10);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(12, 19, 614, 457);
		frame.getContentPane().add(tabbedPane);

		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Events", null, scrollPane, null);

		setupTasksTable();
		scrollPane.setViewportView(eventsTable);

		JScrollPane todosScrollPane = new JScrollPane();
		tabbedPane.addTab("Todos", null, todosScrollPane, null);

		todosTable = new JTable();
		todosTable.setShowVerticalLines(false);
		todosTable.setShowGrid(false);
		todosTable.setRowHeight(30);
		todosTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		todosTable.setFillsViewportHeight(true);
		todosTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		todosScrollPane.setViewportView(todosTable);
	}

	private void setupTempVec() {
		dummy = new Vector<DeadlineTask>();
		for (int i = 0; i < 10; i++) {
			dummy.add(new DeadlineTask(i+1, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), "Test"+i, true));
		}
	}

	private void setupTasksTable() {
		eventsTable = new JTable();
		eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		eventsTable.setShowVerticalLines(false);
		eventsTable.setShowGrid(false);
		eventsTable.setFillsViewportHeight(true);
		eventsTable.setModel(new TasksTableModel(dummy));
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		eventsTable.setRowHeight(40);
		eventsTable.getColumnModel().getColumn(0).setMaxWidth(45);
		eventsTable.getColumnModel().getColumn(1).setMinWidth(100);
		eventsTable.getColumnModel().getColumn(1).setMaxWidth(100);
		eventsTable.getColumnModel().getColumn(2).setMinWidth(70);
		eventsTable.getColumnModel().getColumn(2).setMaxWidth(70);
		eventsTable.getColumnModel().getColumn(3).setMinWidth(70);
		eventsTable.getColumnModel().getColumn(3).setMaxWidth(70);
		eventsTable.getColumnModel().getColumn(4).setMinWidth(200);
		eventsTable.getColumnModel().getColumn(5).setMaxWidth(40);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
		eventsTable.setDefaultRenderer(Integer.class, centerRenderer);
		eventsTable.setDefaultRenderer(LocalDateTime.class, centerRenderer);
		eventsTable.setDefaultRenderer(String.class, centerRenderer);
	}
}

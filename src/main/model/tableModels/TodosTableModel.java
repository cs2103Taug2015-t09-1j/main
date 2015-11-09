/*
 *
 */
package main.model.tableModels;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.ui.MainGUI;

/**
 * The Class TodosTableModel.
 * The table model for the Todos Table.
 * Handles the user UI interactions of the JTable.
 *
 * @@author Dalton
 */
@SuppressWarnings("serial")
public class TodosTableModel extends AbstractTableModel {
	private static TodosTableModel ttm = null;
	private static MainGUI mainGUI = null;
	private List<Task> todos = null;
	private final String[] columnNames = { "ID", "Task Description (2)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, String.class, Boolean.class };

	/**
	 * Instantiates a new todos table model.
	 */
	private TodosTableModel() {
		super();
		todos = new ArrayList<Task>();
	}

	/**
	 * Gets the single instance of TodosTableModel.
	 *
	 * @return single instance of TodosTableModel
	 */
	public static TodosTableModel getInstance() {
		if (ttm == null) {
			ttm = new TodosTableModel();
		}
		return ttm;
	}

	/**
	 * Sets the UI instance.
	 *
	 * @param ui	the new UI instance
	 */
	public void setUIInstance(MainGUI ui) {
		assert ui != null;
		mainGUI = ui;
	}

	/**
	 * Sets the tasks.
	 *
	 * @param tasks	the new tasks
	 */
	public void setTasks(List<Task> tasks) {
		assert tasks != null;
		todos = tasks;
	}

	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class<?> getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return todos.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
        	case 1:
        		return true;
        	case 2:
        		return true;
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Todo todo = (Todo)todos.get(row);
		String simulatedCommand = "update " + todo.getTaskID() + " " + (col + 1) + " ";
		String updatedValue = "";
		String originalValue = "";
		switch (col) {
			case 1:
				originalValue += todo.getTaskDesc();
				updatedValue += value;
				break;
			case 2:
				originalValue += value;
				simulatedCommand = ((Boolean)value ?  "done" : "undone") + " " + todo.getTaskID();
				break;
			default:
				// impossible case
				assert false : col;
		}

		// Sends a simulated command if the user updates from the table directly
		if (!updatedValue.trim().equals(originalValue)) {
			mainGUI.sendUserInput(simulatedCommand + updatedValue);
		}
    }

	public Object getValueAt(int row, int col) {
		Todo todo = (Todo)todos.get(row);
		switch (col) {
			case 0:
				return todo.getTaskID();
			case 1:
				return todo.getTaskDesc();
			case 2:
				return todo.isDone();
			default:
				// impossible case
				assert false : col;
		}

		return new String();
	}
}
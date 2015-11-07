package main.model.tableModels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.storage.Storage;
import main.ui.MainGUI;

/**
 * @@author Dalton
 *
 */
@SuppressWarnings("serial")
public class TodosTableModel extends AbstractTableModel {
	private static TodosTableModel ttm = null;
	private static MainGUI mainGUI = null;
	private List<Task> todos = null;
	private final String[] columnNames = { "ID", "Task Description (2)", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, String.class, Boolean.class };

	private TodosTableModel(MainGUI ui) {
		super();
		mainGUI = ui;
		todos = new ArrayList<Task>();
	}

	public static TodosTableModel getInstance(MainGUI ui) {
		if (ttm == null) {
			assert ui != null;
			ttm = new TodosTableModel(ui);
		}
		return ttm;
	}

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
		Todo t = (Todo)todos.get(row);
		String simulatedCommand = "update " + t.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				simulatedCommand = simulatedCommand + (String)value;
				break;
			case 2:
				simulatedCommand = ((Boolean)value ?  "done" : "undone") + " " + t.getTaskID();
				break;
			default:
				// impossible case
		}

		mainGUI.sendUserInput(simulatedCommand);
    }

	public Object getValueAt(int row, int col) {
		Todo t = (Todo)todos.get(row);
		switch (col) {
			case 0:
				return t.getTaskID();
			case 1:
				return t.getTaskDesc();
			case 2:
				return t.isDone();
			default:
				// impossible case
		}

		return new String();
	}
}
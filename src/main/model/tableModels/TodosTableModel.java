/**
 *
 */
package main.model.tableModels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.model.EnumTypes.TASK_TYPE;
import main.model.taskModels.Task;
import main.model.taskModels.Todo;
import main.storage.Storage;
import main.ui.MainGui;

/**
 * @author Dalton
 *
 */
public class TodosTableModel extends AbstractTableModel {
	private static TodosTableModel ttm = TodosTableModel.getInstance();
	private final String[] columnNames = { "ID", "Task Description", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, String.class, Boolean.class };
	private List<Task> todos = new ArrayList<>();
	private MainGui mainGui;

	public TodosTableModel() {
		super();
	}
	
	public void setTasks(List<Task> tasks) {
		this.todos = tasks;
	}

	public static TodosTableModel getInstance() {
		if (ttm == null) {
			ttm = new TodosTableModel();
		}
		return ttm;
	}
	
	public void setMainGui(MainGui mainGui) {
		this.mainGui = mainGui;
	}
	
	public int getColumnCount() {
		return columnNames.length;
    }

	public String getColumnName(int col) {
		return columnNames[col];
	}

    public Class getColumnClass(int col) {
		return columnTypes[col];
	}

	public int getRowCount() {
	    return todos.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
        	case 2:
        		return true;
            default:
                return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Todo t = (Todo)todos.get(row);
		Boolean shouldProcess = false;
		String fakeCommand = "update " + t.getTaskID() + " " + (col + 1) + " ";
		switch (col) {
			case 1:
				shouldProcess = true;
				fakeCommand = fakeCommand + (String)value;
				break;
			case 2:
				shouldProcess = true;
				fakeCommand = fakeCommand + (Boolean)value;
				break;
		}
		if (shouldProcess && mainGui != null) {
			mainGui.fakeInputComeIn(fakeCommand);
		}

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
		}

		return new String();
	}
}
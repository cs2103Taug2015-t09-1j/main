/**
 *
 */
package main.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import main.models.Todo;
import main.models.EnumTypes.TASK_TYPE;
import main.storage.Storage;

/**
 * @author Dalton
 *
 */
public class TodosTableModel extends AbstractTableModel {
	private static TodosTableModel ttm = TodosTableModel.getInstance();
	private final String[] columnNames = { "ID", "Task Description", "Done" };
	private final Class<?>[] columnTypes = { Integer.class, String.class, Boolean.class };
	private List<Task> todos = new ArrayList<>();

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
		switch (col) {
			case 1:
				t.setTaskDesc((String) value);
			break;
			case 2:
				t.setDone((Boolean)value);
			break;
		}
		Storage.getInstance().saveTaskType(TASK_TYPE.TODO);
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
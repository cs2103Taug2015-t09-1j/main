/**
 *
 */
package models;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

import logic.Logic;
import models.Todo;
import models.Commands.TASK_TYPE;
import storage.Storage;

/**
 * @author Dalton
 *
 */
public class TodosTableModel extends AbstractTableModel {
	private String[] columnNames = { "ID", "Task Description", "Done" };
	private Class[] columnTypes = { Integer.class, String.class, Boolean.class };
	private Logic logic = Logic.getInstance();

	ArrayList<Todo> todos;

	public TodosTableModel() {
		super();
		this.todos = (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.TODO);
	}

	public TodosTableModel(ArrayList<Todo> todos) {
		super();
		this.todos = todos;
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
		Storage.saveTaskType(TASK_TYPE.TODO);
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
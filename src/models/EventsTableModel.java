/**
 *
 */
package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import models.Event;
import parser.MainParser;
import models.Commands.TASK_TYPE;
import storage.Storage;

/**
 * @author Dalton
 *
 */
public class EventsTableModel extends AbstractTableModel {
	private String[] columnNames = { "ID", "Start Date", "End Date", "Task Description", "Done" };
	private Class<?>[] columnTypes = { Integer.class, Date.class, Date.class, String.class, Boolean.class };

	ArrayList<Event> events;

	public EventsTableModel() {
		super();
		this.events = (ArrayList)Storage.getAllTask(Commands.TASK_TYPE.EVENT);
	}

	public EventsTableModel(ArrayList<Event> events) {
		super();
		this.events = events;
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
	    return events.size();
    }

	public boolean isCellEditable(int row, int col) {
        switch (col) {
	    	case 1:
	    		return true;
	    	case 2:
	    		return true;
        	case 3:
        		return true;
        	case 4:
        		return true;
        	default:
        		return false;
        }
    }

	public void setValueAt(Object value, int row, int col) {
		Event evt = (Event)events.get(row);
		switch (col) {
			case 1:
				evt.setFromDate((Date)value);
				break;
			case 2:
				evt.setToDate((Date)value);
				break;
			case 3:
				evt.setTaskDesc((String)value);
				break;
			case 4:
				evt.setDone((Boolean)value);
				break;
		}
		Storage.saveTaskType(TASK_TYPE.EVENT);
    }

	public Object getValueAt(int row, int col) {
		Event evt = (Event)events.get(row);
		switch (col) {
			case 0:
				return evt.getTaskID();
			case 1:
				return evt.getFromDate();
			case 2:
				if (evt.getFromDate() == evt.getToDate()) {
					return null;
				} else {
					return evt.getToDate();
				}
			case 3:
				return evt.getTaskDesc();
			case 4:
				return evt.isDone();
		}
		return new String();
	}
}
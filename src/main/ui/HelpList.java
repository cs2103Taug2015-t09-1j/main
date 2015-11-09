/*
 * 
 */
package main.ui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JList;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class HelpList.
 *
 * @@author Dalton
 */
public class HelpList extends JPanel {

	//JPanel contentPane;
	ListSelectionModel listSelectionModel;
	JScrollPane textPane;
	JTextPane text;
	JList list;
	String newline = "\n";

	/**
	 * Create the frame.
	 */
	public HelpList() {
		//this.contentPane = new JPanel();
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setBounds(100, 100, 300, 600);
		//setTitle("TodoKoro Help");
		//contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//setContentPane(contentPane);
		//contentPane.setLayout(null);
		//this.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);

		JScrollPane listPane = new JScrollPane();
		listPane.setBounds(6, 30, 259, 138);
		listPane.setBorder(getCompoundBorder(4,4,4,4));
		this.add(listPane);
		//contentPane.add(listPane);

		list = new JList();
		list.setFont(new Font("Avenir Next", Font.PLAIN, 12));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(new AbstractListModel() {
			String[] featureList = new String[] {"Adding a task", "Deleting task(s)", "Updating task information", "Viewing task(s) on a date",
	        		"Marking a task as done/undone","Undo-ing/Redo-ing the previous action(s)","Hotkeys"};
			public int getSize() {
				return featureList.length;
			}
			public Object getElementAt(int index) {
				return featureList[index];
			}
		});
		list.setSelectedIndex(-1);
		list.requestFocus();

		listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
		listPane.setViewportView(list);

		JScrollPane textPane = new JScrollPane();
		textPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		textPane.setBounds(6, 194, 259, 406);
		textPane.setBorder(getCompoundBorder(4,4,4,4));
		//contentPane.add(textPane);
		this.add(textPane);

		text = new JTextPane();
		text.setBounds(6, 194, 259, 406);
		text.setEditable(false);
		text.setFont(new Font("Dialog", Font.PLAIN, 12));
		text.setText("Welcome to Todokoro");

		//textPane.setColumnHeaderView(text);
		textPane.setViewportView(text);

		JLabel lblCommandList = new JLabel("Command List");
		lblCommandList.setFont(new Font("Dialog", Font.BOLD, 14));
		lblCommandList.setBounds(6, 11, 250, 14);
		//contentPane.add(label);
		this.add(lblCommandList);

		JLabel lblNewLabel = new JLabel("Command Details");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		lblNewLabel.setBounds(6, 172, 250, 21);
		add(lblNewLabel);
		//this.add(label);

		InputMap im = list.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap am = list.getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Scroll Up Help List");
		am.put("Scroll Up Help List", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				textPane.getVerticalScrollBar().setValue(textPane.getVerticalScrollBar().getValue()-100);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "Scroll Down Help List");
		am.put("Scroll Down Help List", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				textPane.getVerticalScrollBar().setValue(textPane.getVerticalScrollBar().getValue()+100);
			}
		});
	}

	public void getHelpListFocus() {
		list.requestFocusInWindow();
	}

	private Border getCompoundBorder(int top, int left, int bottom, int up) {
		Border rounded = new LineBorder(new Color(210, 210, 210), 2, true);
		Border empty = new EmptyBorder(top, left, bottom, up);
		return new CompoundBorder(rounded, empty);
	}

	   /**
	 * The Class SharedListSelectionHandler.
	 *
	 * @@author Dalton
	 */
   	class SharedListSelectionHandler implements ListSelectionListener {
	       public void valueChanged(ListSelectionEvent e) {
	    	    ListSelectionModel lsm = (ListSelectionModel)e.getSource();

	        	SimpleAttributeSet highlight = new SimpleAttributeSet();
	        	SimpleAttributeSet highlight_1 = new SimpleAttributeSet();

	        	highlight = new SimpleAttributeSet();
	            StyleConstants.setBold(highlight, true);
	            StyleConstants.setForeground(highlight, Color.red);

	            highlight_1 = new SimpleAttributeSet();
	            StyleConstants.setBold(highlight_1, true);
	            StyleConstants.setForeground(highlight_1, Color.blue);

	            Document doc = text.getStyledDocument();

	       if (lsm.isSelectionEmpty()) {

	    		   text.setText("");

	        } else {
	             // Find out which indexes are selected.
	             int minIndex = lsm.getMinSelectionIndex();
	             int maxIndex = lsm.getMaxSelectionIndex();
	          for (int i = minIndex; i <= maxIndex; i++) {
	               if(lsm.isSelectedIndex(i)) {

	           switch(i){
	              case -1:
	            	  text.setText("Welcome to TodoKoro Help List!");
	            	  break;

	              case 0:
	            	text.setText("");
				try {
					doc.insertString(doc.getLength(), "Add Events:" +newline, highlight);
					doc.insertString(doc.getLength(), "Add a task with a specific date/time"+newline, null);
					doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
					doc.insertString(doc.getLength(), "task description + date/time" +newline, null);
					doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
					doc.insertString(doc.getLength(), "watch movie on wed ;"+newline, null);
					doc.insertString(doc.getLength(), "watch movie 30 sept ;"+newline, null);
					doc.insertString(doc.getLength(), "tomorrow watch movie at 3pm ;"+newline, null);
					doc.insertString(doc.getLength(), "watch movie from 3pm to 5pm ;"+newline+newline,null);

					doc.insertString(doc.getLength(), "Add Todos"  + newline, highlight);
					doc.insertString(doc.getLength(), "Add a task without a specific date/time" +newline, null);
					doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
					doc.insertString(doc.getLength(), "task description" +newline, null);
					doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
					doc.insertString(doc.getLength(), "watch movie ;"+newline, null);
					doc.insertString(doc.getLength(), "do past year exam paper ;"+newline+newline,null);

					doc.insertString(doc.getLength(), "Add Deadlines: "  + newline, highlight);
					doc.insertString(doc.getLength(), "Add a task with a specific due-date/time" +newline, null);
					doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
					doc.insertString(doc.getLength(), "task description + due/due on/due by/by + due-date/time" +newline, null);
					doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
					doc.insertString(doc.getLength(), "pay utility fee by fri ;"+newline, null);
					doc.insertString(doc.getLength(), "Final Report due by 30 sep ;",null);

				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}

	                   break;

	              case 1:
	            	  text.setText("");
	            	  try {

	      				doc.insertString(doc.getLength(), "Single Delete: "  + newline, highlight);
	      				doc.insertString(doc.getLength(), "delete a task"  +newline, null);
	      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "delete/del + taskId"  +newline, null);
	      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "delete 5"+newline, null);
	      				doc.insertString(doc.getLength(), "del 5"+newline+newline, null);

	      				doc.insertString(doc.getLength(), "Multiple Delete:" + newline, highlight);
	      				doc.insertString(doc.getLength(), "delete several tasks in one time"  +newline, null);
	      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "delete + taskId + taskId +..." +newline, null);
	      				doc.insertString(doc.getLength(), "delete + taskId-taskId" +newline, null);
	      				doc.insertString(doc.getLength(), "delete + all" +newline, null);
	      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "delete 5 8 9 23" +newline, null);
	      				doc.insertString(doc.getLength(), "delete 5-23" +newline, null);
	      				doc.insertString(doc.getLength(), "delete all"+newline+newline, null);



	      			} catch (BadLocationException e1) {
	      				e1.printStackTrace();
	      			}

	                   break;

	              case 2:
	            	  text.setText("");
	            	  try {

	        				doc.insertString(doc.getLength(), "Update: "  + newline, highlight);
	        				doc.insertString(doc.getLength(), "update a task information"  +newline, null);
	        				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	        				doc.insertString(doc.getLength(), "update + taskId + update column number + update infromarion"  +newline, null);
	        				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	        				doc.insertString(doc.getLength(), "update 2 4 watch movie with Jason ;"+newline, null);
	        				doc.insertString(doc.getLength(), "update 2 1 Oct 23 ;"+newline, null);

	        			} catch (BadLocationException e1) {
	        				e1.printStackTrace();
	        			}

	                   break;

	              case 3:
	            	  text.setText("");
	            	  try {

	      				doc.insertString(doc.getLength(), "View task(s) on one day: "  + newline, highlight);
	      				doc.insertString(doc.getLength(), "view task(s) on a specific date/view all tasks"  +newline, null);
	      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "view + date / view + all"  +newline, null);
	      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "view today ;"+newline, null);
	      				doc.insertString(doc.getLength(), "view nov 3 ;"+newline+newline, null);

	      				doc.insertString(doc.getLength(), "View all tasks: "  + newline, highlight);
	      				doc.insertString(doc.getLength(), "view all Event tasks (if you are on Events Tag) / view all Deadline tasks (if you are on Deadlines Tag) "  +newline, null);
	      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "view + all"  +newline, null);
	      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "view all ;"+newline, null);


	      			} catch (BadLocationException e1) {
	      				e1.printStackTrace();
	      			}

	            	  break;

	              case 4:
	            	  text.setText("");
	            	  try {

	        				doc.insertString(doc.getLength(), "Done/Undone: " + newline, highlight);
	        				doc.insertString(doc.getLength(), "mark a done task as undone"  +newline, null);
	        				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	        				doc.insertString(doc.getLength(), "done/undone + taskId"+newline, null);
	        				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	        				doc.insertString(doc.getLength(), "done 1"+newline, null);
	        				doc.insertString(doc.getLength(), "undone 1"+newline, null);

	        			} catch (BadLocationException e1) {
	        				e1.printStackTrace();
	        			}

	            	  break;

	              case 5:
	            	  text.setText("");
	            	  try {

	      				doc.insertString(doc.getLength(), "Undo-ing/Redo-ing a single action: " + newline, highlight);
	      				doc.insertString(doc.getLength(), "undo/redo the privious action"  +newline, null);
	      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "undo/redo"+newline, null);
	      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "undo"+newline, null);
	      				doc.insertString(doc.getLength(), "redo"+newline+newline, null);

	      				doc.insertString(doc.getLength(), "Undo-ing/Redo-ing multiple actions: " + newline, highlight);
	      				doc.insertString(doc.getLength(), "undo/redo the privious actions"  +newline, null);
	      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "undo/redo + number"+newline, null);
	      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
	      				doc.insertString(doc.getLength(), "undo 20"+newline, null);
	      				doc.insertString(doc.getLength(), "redo 10"+newline, null);

	      			} catch (BadLocationException e1) {
	      				e1.printStackTrace();
	      			}

	            	  break;

	              case 6:
	            	  text.setText("");
	            	  try{
	            		  doc.insertString(doc.getLength(), "Switching within task tags: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "Events Tag: "+newline, highlight_1);
	            		  doc.insertString(doc.getLength(), "Ctrl+1"  +newline, null);
	            		  doc.insertString(doc.getLength(), "Todos Tag: "+newline, highlight_1);
	            		  doc.insertString(doc.getLength(), "Ctrl+2"  +newline, null);
	            		  doc.insertString(doc.getLength(), "Deadlines Tag: "+newline, highlight_1);
	            		  doc.insertString(doc.getLength(), "Ctrl+3"  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Switching to simple mode: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "F2"  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Choosing save direction: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "F3"  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Exiting save direction: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "Esc"  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Getting help list: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "..."  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Switching between themes: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "..."  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Scrolling down the task screen: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "..."  +newline+newline, null);

	            		  doc.insertString(doc.getLength(), "Scrolling up the task screen: " + newline, highlight);
	            		  doc.insertString(doc.getLength(), "..."  +newline+newline, null);




	            	  }catch (BadLocationException e1) {
		      				e1.printStackTrace();
		      			}

	            	  break;


	          }
	           text.setCaretPosition(0);

	       }
	      }
	     }
	    }
	   }
}



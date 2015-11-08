package main.ui;


import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import java.awt.Font;
import javax.swing.JTextPane;

public class GetHelpList extends JFrame {

	JPanel contentPane;
	ListSelectionModel listSelectionModel;
	JScrollPane textPane;
	JTextPane text;
	String newline = "\n";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GetHelpList frame = new GetHelpList();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GetHelpList() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 600);
		setTitle("TodoKoro Help");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane listPane = new JScrollPane();
		listPane.setBounds(6, 6, 280, 121);
		contentPane.add(listPane);
		
		JList list = new JList();
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
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
		listPane.setViewportView(list);

	
		
		JScrollPane textPane = new JScrollPane();
		textPane.setBounds(6, 148, 280, 400);
		contentPane.add(textPane);
		
		text = new JTextPane();
		text.setText("Welcome to TodoKoro Help !");
		textPane.setColumnHeaderView(text);
		
		
		JLabel label = new JLabel("TodoKoro Help List:");
		label.setFont(new Font("Avenir Next", Font.PLAIN, 13));
		label.setBounds(6, 128, 124, 16);
		contentPane.add(label);
	}
	
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

	        
	       }
	      }
	     }
	    }
	   }
}



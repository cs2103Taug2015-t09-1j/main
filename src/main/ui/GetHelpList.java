package main.ui;


import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;



import java.awt.*;


/**
 * @@author Jia Qi
 *
 */
public class GetHelpList extends JPanel {
    JTextPane output;
    JList list;
    JLabel label;
    String newline = "\n";
    ListSelectionModel listSelectionModel;

    public void requestListFocus() {
    	list.requestFocusInWindow();
    }

    public GetHelpList() {
        super(new BorderLayout());

        String[] listData = {"Adding a task", "Deleting a task", "Updating task information", "Marking a task as done",
                              "Masking a task as undone", "Undo-ing the previous action", "Redo-ing the action after undo" };

        list = new JList(listData);
        list.setSelectedIndex(-1);
        list.requestFocus();



        listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
        JScrollPane listPane = new JScrollPane(list);



        JPanel controlPane = new JPanel();
        JLabel label = new JLabel();
        controlPane.add(label);
        label.setText("TodoKoro Help List:");
        label.setFont(new Font("San Serif", Font.BOLD, 14));


        //Build output area.
        output = new JTextPane();
        output.setEditable(false);
        output.setMargin(new Insets(5, 5, 5, 5));
        output.setText("WELCOME TO TODOKORO!");
        JScrollPane outputPane = new JScrollPane(output,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        //Do the layout.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);

        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1,1));
        listContainer.setBorder(BorderFactory.createTitledBorder(
                                                "Featrue List"));
        listContainer.add(listPane);

        topHalf.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        topHalf.add(listContainer);


        //topHalf.setPreferredSize(new Dimension(100, 170));
        splitPane.add(topHalf);

        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(controlPane, BorderLayout.PAGE_START);
        bottomHalf.add(outputPane, BorderLayout.CENTER);

        //bottomHalf.setPreferredSize(new Dimension(350, 565));
        splitPane.add(bottomHalf);
    }



        //Create the GUI and show it
    public static void createAndShowGUI(int x, int y) {
        //Create and set up the window.
        JFrame frame = new JFrame("TodoKoro Help");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        GetHelpList demo = new GetHelpList();
        //demo.setOpaque(true);
        frame.setContentPane(demo);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 /*
    public static void main(String[] args) {

        //creating and showing this Help List frame.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }*/

    class SharedListSelectionHandler extends JFrame implements ListSelectionListener {
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

            Document doc = output.getStyledDocument();

       if (lsm.isSelectionEmpty()) {

    		   output.setText("");

        } else {
             // Find out which indexes are selected.
             int minIndex = lsm.getMinSelectionIndex();
             int maxIndex = lsm.getMaxSelectionIndex();
          for (int i = minIndex; i <= maxIndex; i++) {
               if(lsm.isSelectedIndex(i)) {

           switch(i){
              case -1:
            	  output.setText("Welcome to TodoKoro Help List!");
            	  break;

              case 0:
            	output.setText("");
			try {
				doc.insertString(doc.getLength(), "Add Events:" +newline, highlight);
				doc.insertString(doc.getLength(), "Add a task with a specific date/time"+newline, null);
				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "task description + date/time" +newline, null);
				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "watch movie on wed ;"+newline, null);
				doc.insertString(doc.getLength(), "watch movie 30 sept ;"+newline, null);
				doc.insertString(doc.getLength(), "watch movie from 3pm to 5pm ;"+newline+newline,null);

				doc.insertString(doc.getLength(), "Add Todos"  + newline, highlight);
				doc.insertString(doc.getLength(), "Add a task without a specific date/time" +newline, null);
				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "task description" +newline, null);
				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "watch movie ;"+newline, null);
				doc.insertString(doc.getLength(), "do past year exam paper ;"+newline+newline,null);

				doc.insertString(doc.getLength(), "Deadlines: "  + newline, highlight);
				doc.insertString(doc.getLength(), "Add a task with a specific due-date/time" +newline, null);
				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "task description + due/due on/due by/by + due-date/time" +newline, null);
				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "pay utility fee by fri ;"+newline, null);
				doc.insertString(doc.getLength(), "Final Report due by 30 sep ;"+newline+newline,null);

				doc.insertString(doc.getLength(), "Notes: "  + newline, highlight);
				doc.insertString(doc.getLength(), " if Task Description has number (e.g: CS2103 tutorial), task description should add double quotation marks. " +newline, null);
				doc.insertString(doc.getLength(), "Syntax Examples:"+newline, highlight_1);
				doc.insertString(doc.getLength(), "\"CS2103 tutorial\"  due/due on/due by/by  wed ;"+newline, null);
				doc.insertString(doc.getLength(), "\"CS2103 tutorial\"  on wed ;",null);





			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}

                   break;

              case 1:
            	  output.setText("");
            	  try {

      				doc.insertString(doc.getLength(), "Single Delete: "  + newline, highlight);
      				doc.insertString(doc.getLength(), "delete a task"  +newline, null);
      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "delete + taskId"  +newline, null);
      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "delete 5"+newline+newline, null);

      				doc.insertString(doc.getLength(), "Multiple Delete:" + newline, highlight);
      				doc.insertString(doc.getLength(), "delete several tasks in one time"  +newline, null);
      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "delete + taskId + taskId +..." +newline, null);
      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "delete 5 8 9 23"+newline+newline, null);


      			} catch (BadLocationException e1) {
      				e1.printStackTrace();
      			}

                   break;

              case 2:
            	  output.setText("");
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
            	  output.setText("");
            	  try {

      				doc.insertString(doc.getLength(), "Done: "  + newline, highlight);
      				doc.insertString(doc.getLength(), "mark a undone task as done"  +newline, null);
      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "done + taskId"  +newline, null);
      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "done 1"+newline, null);

      			} catch (BadLocationException e1) {
      				e1.printStackTrace();
      			}

            	  break;

              case 4:
            	  output.setText("");
            	  try {

        				doc.insertString(doc.getLength(), "Undone: " + newline, highlight);
        				doc.insertString(doc.getLength(), "mark a done task as undone"  +newline, null);
        				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
        				doc.insertString(doc.getLength(), "undone + taskId"+newline, null);
        				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
        				doc.insertString(doc.getLength(), "undone 1"+newline, null);

        			} catch (BadLocationException e1) {
        				e1.printStackTrace();
        			}

            	  break;

              case 5:
            	  output.setText("");
            	  try {

      				doc.insertString(doc.getLength(), "Undo: " + newline, highlight);
      				doc.insertString(doc.getLength(), "undo the privious action"  +newline, null);
      				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "undo"+newline, null);
      				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
      				doc.insertString(doc.getLength(), "undo"+newline, null);

      			} catch (BadLocationException e1) {
      				e1.printStackTrace();
      			}

            	  break;

              case 6:
            	  output.setText("");
            	  try {

        				doc.insertString(doc.getLength(), "Redo: " + newline, highlight);
        				doc.insertString(doc.getLength(), "redo the action after executing undo command"  +newline, null);
        				doc.insertString(doc.getLength(), "Syntax:"+newline, highlight_1);
        				doc.insertString(doc.getLength(), "redo"+newline, null);
        				doc.insertString(doc.getLength(), "Examples:"+newline, highlight_1);
        				doc.insertString(doc.getLength(), "redo"+newline, null);

        			} catch (BadLocationException e1) {
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

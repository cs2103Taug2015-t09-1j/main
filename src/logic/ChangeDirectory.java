/**
 *
 */
package logic;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import storage.Storage;

/**
 * @author Dalton
 *
 */
public class ChangeDirectory extends JFrame {
	private static final Storage storage = Storage.getInstance();

	public ChangeDirectory(JFrame frame) {
		JFileChooser dirChooser = new JFileChooser();
		String title = "Select a directory";

		dirChooser.setCurrentDirectory(new java.io.File("."));
		dirChooser.setDialogTitle(title);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setAcceptAllFileFilterUsed(false);
		dirChooser.setVisible(true);

	    //this.setBounds(0, 0, 0, 0);
	    //this.setAlwaysOnTop(true);
	    //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    //this.setLocationRelativeTo(null);
	    //this.setVisible(true);

	    if (dirChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
	    	System.out.println("getCurrentDirectory(): " +  dirChooser.getCurrentDirectory() + storage.getStoreFolder());
	    	System.out.println("getSelectedFile() : " +  dirChooser.getSelectedFile());
	    	System.out.println("getSelectedFile() : " +  dirChooser.getSelectedFile().getName());
	    	storage.setStoreFolder(dirChooser.getSelectedFile().getName());
	    	storage.saveAllTask();
	    } else {
	    	System.out.println("No Selection");
	    }

	    try {
	    	this.dispose();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
}
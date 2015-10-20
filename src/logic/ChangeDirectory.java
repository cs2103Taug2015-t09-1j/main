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

	public ChangeDirectory() {
		JFileChooser dirChooser = new JFileChooser();
		String title = "Select a new directory";

		dirChooser.setCurrentDirectory(new java.io.File("."));
		dirChooser.setDialogTitle(title);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setAcceptAllFileFilterUsed(false);
		dirChooser.setVisible(true);

	    this.setBounds(0, 0, 0, 0);
	    this.setAlwaysOnTop(true);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setVisible(true);

	    if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    	/*System.out.println("getCurrentDirectory(): " +  dirChooser.getCurrentDirectory() + storage.getStoreDir());
	    	System.out.println("getSelectedFile() : " +  dirChooser.getSelectedFile());
	    	System.out.println("getSelectedFile() : " +  dirChooser.getSelectedFile().getName());
	    	System.out.println(dirChooser.getSelectedFile().getPath());*/
	    	storage.setStoreDir(dirChooser.getSelectedFile().getPath());
	    } else {
	    	System.out.println("No Selection");
	    }
	}
}

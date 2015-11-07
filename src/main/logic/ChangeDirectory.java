/**
 *
 */
package main.logic;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import main.storage.LogFileHandler;
import main.storage.Storage;
/**
 * @@author Dalton
 *
 */
public class ChangeDirectory extends JFrame {
	private static final Storage storage = Storage.getInstance();
	private static final Logger logger = Logger.getLogger(ChangeDirectory.class.getName());

	public ChangeDirectory(JFrame frame) {
		LogFileHandler.getInstance().addLogFileHandler(logger);
		JFileChooser dirChooser = new JFileChooser();
		String title = "Select a directory";

		dirChooser.setCurrentDirectory(new File("."));
		dirChooser.setDialogTitle(title);
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.setAcceptAllFileFilterUsed(false);
		dirChooser.setVisible(true);

	    if (dirChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
	    	storage.setStoreDir(dirChooser.getSelectedFile().getPath());
	    }

	    try {
	    	this.dispose();
	    } catch (Exception e) {
	    	logger.log(Level.SEVERE, e.getMessage(), e);
	    }
	}
}

package main.storage;

//@@author Hiep
public class DirectoryHandler {
	
	/**
	 * 
	 * @param dir is directory needs to be fixed
	 * @return UNIX format of directory of "dir"
	 */
	public static String fixDir(String dir) {
		return dir.replace("\\", "/");
	}
	
	/**
	 * get current working directory of the app 
	 * @return
	 */
	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}
}

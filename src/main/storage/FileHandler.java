package main.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//@@author Hiep
public class FileHandler {
	
	public static void createNewFolderIfNotExisit(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				// error occurs 
			}
		}
	}
	
	public static void createNewFileIfNotExisit(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// error occurs 
			}
		}
	}
	
	public static String readFromFile(String fileName) {
		
		StringBuilder result = new StringBuilder();
		BufferedReader  fr = null;
		
		try {
			fr = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = fr.readLine()) != null) 
				result.append(line);
		} catch (Exception e){
			// error occurs
		} finally {
			try {
				fr.close();
			} catch (Exception e) {
				// error occurs 
			}	
		}
		System.out.println(result.toString());
		return result.toString();
	}
	public static void writeToFile(String fileName, String data) {
		FileWriter fw = null;
		 
		try {
			fw = new FileWriter(fileName);
			fw.write(data);
		} catch (IOException e) {
			// error occurs
		} finally {
			try {
				fw.close();
			} catch (IOException e){
				// error occurs
			}
		}
	}
}

package storage;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import models.Event;
import models.FloatingTask;
import models.Task;

public class DataParser {
	
	private static volatile Gson gson = null;
	
	public static Gson getGson() {
        if (gson == null) {
            synchronized (DataParser.class) {
                if (gson == null) {
                	gson = new Gson();
                }
            }
        }
        return gson;
	}
	
	public static String serializeFloatingTask(List<FloatingTask> tasks) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<FloatingTask>>() {}.getType();
		return gson.toJson(tasks, listType);
	}
	
	public static String serializeEvent(List<Event> tasks) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<Event>>() {}.getType();
		return gson.toJson(tasks, listType);
	}
	
	public static List<FloatingTask> deserializeFloatingTask(String data) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<FloatingTask>>() {}.getType();
		if (data.isEmpty()) data = "[]";
		return gson.fromJson(data, listType);
	}
	public static List<Event> deserializeEvent(String data) {
		Gson gson = getGson();
		Type listType = new TypeToken<List<Event>>() {}.getType();
		if (data.isEmpty()) data = "[]";
		return gson.fromJson(data, listType);
	}
}

package storage;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import models.Commands.TASK_TYPE;
import models.DeadlineTask;
import models.Event;
import models.FloatingTask;
import models.Task;

public class DataParser {
	
	private static volatile Gson gson = null;
	
	public static Gson getGson() {
        if (gson == null) {
            synchronized (DataParser.class) {
                if (gson == null) {
                	gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
                }
            }
        }
        return gson;
	}
	
	public static Type getListType(TASK_TYPE type) {
		switch (type) {
			case DEADLINE_TASK: return new TypeToken<List<DeadlineTask>>() {}.getType();
			case EVENT: return new TypeToken<List<Event>>() {}.getType();
			case FLOATING_TASK: return new TypeToken<List<FloatingTask>>() {}.getType();
			default: return null;
		}
	}
	
	public static String serialize(List<Task> tasks, TASK_TYPE type) {
		Gson gson = getGson();
		Type listType = getListType(type);
		if (type == null) {
			return "[]";
		}
		return gson.toJson(tasks, listType);
	}
	
	public static List<Task> deserialize(String data, TASK_TYPE type) {
		Gson gson = getGson();
		Type listType = getListType(type);
		if (data.isEmpty()) data = "[]";
		return gson.fromJson(data, listType);
	}
}

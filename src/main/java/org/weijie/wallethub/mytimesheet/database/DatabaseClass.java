package org.weijie.wallethub.mytimesheet.database;

import java.util.HashMap;
import java.util.Map;

import org.weijie.wallethub.mytimesheet.model.Message;
import org.weijie.wallethub.mytimesheet.model.Profile;

public class DatabaseClass {

	// this class is to simulate as a database with hashmap
	private static Map<Long, Message> messages = new HashMap<>();
	private static Map<String, Profile> profiles = new HashMap<>();
	
	public static Map<Long, Message> getMessages() {
		return messages;
	}
	
	public static Map<String, Profile> getProfiles() {
		return profiles;
	}
}

package org.weijie.wallethub.mytimesheet.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;



import org.weijie.wallethub.mytimesheet.database.DatabaseClass;
import org.weijie.wallethub.mytimesheet.model.Message;

public class MessageService {

	private Map<Long, Message> messages = DatabaseClass.getMessages();

	public MessageService() {
		messages.put(1L, new Message(1, "Hello jack", "Weijie"));
		messages.put(2L, new Message(2, "Hello jane", "Justin"));
	}

	public List<Message> getAllMessages(){
		return new ArrayList<Message>(messages.values());
	}

	public List<Message> getMessagesForYear(int year) {
		List<Message> messageforyear = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		for (Message m:messages.values()) {
			c.setTime(m.getCreated());
			if (c.get(Calendar.YEAR) == year) {
				messageforyear.add(m);
			}
		}
		return messageforyear;
	}

	public List<Message> getMessagePaginated(int start, int size) {
		List<Message> messagelist = new ArrayList<>(messages.values());
		if (start+size > messagelist.size()) return new ArrayList<Message>();
		return messagelist.subList(start, start+size);
	}

	public Message getMessage(long id) {
		return messages.get(id);
	}

	public Message addMessage(Message message) {
		message.setId(messages.size() + 1);
		messages.put(message.getId(), message);
		return message;
	}

	public Message updateMessage(Message message) {
		if (message.getId() <= 0) {
			return null;
		}
		messages.put(message.getId(), message);
		return message;
	}

	public Message removeMessage(long id) {
		return messages.remove(id);
	}
}

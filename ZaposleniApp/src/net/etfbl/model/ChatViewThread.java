package net.etfbl.model;

public class ChatViewThread extends Thread {
	private volatile boolean newChat;

	public ChatViewThread() {
		super();
		setNewChat(false);
	}

	public ChatViewThread(String arg0) {
		super(arg0);
	}

	public boolean isNewChat() {
		return newChat;
	}

	public void setNewChat(boolean newChat) {
		this.newChat = newChat;
	}
	
}

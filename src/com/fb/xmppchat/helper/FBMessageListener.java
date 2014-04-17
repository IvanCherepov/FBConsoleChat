package com.fb.xmppchat.helper;

import com.fb.xmppchat.app.FBConsoleChatApp;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
 
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
 
public class FBMessageListener implements MessageListener, Runnable {
 
    private FBMessageListener fbml = this;
    private XMPPConnection conn;
    private BidiMap friends;
    private FBConsoleChatApp sender;
 	
 	public static volatile String FirstMessage;
 	private Boolean FirstMessageFlag;
 	
    public FBMessageListener(XMPPConnection conn, FBConsoleChatApp snd) {
	this.conn = conn;
	this.sender = snd;
	new Thread(this).start();
	FirstMessageFlag = true;
	FirstMessage = null;
    }
 
    public void setFriends(BidiMap friends) {
	this.friends = friends;
    }
 
    public void processMessage(Chat chat, Message message) {
	System.out.println();
	MapIterator it = friends.mapIterator();
	String key = null;
	RosterEntry entry = null;
	
	while (it.hasNext()) {
	    key = (String) it.next();
	    entry = (RosterEntry) it.getValue();
	    if (entry.getUser().equalsIgnoreCase(chat.getParticipant())) {
		break;
	    }
	}
	
	if ((message != null) && (message.getBody() != null) && (FirstMessageFlag)) {
		FirstMessage = message.getBody();
	    FirstMessageFlag = false;
	    System.out.println("Debug 1");
	    
	    try {
			sender.sendMessage("Replying back", key);
	    }
	    catch(Exception ex) {
	    	System.out.println(ex.toString());
	    }
	}
	
	if ((message != null) && (message.getBody() != null)) {
	    System.out.println("You've got new message from " + entry.getName() 
			       + "(" + key + ") :");
	    System.out.println(message.getBody());
	    System.out.print("Your choice [1-4]: ");
	}
    }
    
    public byte[] retrieveFirstMessage()
    {
        return this.FirstMessage.getBytes();
    }
 
    public void run() {
	conn.getChatManager().addChatListener(
					      new ChatManagerListener() {
						  public void chatCreated(Chat chat, boolean createdLocally) {
						      if (!createdLocally) {
							  chat.addMessageListener(fbml);
						      }
						  }
					      }
					      );
    }
}
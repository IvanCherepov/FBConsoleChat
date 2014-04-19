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
 
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


public class FBMessageListener implements MessageListener, Runnable {
 
    private FBMessageListener fbml = this;
    private XMPPConnection conn;
    private BidiMap friends;
    private FBConsoleChatApp sender;
 	
 	public String FirstMessage;
 	public Boolean FirstMessageFlag;
 	public Boolean DHKeySentFlag;
 	
    public FBMessageListener(XMPPConnection conn, FBConsoleChatApp snd) {
	this.conn = conn;
	this.sender = snd;
	new Thread(this).start();
	FirstMessageFlag = true;
	DHKeySentFlag = false;
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
	
		if ((message != null) && (message.getBody() != null)) {
			
			if ((!FirstMessageFlag) && (!DHKeySentFlag))
			{
				System.out.println("You've got new message from " + entry.getName() 
						   + "(" + key + ") :");
				System.out.println(message.getBody());
				System.out.print("Your choice [1-4]: ");
			
			}
			
			if (DHKeySentFlag) {
			//System.out.println("TESt");
				FirstMessage = message.getBody();
				FirstMessageFlag = false;
				System.out.println("You've got Public Key from " + entry.getName() 
				+ "(" + key + ") :");
			}
			
			if ((FirstMessageFlag) && (!DHKeySentFlag)){
			//FirstMessage = stringToPublicKey(message.getBody());
			FirstMessage = message.getBody();
			FirstMessageFlag = false;
			
		
			try {
				//String val = new String(sender.aShared, "UTF-8");
				//sender.sendMessage(val, key);
					String val = Base64Coder.toString(sender.pk);
					sender.sendECDHkey(val, key);
				}
				catch(Exception ex) {
					System.out.println(ex.toString());
				}
			}
			
			
		}
		
    }
    
    public String retrieveFirstMessage() throws Exception
    {
        return FirstMessage;
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
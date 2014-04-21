package com.fb.xmppchat.helper;

import com.fb.xmppchat.app.FBConsoleChatApp;

import com.fb.xmppchat.helper.Base64Coder;
import com.fb.xmppchat.helper.BasicDHExample;

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
import java.security.*;

public class FBMessageListener implements MessageListener, Runnable {
 
    private FBMessageListener fbml = this;
    private XMPPConnection conn;
    private BidiMap friends;
    private FBConsoleChatApp sender;
    private BasicDHExample dh;
 	
 	public String FirstMessage;
 	public String SecondMessage;
 	public Boolean FirstMessageFlag;
 	public Boolean ThreePartyFlag;
 	public Boolean SenderFlag;
 	
    public FBMessageListener(XMPPConnection conn, FBConsoleChatApp snd, BasicDHExample dh) {
	this.conn = conn;
	this.sender = snd;
	this.dh = dh;
	new Thread(this).start();
	FirstMessageFlag = true;
	//ThreePartyFlag = false;
	SenderFlag = false;
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
		try {
				
				if ((message != null) && (message.getBody() != null)) {
	
					FirstMessage = message.getBody();
					System.out.println("You've got Message from " + entry.getName() + "(" + key + ") :" + Base64Coder.fromString(message.getBody()));
	
				if (!sender.iAmSender3P) {
				
					if (FirstMessageFlag) {
	
					FirstMessageFlag = !FirstMessageFlag;
	
					String val = Base64Coder.toString(sender.pk);
					sender.sendECDHkey(val, key);
					
					Thread.sleep(3000);
					
					sender.pkiPeerB = (PublicKey) Base64Coder.fromString(FirstMessage);
					sender.sendIntKeyes3P(key);
					}
					
					else {
					sender.pkiPeerCB = (PublicKey) Base64Coder.fromString(message.getBody());
					
					}
				}
				
				
				if ((sender.iAmSender3P) && (SenderFlag)) {
					System.out.println("DEBUG 1");	
					SecondMessage = message.getBody();
				}
				
				if (sender.iAmSender3P) {
					System.out.println("DEBUG 2");
					SenderFlag = !SenderFlag;
				}
	
				}
				}
				catch(Exception ex) {
						System.out.println(ex.toString());
					} 
		}
		
		//else {}
			
		
		
			
			/*
			try {
			  if (sender.pkiPeerB == null) {
					sender.pkiPeerB = (PublicKey) Base64Coder.fromString(message.getBody());
					System.out.println("SAY HI pkiPeerB from Listener" + sender.pkiPeerB);
				}
			 else if (sender.pkiPeerBA == null) {
			 sender.pkiPeerC = (PublicKey) Base64Coder.fromString(message.getBody());
		System.out.println("SAY HI pkiPeer BA from Listener" +  sender.pkiPeerBA);
				}
			else if (sender.pkiPeerCB == null) {
			 sender.pkiPeerCB = (PublicKey) Base64Coder.fromString(message.getBody());
		System.out.println("SAY HI KEY INTERMEDIATE KEY CBC from Listener " + sender.pkiPeerCB);
				}
				}
 	     catch (Exception e) {}

 	     
			if (DHKeySentFlag3P) {
				FirstMessageFlag = !FirstMessageFlag;
				System.out.println("You've got Public Key from " + entry.getName() 
					+ "(" + key + ") :");
				FirstMessage = message.getBody();
				}
			
			else if (FirstMessageFlag){
			    
			    FirstMessageFlag = !FirstMessageFlag;
				System.out.println("You've got Public Key from " + entry.getName() 
					+ "(" + key + ") :");
				FirstMessage = message.getBody();
			
				try {
					//String val = new String(sender.aShared, "UTF-8");
					//sender.sendMessage(val, key);
						String val = Base64Coder.toString(sender.pk);
						sender.sendECDHkey(val, key);
						
										
										}
						else { SecondMessage = message.getBody(); }				
					} 
					catch(Exception ex) {
						System.out.println(ex.toString());
					} 
				}
			
			/*else if (sender.pkiPeerCB == null) {
					try {
			 		sender.pkiPeerCB = (PublicKey) Base64Coder.fromString(message.getBody());
					System.out.println("SAY HI KEY INTERMEDIATE KEY CB from Listener " + sender.pkiPeerCB);
										}
					catch(Exception ex) {
						System.out.println(ex.toString());
					}
				}*/
				
				
			
			/*if ((!FirstMessageFlag) && (!DHKeySentFlag))
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
				DHKeySentFlag = !DHKeySentFlag;
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
			}*/
					
    
    public String retrieveFirstMessage() throws Exception
    {
        return FirstMessage;
    }
    
    public String retrieveSecondMessage() throws Exception
    {
        return SecondMessage;
    }
    
	public void sendIntKeyes3P(String key) {
	try {
	if (!sender.iAmSender3P) {System.out.println("BLYAAAAAAAD");}

	if ((!sender.iAmSender3P) && (sender.pkiPeerB != null)){
		if (sender.pkiPeerBA == null) {sender.pkiPeerBA = (PublicKey) dh.getIntermKey(sender.pkiPeerB);}
		
		System.out.println("Sending Interm Key ba to a ");
		
		sender.sendECDHkey( key, Base64Coder.toString(dh.getIntermKey(sender.pkiPeerB)));
		}
		
	else if (sender.pkiPeerCB == null) {
		sender.pkiPeerCB = (PublicKey) dh.getIntermKey(sender.pkiPeerB);
		System.out.println("Sending Interm Key cb to a ");
		sender.sendECDHkey( key, Base64Coder.toString(dh.getIntermKey(sender.pkiPeerB)));
	
		}
		}
	 catch (Exception e) {}
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
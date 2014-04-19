package com.fb.xmppchat.app;
 
import com.fb.xmppchat.helper.CustomSASLDigestMD5Mechanism;
import com.fb.xmppchat.helper.FBMessageListener;
import com.fb.xmppchat.helper.BasicDHExample;
import com.fb.xmppchat.helper.Utils;
import com.fb.xmppchat.helper.TKey;
import com.fb.xmppchat.helper.Base64Coder;

import java.io.*;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
//import org.apache.commons.io.IOUtils;
 
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.math.BigInteger;
import java.util.*;
import java.io.*;
import java.security.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PublicKey;


import javax.crypto.*;

public class FBConsoleChatApp {
 
   public static final String FB_XMPP_HOST = "chat.facebook.com";
   public static final int FB_XMPP_PORT = 5222;
 
   private ConnectionConfiguration config;
   private XMPPConnection connection;
   private BidiMap friends = new DualHashBidiMap();
   private FBMessageListener fbml;
   private BasicDHExample dh;
   
   private String friendKey = null;
   
   private BigInteger secret;
   private BigInteger A;
   private BigInteger B;
   private Random rand;
   
    public static final BigInteger P = new BigInteger("150396459018121493735075635131373646237977288026821404984994763465102686660455819886399917636523660049699350363718764404398447335124832094110532711100861016024507364395416614225232899925070791132646368926029404477787316146244920422524801906553483223845626883475962886535263377830946785219701760352800897738687");
    public static final BigInteger G = new BigInteger("105003596169089394773278740673883282922302458450353634151991199816363405534040161825176553806702944696699090103171939463118920452576175890312021100994471453870037718208222180811650804379510819329594775775023182511986555583053247825364627124790486621568154018452705388790732042842238310957220975500918398046266");
    public static final int LENGTH = 1023;
    
    public static byte[] aShared = null;
    public static String aSharedS = "No";
    public static PublicKey pk;
 
   public String connect() throws XMPPException {
      config = new ConnectionConfiguration(FB_XMPP_HOST, FB_XMPP_PORT);
      SASLAuthentication.registerSASLMechanism("DIGEST-MD5"
        , CustomSASLDigestMD5Mechanism.class);
      config.setSASLAuthenticationEnabled(true);
      config.setDebuggerEnabled(false);
      connection = new XMPPConnection(config);
      connection.connect();
      fbml = new FBMessageListener(connection, this);
      dh = new BasicDHExample();
      return connection.getConnectionID();
   }
 
   public void disconnect() {
      if ((connection != null) && (connection.isConnected())) {
         Presence presence = new Presence(Presence.Type.unavailable);
         presence.setStatus("offline");
         connection.disconnect(presence);
      }
   }
 
   public boolean login(String userName, String password) 
     throws XMPPException {
      if ((connection != null) && (connection.isConnected())) {
         connection.login(userName, password);
         return true;
      }
      return false;
   }
 
   public String readInput() throws IOException {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      return br.readLine();
   }
 
   public void showMenu() {
      System.out.println("Please select one of the following menu.");
	  System.out.println("0. Initial Setup");
      System.out.println("1. List of Friends online");
      System.out.println("2. Send Message");
      System.out.println("3. Initiate key exchange");
      System.out.println("4. EXIT");
      System.out.print("Your choice [1-4]: ");
   }
   
   public void initialSetup() {
      dh = new BasicDHExample();
	  pk = dh.dhInit();
   }
   
   
   public void getFriends() {
      if ((connection != null) && (connection.isConnected())) {
         Roster roster = connection.getRoster();
         int i = 1;
         for (RosterEntry entry : roster.getEntries()) {
            Presence presence = roster.getPresence(entry.getUser());
            if ((presence != null) 
               && (presence.getType() != Presence.Type.unavailable)) {
               friends.put("#" + i, entry);
               System.out.println(entry.getName() + "(#" + i + ")");
               i++;
            }
         }
         fbml.setFriends(friends);
 	     
 	     try {
 	     
 	     PublicKey pkiPeer = (PublicKey) Base64Coder.fromString(fbml.retrieveFirstMessage());
 	     System.out.println("PEER KEY" + pkiPeer.toString());
 	     String pkiShared = dh.getPeerKey(pkiPeer);
 	     System.out.println("SHARED KEY" + pkiShared);
 	     System.out.println("MY public KEY" + Base64Coder.toString(pk));
 	     
 	     //TEST
 	     
 	     if (Base64Coder.fromString(Base64Coder.toString(pk)) != null)
 	     {
 	     System.out.println("TEST");
 	     System.out.println(Base64Coder.fromString(Base64Coder.toString(pk)));
 	     }
 	     else
 	     {
 	     System.out.println("BLYAD'");
 	     }

 	     //System.out.println(dh.stringToPublicKey(fbml.retrieveFirstMessage()));
 	     //dh.getPeerKey(fbml.retrieveFirstMessage());
 	     }
 	     catch (Exception e) {}
      }
   }
 
   public void sendMessage() throws XMPPException
     , IOException {
     
     System.out.println("Type the key number of your friend (e.g. #1) and the text that you wish to send !");
      String text = null;
      System.out.print("Your friend's Key Number: ");
      friendKey = readInput();
      System.out.print("Your Text message: ");
      text = readInput();
      sendMessage((RosterEntry) friends.get(friendKey), text);
   }
   
   public void sendMessage(String text, String key) throws XMPPException {
   	  sendMessage((RosterEntry) friends.get(key), text);
   }
   
   public void sendECDHkey(String text, String key) throws XMPPException {
   	  sendECDHkey((RosterEntry) friends.get(key), text);
   }
 
   public void sendMessage(final RosterEntry friend, String text) 
     throws XMPPException {
      if ((connection != null) && (connection.isConnected())) {
         ChatManager chatManager = connection.getChatManager();
         Chat chat = chatManager.createChat(friend.getUser(), fbml);
         chat.sendMessage(text);
         System.out.println("Your message has been sent to "
            + friend.getName());
      }
   }
      
    public void sendECDHkey() throws XMPPException
     , IOException {
     
	  System.out.println("My PublicKey BEFORE sending" + pk);
	  
      System.out.println("Type the key number of your friend (e.g. #1)");
      String friendKey = null;
      String text = null;
      System.out.print("Your friend's Key Number: ");
      friendKey = readInput();
      
      System.out.print("You sent g^a mod p: " + Base64Coder.toString(pk) );
      
      sendECDHkey((RosterEntry) friends.get(friendKey), Base64Coder.toString(pk) );
      
      fbml.DHKeySentFlag = true;
    }
    
    /*private String sterializePKey() {
    
		TKey key = new TKey();
		key.key = pk;

		try
		{
			//FileOutputStream fileOut = new FileOutputStream("temp.txt");
			ObjectOutputStream out = new ObjectOutputStream();
			out.writeObject(key);
			out.close();
			//fileOut.close();
			System.out.printf("Serialized data is saved in /tmp/employee.ser");
		}
		catch(IOException i)
		{
			i.printStackTrace();
		}
		
		/*try (BufferedReader br = new BufferedReader(new FileReader("temp.txt"))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			String everything = sb.toString();
			return everything;
			}
		catch (IOException e) {e.printStackTrace();}*
		return null;
    }*/
    
	/*private PublicKey deSterializePKey(String kPeer) {
		try
		{	
			PrintWriter fileInput = new PrintWriter("temp.txt");
			fileInput.println(kPeer);
			FileInputStream fileIn = new FileInputStream("temp.txt");
			
			ObjectInputStream in = new ObjectInputStream(fileIn);
			TKey key = new TKey();
			key = in.readObject();
			in.close();
			fileIn.close();
		}
		catch(IOException i)
		{
			i.printStackTrace();
		}
    }*/
      
    public void sendECDHkey(final RosterEntry friend, String text) 
     throws XMPPException {
      if ((connection != null) && (connection.isConnected())) {
         ChatManager chatManager = connection.getChatManager();
         Chat chat = chatManager.createChat(friend.getUser(), fbml);
         chat.sendMessage(text);
         System.out.println("Your g^a mod p has been sent to "
            + friend.getName());
      }
   }
   
   public static void main(String[] args) {
      if (args.length == 0) {
        System.err.println("Usage: java FBConsoleChatApp [username_facebook] [password]");
        System.exit(-1);
      }
 
      String username = args[0];
      String password = args[1];
 
      FBConsoleChatApp app = new FBConsoleChatApp();
      
      try {
         app.connect();
         if (!app.login(username, password)) {
            System.err.println("Access Denied...");
            System.exit(-2);
         }
         
         app.showMenu();
         String data = null;
         menu:
         while((data = app.readInput().trim()) != null) {
            if (!Character.isDigit(data.charAt(0))) {
               System.out.println("Invalid input.Only 0-4 is allowed !");
               app.showMenu();
               continue;
            }
            int choice = Integer.parseInt(data);
            if ((choice != 0) && (choice != 1) && (choice != 2) && (choice != 3) && (choice != 4)) {
               System.out.println("Invalid input.Only 0-4 is allowed !");
               app.showMenu();
               continue;
            }

            switch (choice) {
			   case 0: app.initialSetup();
                       app.showMenu();
                       continue menu;
               case 1: app.getFriends();
                       app.showMenu();
                       continue menu;
               case 2: app.sendMessage();
                       app.showMenu();
                       continue menu;
			   case 3: app.sendECDHkey();
                       app.showMenu();     
                       continue menu;    
               default: break menu;
            }
         }
         app.disconnect();
      } catch (XMPPException e) {
        if (e.getXMPPError() != null) {
           System.err.println("ERROR-CODE : " + e.getXMPPError().getCode());
           System.err.println("ERROR-CONDITION : " + e.getXMPPError().getCondition());
           System.err.println("ERROR-MESSAGE : " + e.getXMPPError().getMessage());
           System.err.println("ERROR-TYPE : " + e.getXMPPError().getType());
        }
        app.disconnect();
      } catch (IOException e) {
        System.err.println(e.getMessage());
        app.disconnect();
      }
  }
}
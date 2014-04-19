package com.fb.xmppchat.helper;

import java.security.*;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Two party key agreement using Diffie-Hellman
 */
public class BasicDHExample
{
    private static BigInteger g512 = new BigInteger(
            "153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7"
          + "749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b"
          + "410b7a0f12ca1cb9a428cc", 16);
    private static BigInteger p512 = new BigInteger(
            "9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd387"
          + "44d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94b"
          + "f0573bf047a3aca98cdf3b", 16);
          
    //private byte[] aShared;
    private KeyAgreement aKeyAgree;
    private KeyPair      aPair;
    private KeyPairGenerator keyGen;

    public PublicKey dhInit() 
    {
    	try{
        DHParameterSpec	dhParams = new DHParameterSpec(p512, g512);
        keyGen = KeyPairGenerator.getInstance("DH");
        SecureRandom random = new SecureRandom();
        keyGen.initialize(dhParams);

        // set up
        aKeyAgree = KeyAgreement.getInstance("DH");
        aPair = keyGen.generateKeyPair();
        
        // two party agreement
        PublicKey pk = aPair.getPublic();
        //PrivateKey pkP = aPair.getPrivate();
        
        aKeyAgree.init(aPair.getPrivate());
        
        BigInteger x = ((javax.crypto.interfaces.DHPrivateKey) aPair.getPrivate()).getX();
 		BigInteger y = ((javax.crypto.interfaces.DHPublicKey) aPair.getPublic()).getY();
 
        return pk;        
        } 
        
        catch(Exception e) {
        return null;
        }
    }
    
    public String getPeerKey(PublicKey pkPeer) throws Exception {
        
        aKeyAgree.doPhase(pkPeer, true);
        
        //  generate the key bytes
        MessageDigest	hash = MessageDigest.getInstance("SHA1");
        byte[] aShared = hash.digest(aKeyAgree.generateSecret());
        
        //System.out.println(Utils.toHex(aShared));
        //byte[] aShared = aKeyAgree.generateSecret();
        return Utils.toHex(aShared);
    }
    
	/*public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
    	KeyFactory fact = KeyFactory.getInstance("DH");
    	X509EncodedKeySpec spec = fact.getKeySpec(publ,
        X509EncodedKeySpec.class);
		return Base64.encodeBase64(spec.getEncoded());	
		}
	   
	public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
    	byte[] data = base64Decode(stored);
    	X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
    	KeyFactory fact = KeyFactory.getInstance("DH");
    	return fact.generatePublic(spec);
	}

    public static PublicKey stringToPublicKey(String s) {

    	BASE64Decoder decoder = new BASE64Decoder();

    	byte[] c = null;
    	KeyFactory keyFact = null;
    	PublicKey returnKey = null;

    	try {
        	c = decoder.decodeBuffer(s);
        	keyFact = KeyFactory.getInstance("DH");
    	} catch (Exception e) {
        	System.out.println("Error in Keygen");
        	e.printStackTrace();
    	}	

   		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(c);
    	try {
        	returnKey = keyGen.generatePublic(x509KeySpec);
    	} catch (Exception e) {

        	System.out.println("Error in Keygen2");
        	e.printStackTrace();
    	}
    	return returnKey; 
	}*/
}
package com.fb.xmppchat.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility class for chapter 4 examples
 */
public class Utils
{
	private static String	digits = "0123456789abcdef";   
  
     /**
     * Return length many bytes of the passed in byte array as a hex string.
     * 
     * @param data the bytes to be converted.
     * @param length the number of bytes in the data block to be converted.
     * @return a hex representation of length bytes of data.
     */
    public static String toHex(byte[] data, int length)
    {
        StringBuffer	buf = new StringBuffer();
        
        for (int i = 0; i != length; i++)
        {
            int	v = data[i] & 0xff;
            
            buf.append(digits.charAt(v >> 4));
            buf.append(digits.charAt(v & 0xf));
        }
        
        return buf.toString();
    }
    
    private static class FixedRand extends SecureRandom
    {
        MessageDigest	sha;
        byte[]			state;
        
        FixedRand()
        {
            try
            {
                this.sha = MessageDigest.getInstance("SHA-1");
                this.state = sha.digest();
            }
            catch (NoSuchAlgorithmException e)
            {
                throw new RuntimeException("can't find SHA-1!");
            }
        }
	
	    public void nextBytes(
	       byte[] bytes)
	    {
	        int	off = 0;
	        
	        sha.update(state);
	        
	        while (off < bytes.length)
	        {	            
	            state = sha.digest();
	            
	            if (bytes.length - off > state.length)
	            {
	                System.arraycopy(state, 0, bytes, off, state.length);
	            }
	            else
	            {
	                System.arraycopy(state, 0, bytes, off, bytes.length - off);
	            }
	            
	            off += state.length;
	            
	            sha.update(state);
	        }
	    }
    }
    
    /**
     * Return a SecureRandom which produces the same value.
     * <b>This is for testing only!</b>
     * @return a fixed random
     */
    public static SecureRandom createFixedRandom()
    {
        return new FixedRand();
    }
}

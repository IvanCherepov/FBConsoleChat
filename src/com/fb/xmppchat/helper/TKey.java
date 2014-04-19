package com.fb.xmppchat.helper;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class TKey implements java.io.Serializable
{
	private final static long serialVersionUID = 1; // See Nick's comment below
	
	public transient PublicKey key;
}
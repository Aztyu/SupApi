package com.supinfo.supapi.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Random;

public class BaseUtil {
	private static String random_str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String generateRandomString(int size){
		String random = new String();
		Random rand = new Random();
		
		for(int i=0; i<size; i++){
			int random_nbr = rand.nextInt(random_str.length());
			random += (rand.nextBoolean())?random_str.charAt(random_nbr):Character.toLowerCase(random.charAt(random_nbr));
		}
		
		return random;
	}
	
	public static String byteToHex(byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash){
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	public static String getSaltedPassword(String salt, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		String to_hash = String.valueOf(salt) + password;
		MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(to_hash.getBytes("UTF-8"));
        return BaseUtil.byteToHex(crypt.digest());
	}
	
	public static String generateSalt() throws NoSuchAlgorithmException{
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		return String.valueOf(secureRandom.nextInt());
	}

	public static String generatePassword() {
		String password = "";
		for(int i=0; i<30 ;i++){
			Random rand = new Random();
			password += random_str.charAt(rand.nextInt(random_str.length()));
		}
		return password;
	}
}

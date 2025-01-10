/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips, David Wang 
 */

package edu.harvard.i2b2.ncbo.util;

import java.util.HashMap;
import java.util.Map;

import java.security.*;

import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Hex;



public class SymbolHash {

	/**
	 * Target character map.
	 */
	private Map<Integer, Character> targetCharMap = new HashMap<Integer, Character>();

	/**
	 * Constructor for populating source and target character maps.
	 */
	public SymbolHash() {
		constructTargetCharMap();
	}

	private static SymbolHash thisInstance;
    
    static {
            thisInstance = new SymbolHash();
    }
    
    public static SymbolHash getInstance() {
        return thisInstance;
    }	
	
	
	/**
	 * Main method for stand-alone execution of concept path translator.
	 * 
	 * @param args
	 *            Command line argument - the concept path
	 */
	public static void main(String[] args) {
			SymbolHash sh = new SymbolHash();
			
			try {
//				String output = sh.createFourCharTerm("(S65.192) Other specified injury of radial artery at wrist and hand level of left arm");
				
				String out = sh.createFourCharTerm("(N0000183553)[AM400] QUINOLONES");
				String out2 = sh.createFourCharTerm("{N0000029368)[AP109] ANTIPROTOZOALS,OTHER");
			//	String output2 = sh.createFourCharTerm("(S65.199) Other specified injury of radial artery at wrist and hand level of unspecified arm");
				
				System.out.println(out);
				System.out.println(out2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Hash algorithm not found");
				e.printStackTrace();
			}
	}

	
	/* tdw9: updated version of Lori's code.
	 * 	- It uses all chunks, each with 8 characters (instead of 7) from the MD5 Hash
	 *  - It uses long instead of int to handle cases where an 8-char chunk translates
	 *    to an integer greater than the max value of a signed integer, which creates
	 *    an overflow error and stops the program.
	 */
	public String createFourCharTerm_tdw9(String input) throws Exception{
	
		MessageDigest md;
		String md5result = null;
		try {
			md = MessageDigest.getInstance("MD5");
		    md.update(input.getBytes(Charset.forName("UTF8")));
		    byte[] resultByte = md.digest();
		    md5result =  new String(Hex.encodeHex(resultByte));
		    
		    //System.err.println( "["+ input + "] -> "+ md5result );
	
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			throw e1;
		}
		
		while(md5result.length() < 32){
			md5result = md5result + "0";
		}
		
		/* LORI'S original code only uses the first 7 chars in each chunk of the md5 hash because 8 chars may create a number greater than signed 32-bit integer */
		/*int int1 = Integer.parseInt(md5result.substring(0, 7), 16);
		int int2 = Integer.parseInt(md5result.substring(8, 15), 16);
		int int3 = Integer.parseInt(md5result.substring(16, 23), 16);
		int int4 = Integer.parseInt(md5result.substring(24, 31), 16);	*/
		
		
		/* In order to use all 8 chars, we us long instead of int. And make casts to int when necessary. */
		long int1 = Long.parseLong(md5result.substring(0, 8), 16);
		long int2 = Long.parseLong(md5result.substring(8, 16), 16);
		long int3 = Long.parseLong(md5result.substring(16, 24), 16);
		long int4 = Long.parseLong(md5result.substring(24), 16);
		
		/* test output */
		/*
		System.err.println( md5result.substring(0, 8) + "\t" + int1 + "\t" + int1%36 + "\t" + Character.toString(targetCharMap.get((int)(int1%36))) );
		System.err.println( md5result.substring(8, 16) + "\t" + int2 + "\t" + int2%36 + "\t" + Character.toString(targetCharMap.get((int)(int2%36))) );
		System.err.println( md5result.substring(16, 24) + "\t" + int3 + "\t" + int3%36 + "\t" + Character.toString(targetCharMap.get((int)(int3%36))) );
		System.err.println( md5result.substring(24) + "\t" + int4 + "\t" + int4%36 + "\t" + Character.toString(targetCharMap.get((int)(int4%36))) );
		System.err.println();
		*/
		
		int mod1 = (int)(int1%36);
		int mod2 = (int)(int2%36);
		int mod3 = (int)(int3%36);
		int mod4 = (int)(int4%36);
		
		String char1 = Character.toString(targetCharMap.get(mod1));
		String char2 = Character.toString(targetCharMap.get(mod2));
		String char3 = Character.toString(targetCharMap.get(mod3));
		String char4 = Character.toString(targetCharMap.get(mod4));
		
		return ( char1 + char2 + char3 + char4);
	}


	/* tdw9: Original code from Lori -- this code is only using 7 characters of hash, e.g. [0-7)? This increases risk of collision. 
	 * However, if switching to 8 chars (eg.g [0,7], [8,16], etc.), means that the signed integer represented by a chunk like
	 * md5results[0,7] may result in a number that is greater than the max value of a signed int. This overflow will cause the program
	 * to stop unceremoniously. The correct fix is to use long instead of int as seen in the fixed version above. This original version 
	 * is kept for reference -- in case we need to verify results of 4-char hashes generated by this original function.
	 * */
	public String createFourCharTerm(String input) throws Exception{
		
		MessageDigest md;
		String md5result = null;
		try {
			md = MessageDigest.getInstance("MD5");
		    md.update(input.getBytes(Charset.forName("UTF8")));
		    byte[] resultByte = md.digest();
		    md5result =  new String(Hex.encodeHex(resultByte));	
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			throw e1;
		}
		
		while(md5result.length() < 32){
			md5result = md5result + "0";
		}
				
		/* LORI'S original code only uses the first 7 chars in each chunk of the md5 hash because 8 chars may create a number greater than signed 32-bit integer */
		int int1 = Integer.parseInt(md5result.substring(0, 7), 16);
		int int2 = Integer.parseInt(md5result.substring(8, 15), 16);
		int int3 = Integer.parseInt(md5result.substring(16, 23), 16);
		int int4 = Integer.parseInt(md5result.substring(24, 31), 16);
				
		int mod1 = int1%36;
		int mod2 = int2%36;
		int mod3 = int3%36;
		int mod4 = int4%36;
		
		String char1 = Character.toString(targetCharMap.get(mod1));
		String char2 = Character.toString(targetCharMap.get(mod2));
		String char3 = Character.toString(targetCharMap.get(mod3));
		String char4 = Character.toString(targetCharMap.get(mod4));
		
		return ( char1 + char2 + char3 + char4);
	}	
	
	/**
	 * Constructs target character map.
	 */
	private void constructTargetCharMap() {
		targetCharMap.put(0, 'a');
		targetCharMap.put(1, 'b');
		targetCharMap.put(2, 'c');
		targetCharMap.put(3, 'd');
		targetCharMap.put(4, 'e');
		targetCharMap.put(5, 'f');
		targetCharMap.put(6, 'g');
		targetCharMap.put(7, 'h');
		targetCharMap.put(8, 'i');
		targetCharMap.put(9, 'j');
		targetCharMap.put(10, 'k');
		targetCharMap.put(11, 'l');
		targetCharMap.put(12, 'm');
		targetCharMap.put(13, 'n');
		targetCharMap.put(14, 'o');
		targetCharMap.put(15, 'p');
		targetCharMap.put(16, 'q');
		targetCharMap.put(17, 'r');
		targetCharMap.put(18, 's');
		targetCharMap.put(19, 't');
		targetCharMap.put(20, 'u');
		targetCharMap.put(21, 'v');
		targetCharMap.put(22, 'w');
		targetCharMap.put(23, 'x');
		targetCharMap.put(24, 'y');
		targetCharMap.put(25, 'z');
		targetCharMap.put(26, '1');
		targetCharMap.put(27, '2');
		targetCharMap.put(28, '3');
		targetCharMap.put(29, '4');
		targetCharMap.put(30, '5');
		targetCharMap.put(31, '6');
		targetCharMap.put(32, '7');
		targetCharMap.put(33, '8');
		targetCharMap.put(34, '9');
		targetCharMap.put(35, '0');
	}
}

	 

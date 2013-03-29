/**
 * 
 */
package main;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author tomAndSimon
 *
 */
public class HTMLreadImpl implements HTMLread {

	public boolean readUntil(InputStream stream, char ch1, char ch2) {
		char nextCharacter;
		boolean result = false;
		int byteRead;
		try {
			while ((byteRead = stream.read()) != -1) {
				nextCharacter = Character.toLowerCase((char) byteRead);
				if (nextCharacter == Character.toLowerCase(ch1)) {
					result = true;
					break;
				} 
				if (nextCharacter == Character.toLowerCase(ch2)) {
					break;
				}
			}
		} catch (IOException ex) {
			System.out.println("IO exception reading HTML...");
			ex.printStackTrace();
		}
		return result;
	}


	public char skipSpace(InputStream stream, char ch) {
		char nextCharacter;
		char result = Character.MIN_VALUE;
		int byteRead;
		try {
			while ((byteRead = stream.read()) != -1) {
				nextCharacter = Character.toLowerCase((char) byteRead);
				if(Character.isWhitespace(nextCharacter)) {
					continue;
				} else {
					if (nextCharacter == ch) {
						break;
					}
					result = nextCharacter;
					break;
				}
			}
		} catch (IOException ex) {
			System.out.println("IO exception reading HTML...");
			ex.printStackTrace();
		}
		return result;
	}


	public String readString(InputStream stream, char ch1, char ch2) {
		boolean foundCh1 = false;
		StringBuilder whatIsRead = new StringBuilder();
		char nextCharacter;
		try {
			while ((nextCharacter = (char) stream.read()) != -1) {
				whatIsRead.append(nextCharacter);
				if (nextCharacter == ch1) {
					foundCh1 = true;
					break;
				} 
				if (nextCharacter == ch2) {
					break;
				}
			}
		} catch (IOException ex) {
			System.out.println("IO exception reading HTML...");
			ex.printStackTrace();
		} 	
		if (foundCh1) {
			return whatIsRead.toString();
		}
		return null;
	}
}


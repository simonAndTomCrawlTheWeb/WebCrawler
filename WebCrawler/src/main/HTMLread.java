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
public class HTMLread {

	/**
	 * Consumes characters from the given InputStream and stops when either a
	 * character that is the same as ch1 or ch2 is encountered (ignoring case).
	 * 
	 * If the character encountered is the same as ch1, returns true; otherwise,
	 * false is returned.
	 * 
	 * @param stream the InputStream to be read
	 * @param ch1 the character to search for
	 * @param ch2 the character to terminate on
	 * @return whether or not ch1 was encountered (ignoring case)
	 */
	public static boolean readUntil(InputStream stream, char ch1, char ch2) {
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

	/**
	 * Consumes up to, and including, the first non-whitespace character on the 
	 * given InputStream or up to and including the given character.
	 * 
	 * If the method stopped reading because the given character was encountered,
	 * the smallest possible value of a char is returned.
	 * 
	 * @param stream the InputStream to be read
	 * @param ch the character to terminate on
	 * @return the first non-whitespace character, or the smallest char value if ch
	 * was encountered
	 */
	public static char skipSpace(InputStream stream, char ch) {
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

	/**
	 * Consumes characters on the given InputStream and stops when either a character
	 * that is the same as one of the given characters, ch1 and ch2, is encountered, 
	 * not ignoring case.
	 * 
	 * If the character encountered is the same as ch1 a String is returned that contains,
	 * in order, the characters read. If the terminating character is the same as ch2 the 
	 * String value null is returned. ((Null otherwise???))
	 * 
	 * @param stream the InputStream to be read 
	 * @param ch1 the ending character of the String to be read
	 * @param ch2 the character to terminate on
	 * @return the String of characters read ending in ch1, or null if ch2 is read
	 */
	public static String readString(InputStream stream, char ch1, char ch2) {
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


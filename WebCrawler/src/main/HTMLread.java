
package main;

import java.io.InputStream;

/**
 * A class to contain methods for parsing HTML commands from a stream.
 * 
 * @author tomAndSimon 
 */
public interface HTMLread {
	
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
	boolean readUntil(InputStream stream,char ch1,char ch2);
	
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
	char skipSpace(InputStream stream,char ch);
	
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
	String readString(InputStream stream,char ch1,char ch2);
}

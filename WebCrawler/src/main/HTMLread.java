
package main;

import java.io.InputStream;

/**
 * @author tomAndSimon 
 * 
 *
 */
public interface HTMLread {
	boolean readUntil(InputStream stream,char ch1,char ch2);
	char skipSpace(InputStream stream,char ch);
	String readString(InputStream stream,char ch1,char ch2);
}
// PUSH TEST Y'ALL
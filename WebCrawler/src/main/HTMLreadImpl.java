/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @author tomAndSimon
 *
 */
public class HTMLreadImpl implements HTMLread {

	@Override
	public boolean readUntil(InputStream stream, char ch1, char ch2) {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		char nextCharacter;
		boolean result = false;
		try {
			while ((nextCharacter = (char) in.read()) != -1) {
				// System.out.println(nextCharacter);
				if (nextCharacter == ch1) {
					result = true;
					break;
				} 
				if (nextCharacter == ch2) {
					break;
				}
			}
		} catch (IOException ex) {
			System.out.println("IO exception...");
			ex.printStackTrace();
		} finally {
			closeReader(in);
		}
		return result;
	}

	@Override
	public char skipSpace(InputStream stream, char ch) {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		char nextCharacter;
		char result = Character.MIN_VALUE;
		try {
			while ((nextCharacter = (char) in.read()) != -1) {
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
			System.out.println("IO exception...");
			ex.printStackTrace();
		} finally {
			closeReader(in);
		}
		return result;
	}

	@Override
	public String readString(InputStream stream, char ch1, char ch2) {
		boolean foundCh1 = false;
		StringBuilder whatIsRead = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		char nextCharacter;
		try {
			while ((nextCharacter = (char) in.read()) != -1) {
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
			System.out.println("IO exception...");
		} finally {
			closeReader(in);
		}		
		if (foundCh1) {
			return whatIsRead.toString();
		}
		return null;
	}
	
	private void closeReader(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException ex) {
			System.out.println("IO problem when closing reader...");
		}
	}
}


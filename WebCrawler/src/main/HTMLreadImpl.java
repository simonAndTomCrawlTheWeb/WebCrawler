/**
 * 
 */
package main;

import java.io.InputStream;

/**
 * @author tomAndSimon
 *
 */
public class HTMLreadImpl implements HTMLread {

	/* (non-Javadoc)
	 * @see main.HTMLread#readUntil(java.io.InputStream, char, char)
	 */
	@Override
	public boolean readUntil(InputStream stream, char ch1, char ch2) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see main.HTMLread#skipSpace(java.io.InputStream, char)
	 */
	@Override
	public char skipSpace(InputStream stream, char ch) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see main.HTMLread#readString(java.io.InputStream, char, char)
	 */
	@Override
	public String readString(InputStream stream, char ch1, char ch2) {
		// TODO Auto-generated method stub
		return null;
	}

}

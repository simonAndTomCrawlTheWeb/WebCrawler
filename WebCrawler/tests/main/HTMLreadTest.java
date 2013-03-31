/**
 * 
 */
package main;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static main.HTMLread.*;
/**
 * @author tomAndSimon
 *
 */
public class HTMLreadTest {

	@Test
	public void readUntilTest() {
		InputStream stream1 = null;
		InputStream stream2 = null;
		try {
			URL mySite1 = new URL("http://www.google.co.uk");
			URL mySite2 = new URL("http://www.guardian.co.uk/");
			stream1 = mySite1.openStream();
			stream2 = mySite2.openStream();
		} catch (MalformedURLException ex){
			System.out.println("Malformed...");			
		} catch (IOException ex) {
			System.out.println("IO problem...");		
		}
		boolean result1 = readUntil(stream1, 's', 'f');
		boolean result2 = readUntil(stream2, 'l', 's');
		assertTrue(result1);
		assertTrue(result2);
	}
	
	@Test
	public void readStringTest() {
		InputStream stream1 = null;
		InputStream stream2 = null;
		try {
			URL mySite1 = new URL("http://www.google.co.uk");
			URL mySite2 = new URL("http://www.telegraph.co.uk/");
			stream1 = mySite1.openStream();
			stream2 = mySite2.openStream();
		} catch (MalformedURLException ex){
			System.out.println("Malformed...");			
		} catch (IOException ex) {
			System.out.println("IO problem...");		
		}
		String result1 = readString(stream1, 'i', 'z');
		String result2 = readString(stream2, 'h', 'm');
		assertEquals("<!doctype html><html i", result1);
		assertEquals("<!DOCTYPE h", result2);
	}
	
	@Test
	public void skipSpaceTest() throws Exception {
		String str = "  x  ";
		InputStream stream = new ByteArrayInputStream(str.getBytes());
		assertEquals('x', skipSpace(stream, 'y'));
	
		stream = new ByteArrayInputStream(str.getBytes());
		assertEquals(Character.MIN_VALUE, skipSpace(stream, 'x'));
	}

	@Test
	public void skipSpaceTest2() throws Exception {
		URL mySite = new URL("http://www.guardian.co.uk/"); // Helpfully provides ample whitespace ahead of first '<'
		InputStream stream = mySite.openStream();
		
		assertEquals('<', skipSpace(stream, 'D'));
		
		stream = mySite.openStream();
		assertEquals(Character.MIN_VALUE, skipSpace(stream, '<'));
		}
}

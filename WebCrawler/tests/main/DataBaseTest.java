/**
 * 
 */
package main;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tomAndSimon
 *
 */
public class DataBaseTest {
	
	private Database db;
	private String url = "this/is/a/pretend/link";
	private String file = "serializeTest.ser";
	
	@Before
	public void setUp() {
		db = new Database();
	}

	@Test
	public final void testAddAndGetLinks() {
		db.addLink(0, url);
		assertTrue(db.getLinksToCrawl().get(0).contains(url));
	}
	
	@Test
	public final void testAddAndGetResults() {
		db.addResult(url);
		assertTrue(db.getResults().contains(url));
	}
	
	@Test
	public final void testSerialization() throws Exception {
		db.addLink(0, url);
		db.addResult(url);
		
		FileOutputStream fileOut = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(db);
		
		FileInputStream fileIn = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		Database dbDeserialized = (Database) in.readObject();
		
		assertTrue(dbDeserialized.getLinksToCrawl().get(0).contains(url));
		assertTrue(dbDeserialized.getResults().contains(url));
		
		fileOut.close();
		out.close();
		fileIn.close();
		in.close();
	}
	
}

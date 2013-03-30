/**
 * 
 */
package main;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tomAndSimon
 *
 */
public class DataBaseTest {
	
	private Database db;
	private String url = "this/is/a/pretend/link";
	
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
	public void testGetLinksOfPriorityWhenEmpty() {
		db.addLink(0, url);
		assertNotNull(db.getLinksOfPriority(0));
		assertNull(db.getLinksOfPriority(5));
	}
	
}

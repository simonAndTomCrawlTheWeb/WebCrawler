/**
 * 
 */
package main;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tomAndSimon
 *
 */
public class WebCrawlerTest {
	
	private WebCrawler crawler;
	private String urlTrue;
	private String urlFalse;

	@Before
	public void setUp() throws Exception {
		crawler = new MockWebCrawler();
		urlTrue = "http://en.wikipedia.org/wiki/Spider";
		urlFalse = "http://en.wikipedia.org/wiki/Arachnophobia";
	}

	@Test
	public final void testSearch() {
		assertTrue(crawler.search(urlTrue));
		assertFalse(crawler.search(urlFalse));
	}
	
	//Just realised it was silly to base a test on a Wikipedia page. Very likely to change its content. Check source if test fails unexpectedly.
	@Test
	public final void testCrawlSimpleEmptyFile() {
	    crawler = new MockWebCrawler(1,20);
	    crawler.crawl("http://en.wikipedia.org/wiki/Spider","WCTest.txt");
	    Database myDb = crawler.getDatabase();
	    Map<Integer, LinkedList<String>> linksToCrawl = myDb.getLinksToCrawl();
	    LinkedList<String> priorityZero = linksToCrawl.get(0);
	    LinkedList<String> priorityOne = linksToCrawl.get(1);
	    assertTrue(priorityZero.size()==1);
	    assertTrue(priorityZero.get(0).equals("http://en.wikipedia.org/wiki/Spider"));
	    assertTrue(priorityOne.size()==19);
	    assertTrue(priorityOne.get(0).equals("http://en.wikipedia.org/wiki/Spider#mw-head"));
	    assertTrue(priorityOne.get(1).equals("http://en.wikipedia.org/wiki/Spider#p-search"));
	    assertTrue(priorityOne.get(2).equals("http://en.wikipedia.org/wiki/Spider_(disambiguation)"));
	    assertTrue(priorityOne.get(5).equals("http://en.wikipedia.org/wiki/Cambrian"));
	    assertFalse(priorityOne.get(7).equals("http://en.wikipedia.org/apple-touch-icon.png"));
	}
	
	@Test
	public final void testCrawlSimpleNoFile() {
	    crawler = new MockWebCrawler(1,20);
	    crawler.crawl("http://en.wikipedia.org/wiki/Spider","blah.txt");
	    Database myDb = crawler.getDatabase();
	    Map<Integer, LinkedList<String>> linksToCrawl = myDb.getLinksToCrawl();
	    LinkedList<String> priorityZero = linksToCrawl.get(0);
	    LinkedList<String> priorityOne = linksToCrawl.get(1);
	    assertTrue(priorityZero.size()==1);
	    assertTrue(priorityZero.get(0).equals("http://en.wikipedia.org/wiki/Spider"));
	    assertTrue(priorityOne.size()==19);
	    assertTrue(priorityOne.get(0).equals("http://en.wikipedia.org/wiki/Spider#mw-head"));
	    assertTrue(priorityOne.get(1).equals("http://en.wikipedia.org/wiki/Spider#p-search"));
	    assertTrue(priorityOne.get(2).equals("http://en.wikipedia.org/wiki/Spider_(disambiguation)"));
	    assertTrue(priorityOne.get(5).equals("http://en.wikipedia.org/wiki/Cambrian"));
	    assertFalse(priorityOne.get(7).equals("http://en.wikipedia.org/apple-touch-icon.png"));
	}	
	
}

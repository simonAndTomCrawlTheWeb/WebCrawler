/**
 * 
 */
package main;

import static org.junit.Assert.*;

import java.util.List;
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
	private String simpleUrl;
	private String testDb;

	@Before
	public void setUp() throws Exception {
		crawler = new MockWebCrawler();
		urlTrue = "http://en.wikipedia.org/wiki/Spider";
		urlFalse = "http://en.wikipedia.org/wiki/Arachnophobia";
		simpleUrl = "http://www.york.ac.uk/teaching/cws/wws/webpage1.html";
		testDb = "WCTest.txt";
	}
	
	@Test
	public final void testSearch() throws Exception {
		assertTrue(crawler.search(urlTrue));
		assertFalse(crawler.search(urlFalse));

	}
	/*
	@Test
	public void testCrawlerSimplePage() {
		crawler.crawl(simpleUrl, testDb);
		Database myDb = crawler.getDatabase();
		List<String> priorityZero = myDb.getLinksOfPriority(0);
		List<String> priorityOne = myDb.getLinksOfPriority(1);
		List<String> priorityTwo = myDb.getLinksOfPriority(2);
		assertEquals(1, priorityZero.size());
		assertEquals(2, priorityOne.size());
		assertEquals(8, priorityTwo.size());
	}
	*/
	@Test
	public void testMaxDepthMaxLinks() {
		crawler = new MockWebCrawler(0,0);
		assertEquals(crawler.getDefaultMaxDepth(), crawler.getDefaultMaxDepth());
		assertEquals(crawler.getDefaultMaxLinks(), crawler.getDefaultMaxLinks());
		
		crawler = new MockWebCrawler(0,1);
		assertEquals(0, crawler.getMaxDepth());
		assertEquals(1, crawler.getMaxLinks());
		
		crawler = new MockWebCrawler(6,700);
		assertEquals(6, crawler.getMaxDepth());
		assertEquals(700, crawler.getMaxLinks());
	}
	
	@Test
	public void testSeedNotDistinct() {
		crawler = new MockWebCrawler(0,1);
		crawler.crawl("http://www.bbc.co.uk", "seedNotUnique.txt");
	}
}

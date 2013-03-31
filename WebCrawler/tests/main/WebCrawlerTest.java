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
public class WebCrawlerTest {
	
	private WebCrawler crawler;
	private String urlTrue;
	private String urlFalse;
	private String simpleUrl;

	@Before
	public void setUp() throws Exception {
		crawler = new MockWebCrawler();
		urlTrue = "http://en.wikipedia.org/wiki/Spider";
		urlFalse = "http://en.wikipedia.org/wiki/Arachnophobia";
		simpleUrl = "http://www.york.ac.uk/teaching/cws/wws/webpage1.html";
	}
	
	@Test
	public final void testSearch() throws Exception {
		assertTrue(crawler.search(urlTrue));
		assertFalse(crawler.search(urlFalse));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSeedNotDistinct() {
		crawler = new MockWebCrawler(0,1);
		crawler.crawl("http://www.bbc.co.uk", "seedNotUnique.txt");
	}
}

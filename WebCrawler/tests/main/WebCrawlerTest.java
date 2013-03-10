/**
 * 
 */
package main;

import static org.junit.Assert.*;

import java.net.URL;

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
	
	@Test
	public final void testCrawl() {
		fail("Not yet implemented"); // TODO
	}

}

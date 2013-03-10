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
	private URL urlTrue;
	private URL urlFalse;
	private String urlStrTrue;
	private String urlStrFalse;

	@Before
	public void setUp() throws Exception {
		crawler = new MockWebCrawler();
		urlStrTrue = "http://en.wikipedia.org/wiki/Spider";
		urlStrFalse = "http://en.wikipedia.org/wiki/Arachnophobia";
		urlTrue = new URL(urlStrTrue);
		urlFalse = new URL(urlStrFalse);
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

package main;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tomAndSimon
 *
 */
public class SimonsWebCrawlerTest {

	private WebCrawler crawler;
	private WebCrawler oldCrawler;
	private String urlTrue;
	private String urlFalse;

	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	@Before
	public void setUp() throws Exception {
		oldCrawler = new MockWebCrawler(1,5);
		crawler = new SimonsMockWebCrawler(1,5);
		urlTrue = "http://en.wikipedia.org/wiki/Spider";
		urlFalse = "http://en.wikipedia.org/wiki/Arachnophobia";
	}
	
	@Test
	public final void testSearch() throws Exception {
		assertTrue(oldCrawler.search(urlTrue));
		assertFalse(oldCrawler.search(urlFalse));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSeedNotDistinct() {
		oldCrawler = new MockWebCrawler(0,1);
		oldCrawler.crawl("http://www.bbc.co.uk/", "seedNotUnique.txt");	
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testJunkDbfile() {
		crawler.crawl(urlTrue, "junkXML.txt");
	}
	
	@Test
	public void testDbfileNotExists() {
		File notHere = new File("notHere.txt");
		if(notHere.exists()) {
			notHere.delete();
		} 
		assertFalse(notHere.exists());
		crawler.crawl(urlTrue, "notHere.txt");
		assertTrue(notHere.exists());
	}
	
	@Test
	public void testDbfileEmpty() throws Exception {
		File empty = new File("empty.txt");
		if(empty.exists()) {
			empty.delete();
		}
		empty.createNewFile();
		assertEquals(0, empty.length());
		crawler.crawl(urlTrue, "empty.txt");
		assertTrue(empty.length() > 0);		
	}
	
	@Test
	public void testLoadPrevDb() throws Exception {
		File proto = new File("premadeDbProto.txt");
		File premade = new File("premadeDb.txt");
		if(premade.exists()) {
			premade.delete();
		}
		premade.createNewFile();
		copyFile(proto, premade);
		
		crawler.crawl("http://www.bbc.co.uk/", "premadeDb.txt");
	}
}
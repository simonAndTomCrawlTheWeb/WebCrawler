/**
 * 
 */
package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import main.HTMLreadImpl;
/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {
	
	//private HTMLread reader = new HTMLreadImpl(); DO WE NEED THIS?
	
	private final static int DEFAULT_MAX_LINKS = 100;
	private final static int DEFAULT_MAX_DEPTH = 10;
	
	private final int maxDepth, maxLinks;
	
	private Map<Integer, LinkedList<String>> linksToCrawl = new HashMap<Integer, LinkedList<String>>();
	private List<String> results = new LinkedList<String>();
	
	public WebCrawler() {
		this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_LINKS);
	}
	
	public WebCrawler(int maxDepth, int maxLinks) {
		this.maxDepth = maxDepth;
		this.maxLinks = maxLinks;
	}

	public final void crawl(String url /*needs 'database' info parameter*/) {
		try {
			processPage(url);
		} catch (MalformedURLException e) {
			System.out.println("Not a well-formed URL...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("There was an I/O problem...");
			e.printStackTrace();
		}
		
		boolean result = search(url);
		if (result) {
			storeResult(url);
		}
	}
	
	private void processPage(String urlStr) throws IOException, MalformedURLException {
		URL url = new URL(urlStr);
		InputStream stream = url.openStream();
		
	}

	abstract boolean search(String url);
}

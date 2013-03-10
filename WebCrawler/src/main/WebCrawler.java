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

import main.HTMLreadImpl;
/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {

	private HTMLread reader = new HTMLreadImpl();
	
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

	public final void crawl(String url) {
		try {
			processPage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		search(url);
	}
	
	private void processPage(String urlStr) throws IOException, MalformedURLException {
		URL url = new URL(urlStr);
		InputStream stream = url.openStream();
		
		//reader.readUntil(stream, '<', );
		
		
		
	}

	abstract boolean search(String url);
}

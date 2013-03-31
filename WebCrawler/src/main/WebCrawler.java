
package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.InitializationException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

import static util.CrawlerUtilities.*;

/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {
	private final static int DEFAULT_MAX_LINKS = 5;
	private final static int DEFAULT_MAX_DEPTH = 2;
	private final int maxDepth, maxLinks;
	private Database data;
	
	public WebCrawler() {
		this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_LINKS);
	}
	
	public WebCrawler(int maxDepth, int maxLinks) {
		if(maxDepth >= 0 && maxLinks > 0) { // maxDepth can be zero (we take the seed url as depth = priority = 0
			this.maxDepth = maxDepth; 		// maxLinks, however must be greater than zero, or crawl will do nothing
			this.maxLinks = maxLinks;
		} else {
			// Set to defaults
			System.out.println("Maximum depth and maximum no. of links to search have minimum values of 0 and 1 respectively (ie. when just the seed URL is searched); setting to default values:");
			System.out.println("\tmax depth = " + DEFAULT_MAX_DEPTH + "\tmax links = " + DEFAULT_MAX_LINKS);
			this.maxDepth = DEFAULT_MAX_DEPTH;
			this.maxLinks = DEFAULT_MAX_LINKS;
		}
	}
	
	abstract boolean search(String url);
	
	/*
	 * Would like to do this recursively; but, started out
	 * by doing it iteratively- refactor later.
	 * 
	 * Currently it gets maxLinks non-unique links; maybe change.
	 */
	public final void crawl(String url, String dbFile) {
		if(url == null || dbFile == null) {
			throw new IllegalArgumentException("Seed URL and database filename cannot be null");
		}
		File file = new File(dbFile); 
		if(file.exists() && file.isFile() && !(file.length() == 0)) { // Check file is not empty
			try {
				loadDatabase(file);
			} catch (XStreamException ex) {
				throw new IllegalArgumentException("Problem reading XML from " + dbFile +", please check it is of the correct format");
			}	
		} else {
			data = new Database();
		}
		
		int currentPriority = 0;
		int linksProcessed = 0;
		if(!data.addLink(currentPriority, url)) {
		// Seed url is in the result list, ie. has been searched previously. As url is not distinct, quit
			throw new IllegalArgumentException("Seed URL " + url + " has been searched previously; try again with distinct seed");
		}
		// *** CRAWL LOOP ***
		while(currentPriority <= maxDepth && data.getLinksOfPriority(currentPriority) != null) {
			List<String> linksAtThisDepth = data.getLinksOfPriority(currentPriority);
			int size = linksAtThisDepth.size();
			int index = 0;
			while(linksProcessed < maxLinks && index < size) {
				url = linksAtThisDepth.get(index);
				// grab links from the current page
				try {
					URL site = new URL(url);
					List<String> foundLinks = getLinksFromPage(site);
					if(foundLinks == null) { // unable to open site at url, move on
						index++;
						continue;
					}
					// add found links to database
					for(String link : foundLinks) {
						data.addLink(currentPriority + 1, link);
					}			
					// call user-defined search within try block to avoid calling on bad URLs
					if(search(url)) {
					// if search successful, add url to database results table
						data.addResult(url);
					}
					linksProcessed++;
					} catch (MalformedURLException e) {
						if(currentPriority == 0) {
						// seed URL was bad
							throw new IllegalArgumentException("Seed URL malformed");
						}
						System.out.println("URL " + url + " malformed");
					}
				index++;				
			}
		currentPriority++;
		}
		// *** LOOP END ***
		// crawling finished, write results to database file
		try {
			writeDatabase(file);
		} catch (XStreamException ex) {
			System.out.println("Could not intialise XStream");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("The file: " + file.getPath() + " either cannot be created or cannot be opened! Please check directory.");		}
	}
	
	/*
	 * This reads the contents of the file supplied and puts it in linksAdded
	 */
	@SuppressWarnings("unchecked")
	private void loadDatabase(File file) throws XStreamException {
		try {
			XStream stream = initialiseXStream();
			data = new Database((List<String>) stream.fromXML(file)); //This needs to be type checked; can do better with XML schema
		} catch (InitializationException ex) {
			throw new XStreamException(ex);
		}
	}
	
	/*
	 * This writes the contents of 'results' from the Database object to the file path supplied to crawl
	 */
	private void writeDatabase(File file) throws XStreamException, IOException {
		try {
			FileOutputStream fileOut = null;
			XStream stream = initialiseXStream();
			fileOut = new FileOutputStream(file);
	     	stream.toXML(data.getResults(), fileOut);
	     	fileOut.close();
		} catch (InitializationException ex) {
			throw new XStreamException(ex);
		}
	}
	
	/**
	 * Creates a new XStream  and sets the appropriate aliases for serialising/deserialising the database to/from file. 
	 * @return stream the initialised XStream
	 */
	private XStream initialiseXStream() throws InitializationException {
		XStream stream = new XStream(new DomDriver());
		stream.alias("links", LinkedList.class);
		stream.alias("url", String.class);
		return stream;
	}
}

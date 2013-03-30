
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {
	private HTMLread reader = new HTMLreadImpl(); 
	private final static int DEFAULT_MAX_LINKS = 5;
	private final static int DEFAULT_MAX_DEPTH = 2;
	private final int maxDepth, maxLinks;
	private Database db;
	
	public WebCrawler() {
		this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_LINKS);
	}
	
	public WebCrawler(int maxDepth, int maxLinks) {
		if(maxDepth > 0 && maxLinks > 0) {
			this.maxDepth = maxDepth; 
			this.maxLinks = maxLinks;
		} else {
			// If input dodgy, set to defaults
			this.maxDepth = DEFAULT_MAX_DEPTH;
			this.maxLinks = DEFAULT_MAX_LINKS;
		}
	}
	
	/*
	 * Would like to do this recursively; but, started out
	 * by doing it iteratively- refactor later.
	 * 
	 * Currently it gets maxLinks non-unique links; maybe change.
	 */
	public final void crawl(String url, String dbFile) {
		File file = new File(dbFile); //What if dbFile is null? Handle NullPointerException? 
		if(file.exists() && file.isFile()) {
			loadDatabase(file);	
		} else {
			db = new Database();
		}
		
		int currentPriority = 0;
		int linksProcessed = 0;
		db.addLink(currentPriority, url);
		
		while(currentPriority <= maxDepth && db.getLinksOfPriority(currentPriority) != null) {
			System.out.println("At depth " + currentPriority);////////DEBUG/////////////////////
			
			
			List<String> linksAtThisDepth = db.getLinksOfPriority(currentPriority);
			int size = linksAtThisDepth.size();
			int index = 0;
			while(linksProcessed <= maxLinks && index < size) {
				url = linksAtThisDepth.get(index);
				// grab links from the current page
				try {
					URL site = new URL(url);
					List<String> foundLinks = getLinksFromPage(site);
					if(foundLinks == null) { // unable to open site at url, move on
						index++;
						continue;
					}
					for(String link : foundLinks) {
						db.addLink(currentPriority + 1, link);
					}
					// call user-defined search within try block to avoid calling on bad URLs
					if(search(url)) {
						db.addResult(url);
					}
					linksProcessed++;
					
					System.out.println(linksProcessed);////////DEBUG/////////////////////
					
					} catch (MalformedURLException e) {
						System.out.println("URL " + url + " malformed");
					}
				index++;
			}
		currentPriority++;
		}
		writeDatabase(file);
	}
	
	/*
	 * Extracts all links from A tags on the given page, of the form <a * href="*" *>,
	 * interprets them using the context of the given page, and returns them in a list.
	 * If the given page cannot be connected to, returns a null list.
	 */
	private int test=0;////////DEBUG/////////////////////
	
	public List<String> getLinksFromPage(URL page) {
		List<String> links = null;
		InputStream stream;
		try {
			if(page.toString().equals("http://trace.ntu.ac.uk/mentors/madigan/colours.htm")) { //////THIS IS DEBUGG!!!!!!
				return links;
			}
			stream = page.openStream();
		} catch (IOException e1) {
			System.out.println("Could not connect to " + page.toString());
			return links;
		}
		
		System.out.println("link no." + (++test)); ////////DEBUG/////////////////////
		
		links = new LinkedList<>();
		boolean endOfTag = false;
		boolean endOfPage = false;
		while(!endOfPage) {
			if(reader.readUntil(stream, '<', '\u001a')) {
				endOfTag = false;
				String tag = reader.readString(stream, ' ', '>');  
				if(tag != null && tag.equalsIgnoreCase("a ")) {
					// found anchor - now look for href...
					while(!endOfTag) {
						if(reader.readUntil(stream, 'h', '>')) {
							String attr = reader.readString(stream, '"', '>');
							if(attr != null && attr.equalsIgnoreCase("ref=\"")) {
								// found the link URL
								String longLink = reader.readString(stream, '"', '>');
								String link = longLink.substring(0, longLink.length() -1); // trim the trailing " at end of longLink
								if(link != null && link.charAt(0) != '#') {                // ignore links to the same page
									try {
										URL linkUrl = new URL(page, link);                 // interpret link in context of current page
										links.add(linkUrl.toString());
									} catch (MalformedURLException e) {
										// Something's wrong with this link - let's ignore it
										System.out.println("Ignoring malformed link " + link);
									}               
								}
								endOfTag = true; // only one link per anchor
							}	
						} else {
							endOfTag = true;
						}
					}		
				}	
			} else {
				endOfPage = true;
			}
		}
		return links;
	}
	
	
	/*
	 * This reads the contents of the file supplied and puts it in linksAdded
	 */
	@SuppressWarnings("unchecked")
	private void loadDatabase(File file) {
		XStream stream = new XStream(new DomDriver());
		stream.alias("links", LinkedList.class);
		stream.alias("url", String.class);
		db = new Database((List<String>) stream.fromXML(file)); //This needs to be type checked; can do better with XML schema
		for (String next: db.getLinksAdded()) {
			System.out.println(next); 
		}
	}
	
	/*
	 * This writes the contents of 'results' from the Database object to the file path supplied to crawl
	 */
	private void writeDatabase(File file) {
		XStream stream = new XStream(new DomDriver());
		stream.alias("links", LinkedList.class);
		stream.alias("url", String.class);
		try {
            FileOutputStream fileOut = new FileOutputStream(file);
            stream.toXML(db.getResults(), fileOut);
        } catch (FileNotFoundException ex) {
        	System.out.println("The file: " + file.getPath() + " was not found!");
            ex.printStackTrace();
        } catch (XStreamException ex) {
        	ex.printStackTrace();
        }
	}

	abstract boolean search(String url);
	
	/*
	 * FOR TESTING ONLY!
	 */
	public Database getDatabase() {
		return db;
	}
}

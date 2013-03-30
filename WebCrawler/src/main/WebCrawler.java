
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

import com.thoughtworks.xstream.InitializationException;
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
		if(maxDepth >= 0 && maxLinks > 0) { // maxDepth can be zero (we take the seed url as depth = priority = 0
			this.maxDepth = maxDepth; 		// maxLinks, however must be greater than zero, or crawl will do nothing
			this.maxLinks = maxLinks;
		} else {
			// Set to defaults
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
		if(url == null || dbFile == null) {
			throw new IllegalArgumentException("Seed URL and database filename cannot be null");
		}
		File file = new File(dbFile); 
		if(file.exists() && file.isFile() && !(file.length() == 0)) { // Check for empty
			try {
				loadDatabase(file);
			} catch (XStreamException ex) {
				System.out.println("Problem reading XML from " + dbFile +", please check it is of the correct format");
				ex.printStackTrace();
			}
				
		} else {
			db = new Database();
		}
		
		int currentPriority = 0;
		int linksProcessed = 0;
		if(!db.addLink(currentPriority, url)) {
			// Seed url is in the result list, ie. has been searched previously. As url is not distinct, quit
			throw new IllegalArgumentException("Seed URL " + url + " has been searched previously; try again with distinct seed");
		}
		
		while(currentPriority <= maxDepth && db.getLinksOfPriority(currentPriority) != null) {
			List<String> linksAtThisDepth = db.getLinksOfPriority(currentPriority);
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
					for(String link : foundLinks) {
						
						
						if(db.addLink(currentPriority + 1, link)) {
							System.out.println("\tadded link:" + link);
						}
						
						
					}
					// call user-defined search within try block to avoid calling on bad URLs
					if(search(url)) {
						db.addResult(url);
					}
					linksProcessed++;
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
	public List<String> getLinksFromPage(URL page) {
		List<String> links = null;
		InputStream stream;
		try {
			stream = page.openStream();
		} catch (IOException e1) {
			System.out.println("Could not connect to " + page.toString());
			return links;
		}		
		
		System.out.println("Scraping: " + page);
		
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
								if(longLink != null) {
									String link = longLink.substring(0, longLink.length() -1); 	// trim the trailing " at end of longLink
									link = link.trim();										   	// link address can be empty/whitespace to refer to current page - we should ignore these
									
								//	System.out.println("\tfound: "+link);
									
									if(!link.isEmpty()) {                
										if(link.charAt(0) != '#') {								// ignore links to the same page
											try {
												URL linkUrl = new URL(page, link);              // interpret link in context of current page
												links.add(linkUrl.toString().toLowerCase());
											} catch (MalformedURLException e) {
												// Something's wrong with this link - let's ignore it
												System.out.println("Ignoring malformed link " + link);
											} 
										}
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
	public void loadDatabase(File file) throws XStreamException {
		try{
			XStream stream = new XStream(new DomDriver());
			stream.alias("links", LinkedList.class);
			stream.alias("url", String.class);
			db = new Database((List<String>) stream.fromXML(file)); //This needs to be type checked; can do better with XML schema
		} catch (InitializationException ex) {
			throw new XStreamException(ex);
		}
	}
	
	/*
	 * This writes the contents of 'results' from the Database object to the file path supplied to crawl
	 */
	public void writeDatabase(File file) {
		FileOutputStream fileOut = null;
		try {
			XStream stream = new XStream(new DomDriver());
			stream.alias("links", LinkedList.class);
			stream.alias("url", String.class);
			fileOut = new FileOutputStream(file);
         	stream.toXML(db.getResults(), fileOut);
        } catch (FileNotFoundException ex) {        
        	System.out.println("The file: " + file.getPath() + " either cannot be created or cannot be opened! Please check directory.");
        } catch (InitializationException ex) {
        	System.out.println("There was a problem with the parser. It cannot be accessed.");
        	ex.printStackTrace();
        } catch (XStreamException ex) {
        	System.out.println("There was a problem with the parser. It cannot write the XML.");
        	ex.printStackTrace();
        } finally {
        	if(fileOut != null) {
        		try {
					fileOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
	}

	abstract boolean search(String url);
	
	/*
	 * FOR TESTING ONLY!
	 */
	public Database getDatabase() { //Added NEW TESTING STUFF; WATCH THIS, MAY CREATE PROBLEMS.
		if (db == null) {
			db = new Database();
		}
		return db;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public int getMaxLinks() {
		return maxLinks;
	}
	
	public int getDefaultMaxDepth() {
		return DEFAULT_MAX_DEPTH;
	}
	
	public int getDefaultMaxLinks() {
		return DEFAULT_MAX_LINKS;
	}
}

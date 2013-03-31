
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
			System.out.println("Maximum depth and maximum no. of links to search have minimum values of 0 and 1 respectively (ie. when just the seed URL is searched); setting to default values:");
			System.out.println("\tmax depth = " + DEFAULT_MAX_DEPTH + "\tmax links = " + DEFAULT_MAX_LINKS);
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
				System.out.println("***********************************");
				System.out.println("After load linksAdded:");
				for(String next : db.getLinksAdded()) {
					System.out.println(next);
				}
				System.out.println("After load results:");
				for(String next : db.getResults()) {
					System.out.println(next);
				}
				System.out.println("***********************************");

			} catch (XStreamException ex) {
				throw new IllegalArgumentException("Problem reading XML from " + dbFile +", please check it is of the correct format");
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
			System.out.println("************************");
			System.out.println("Current depth = " + currentPriority);
			System.out.println("No. of links at this depth = " + size);
			System.out.println("No. of results = " + db.getResults().size());
			System.out.println("Results so far:");
			for(String next : db.getResults()) {
				System.out.println(next);
			}
			System.out.println("************************");
			while(linksProcessed < maxLinks && index < size) {
				System.out.println("************************");
				System.out.println("Processing link no. " + (index+1)+ " at depth " + currentPriority);
				System.out.println("************************");
				url = linksAtThisDepth.get(index);
				// grab links from the current page
				try {
					URL site = new URL(url);
					List<String> foundLinks = getLinksFromPage(site);
					if(foundLinks == null) { // unable to open site at url, move on
						index++;
						continue;
					}
					System.out.println("*****************************");
					System.out.println("Adding found links to DB");
					System.out.println("*****************************");

					for(String link : foundLinks) {
						db.addLink(currentPriority + 1, link);
					}
					
					System.out.println("*****************************");
					System.out.println("links added now:");
					System.out.println("*****************************");

					for(String next : db.getLinksAdded()) {
						System.out.println(next);
					}
					System.out.println("*****************************");
					System.out.println("results now:");
					System.out.println("*****************************");

					for(String next : db.getResults()) {
						System.out.println(next);
					}
					
					// call user-defined search within try block to avoid calling on bad URLs
					if(search(url)) {
						
						System.out.println("adding result: " + url);
						
						db.addResult(url);
					}
					linksProcessed++;
					
					System.out.println("************************");
					System.out.println("No. of links processed =  "+linksProcessed);
					System.out.println("No. of results = " + db.getResults().size());
					System.out.println("************************");
				
					} catch (MalformedURLException e) {
						System.out.println("URL " + url + " malformed");
					}
				index++;				
			}
		currentPriority++;
		}
		
		System.out.println("***********************************");
		System.out.println("RESULTS!");
		for(String next : db.getResults()) {
			System.out.println(next);
		}
		System.out.println("***********************************");
		
		writeDatabase(file);
	}
	
	/*
	 * Extracts all links from A tags on the given page, of the form <a * href="*" *>,
	 * interprets them using the context of the given page, and returns them in a list.
	 * If the given page cannot be connected to, returns a null list.
	 */	
	private List<String> getLinksFromPage(URL page) {
		List<String> links = null;
		InputStream stream;
		try {
			stream = page.openStream();
		} catch (IOException e1) {
			System.out.println("Could not connect to " + page.toString());
			return links;
		}		
		System.out.println("*****************************************");
		System.out.println("Scraping: " + page);
		System.out.println("*****************************************");

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
	private void loadDatabase(File file) throws XStreamException {
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
	private void writeDatabase(File file) {
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
}

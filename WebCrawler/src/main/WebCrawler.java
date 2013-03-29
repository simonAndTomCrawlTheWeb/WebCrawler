
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {
	private HTMLread reader = new HTMLreadImpl(); // DO WE NEED THIS?
	private final static int DEFAULT_MAX_LINKS = 10;
	private final static int DEFAULT_MAX_DEPTH = 100;
	private final int maxDepth, maxLinks;
	private Database db;
	
	public WebCrawler() {
		this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_LINKS);
	}
	
	public WebCrawler(int maxDepth, int maxLinks) {
		//Negative or zero input integers: what to do? GOOD QUESTION!!! Let's 2.0 it!
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
	public final void crawl(String url,String dbFile) {
		File file = new File(dbFile); //What if dbFile is null? Handle NullPointerException?
		if(file.exists() && file.isFile()) {
			loadDatabase(file);	
		} else {
			db = new Database();
		}
		
	}
	
	public List<String> getLinksFromPage(InputStream stream) {
		List<String> links = new LinkedList<>();
		boolean endOfTag = false;
		boolean endOfPage = false;
		while(!endOfPage) {
			if(reader.readUntil(stream, '<', '\u001a')) {
				String tag = reader.readString(stream, ' ', '>');
				if(tag != null && tag.equalsIgnoreCase("a ")) {
					// found anchor - now look for href...
					while(!endOfTag) {
						if(reader.readUntil(stream, 'h', '>')) {
							String attr = reader.readString(stream, '"', '>');
							if(attr != null && attr.equalsIgnoreCase("ref=\"")) {
								// found the link URL
								String newLink = reader.readString(stream, '"', '>');
								if(newLink != null) {
									links.add(newLink.substring(0, newLink.length() - 1)); // remove the " at end of url
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
	private void writeDatabase(Database db) {
		XStream stream = new XStream(new DomDriver());
		stream.alias("links", LinkedList.class);
		stream.alias("url", String.class);
		try {
            FileOutputStream file = new FileOutputStream("database.txt");
            stream.toXML(db.getResults(), file);
        } catch (FileNotFoundException ex) {
        	System.out.println("The file was not found!");
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

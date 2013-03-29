
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	//private HTMLread reader = new HTMLreadImpl(); DO WE NEED THIS?
	private final static int DEFAULT_MAX_LINKS = 10;
	private final static int DEFAULT_MAX_DEPTH = 100;
	private final int maxDepth, maxLinks;
	private Database db;
	
	public WebCrawler() {
		this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_LINKS);
	}
	
	public WebCrawler(int maxDepth, int maxLinks) {
		//Negative or zero input integers: what to do?
		this.maxDepth = maxDepth; 
		this.maxLinks = maxLinks;
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
		
		int numberOfLinksFound = 1; //Including url given to method as one of the links
		int currentPriority = 0; //The url parameter is the only link with priority 0
		int index = 0; //The index of the current link in linksToCrawl.get(currentPriority)
		db.addLink(currentPriority,url); //Add url to links database

		while (currentPriority <= maxDepth && numberOfLinksFound < maxLinks) { //Stops user-settings on depth and link number being exceeded
			try {
				Document htmlDoc = Jsoup.connect(url).get(); //Parse page (see Jsoup API)
				Elements allATags = htmlDoc.select("a[href]"); //Find all 'a-tags' with an 'href'-attribute
				int i = 0; //The index of allATags
				while ((i < allATags.size()) && (numberOfLinksFound < maxLinks)) { //Stops maxLinks being exceeded and IndexOutOfBoundsExceptions on allATags
					db.addLink(currentPriority+1,allATags.get(i).attr("abs:href").toString()); //Adds absolute address of links found (with relevant priority)
					//System.out.println(allATags.get(i).attr("abs:href").toString());
					numberOfLinksFound++;
					i++;
				}
			} catch (IllegalArgumentException ex) {
				System.out.println("That was not a valid url..."); //Should this be kept in the index?
				//ex.printStackTrace();
			} catch (IOException ex) {
				System.out.println("There was an I/O problem...");
				//ex.printStackTrace();
			}
			
			/*
			 * 'Future programmers' search functionality
			 */
			boolean result = search(url); //Do we always want to hand the url over to search? See exceptions above.
			if (result) {
				db.addResult(url);
			}
			
			index++;
			if (index == db.getLinksToCrawl().get(currentPriority).size()) { //Checks if we have crawled all the links in the current priority group
				currentPriority++;
				index = 0;
				if (db.getLinksToCrawl().get(currentPriority) != null) { //Checks if there are any links of a higher priority that need to be crawled
					url = db.getLinksToCrawl().get(currentPriority).get(index); //Update url-"to crawl" to first element of the next higher priority group
				} else {
					//There are no higher priority links to crawl
					return;
				}
			} else {
				//Update url-"to crawl" to be the next element along in the current priority group
				url = db.getLinksToCrawl().get(currentPriority).get(index);
			}
		}
		writeDatabase(db);
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

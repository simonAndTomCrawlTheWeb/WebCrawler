
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
					numberOfLinksFound++;
					i++;
				}
			} catch (IllegalArgumentException ex) {
				System.out.println("That was not a valid url..."); //Should this be kept in the index?
				ex.printStackTrace();
			} catch (IOException ex) {
				System.out.println("There was an I/O problem...");
				ex.printStackTrace();
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
	}
	
	private void loadDatabase(File file) {
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		
		try {
			fileIn = new FileInputStream(file);
			in = new ObjectInputStream(fileIn);
			db = (Database) in.readObject();
		} catch (FileNotFoundException e) {
			System.out.println("The file at " + file.getPath() + " does not exist, or cannot be opened...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("There was an I/O problem...");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Database class could not be found...");
			e.printStackTrace();
		} finally {
			if(fileIn != null) {
				try {
					fileIn.close();
				} catch (IOException e) {
					System.out.println("An I/O error has occurred...");
					e.printStackTrace();
				}
			}
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("An I/O error has occurred...");
					e.printStackTrace();
				}
			}
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

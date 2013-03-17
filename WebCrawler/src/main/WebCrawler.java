
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
	
	public final void crawl(String url,String dbFile) {
		
		/*
		 * Loads information from db file or creates an empty database
		 */
		File file = new File(dbFile); //What if dbFile is null? Handle NullPointerException?
		if(file.exists() && file.isFile()) {
			loadDatabase(file);	
		} else {
			db = new Database();
		}
		
		//Variables keep track of what needs to be crawled
		int numberOfLinksFound = 0;
		int currentPriority = 0;
		int index = 0;
		
		while (currentPriority <= maxDepth && numberOfLinksFound < maxLinks) {
			//Add link to be crawled
			db.addLink(currentPriority,url);
			
			try {
				Document htmlDoc = Jsoup.connect(url).get();
				Elements allATags = htmlDoc.select("a[href]");
				System.out.println("Number of A tags: " + allATags.size());
				int i = 0;
				while (i < allATags.size() && numberOfLinksFound < maxLinks) {
					db.addLink(currentPriority+1,allATags.get(i).attr("abs:href").toString());
					numberOfLinksFound++;
					i++;
				}
			} catch (IOException ex) {
				System.out.println("There was an I/O problem...");
				ex.printStackTrace();
			}
			
			//'Future programmers' search functionality
			boolean result = search(url);
			if (result) {
				db.addResult(url);
			}
			
			index++;
			//Update priority and url to be crawled
			if (index == db.getLinksToCrawl().get(currentPriority).size()) {
				currentPriority++;
				index = 0;
				if (db.getLinksToCrawl().get(currentPriority) != null) {
					url = db.getLinksToCrawl().get(currentPriority).get(index);
				} else {
					numberOfLinksFound = maxLinks;
				}
			} else {
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

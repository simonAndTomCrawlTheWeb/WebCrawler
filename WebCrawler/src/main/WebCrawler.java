
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
	
	private void crawl(String url,int priority,int index) {
		try {
			Document htmlDoc = Jsoup.connect(url).get();
			Elements allATags = htmlDoc.select("a[href]");
			int i = 0;
			int requiredLinks = maxLinks-db.getLinksAdded().size();
			while (i < requiredLinks && (i < allATags.size())) {
				db.addLink(priority,allATags.get(i).attr("abs:href").toString());
				i++;
			}
		} catch (IOException ex) {
			System.out.println("There was an I/O problem...");
			ex.printStackTrace();
		}
		boolean result = search(url);
		if (result) {
			db.addResult(url);
		}
		if (db.getLinksAdded().size() >= maxLinks) {
			System.out.println("here");
			return;
		}
		index++;
		if (index == db.getLinksToCrawl().get(priority).size()) {
			if (db.getLinksToCrawl().size() == priority || priority == maxDepth) {
				return;
			} 
			crawl(db.getLinksToCrawl().get(priority+1).get(0),priority+1,0);
		} else {
			crawl(db.getLinksToCrawl().get(priority).get(index),priority,index);
		}
	}
	
	public final void crawl(String url, String dbFile) {
		File file = new File(dbFile); //What if dbFile is null? Handle NullPointerException?
		if(file.exists() && file.isFile()) {
			loadDatabase(file);	
		} else {
			db = new Database();
		}
		db.addLink(0,url);
		crawl(url,1,0);
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

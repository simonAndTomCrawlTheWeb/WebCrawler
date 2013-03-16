/**
 * 
 */
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import main.HTMLreadImpl;
/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {
	
	//private HTMLread reader = new HTMLreadImpl(); DO WE NEED THIS?
	
	private final static int DEFAULT_MAX_LINKS = 100;
	private final static int DEFAULT_MAX_DEPTH = 10;
	
	private final int maxDepth, maxLinks;
	
	private Database db;
	
	public WebCrawler() {
		this(DEFAULT_MAX_DEPTH, DEFAULT_MAX_LINKS);
	}
	
	public WebCrawler(int maxDepth, int maxLinks) {
		this.maxDepth = maxDepth;
		this.maxLinks = maxLinks;
	}

	public final void crawl(String url, String dbFile) {
		File file = new File(dbFile);
		if(file.exists() && file.isFile()) {
			loadDatabase(file);	
		} else {
			db = new Database();
		}
		try {
			processPage(url);
		} catch (MalformedURLException e) {
			System.out.println("Not a well-formed URL...");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("There was an I/O problem...");
			e.printStackTrace();
		}
		
		boolean result = search(url);
		if (result) {
			db.addResult(url);
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

	private void processPage(String urlStr) throws IOException, MalformedURLException {
		URL url = new URL(urlStr);
		InputStream stream = url.openStream();
		
	}

	abstract boolean search(String url);
}

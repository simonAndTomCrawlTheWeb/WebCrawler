package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class to store the URL links found by WebCrawler.
 * Links to be crawled are stored in lists in linksToCrawl, mapped to their priority.
 * Links to be stored after a successful search are added to the results list.
 * @author tomAndSimon
 */
public class Database {
	private Map<Integer, LinkedList<String>> linksToCrawl = new HashMap<Integer, LinkedList<String>>();
	private List<String> linksAdded = new LinkedList<>(); // used as reference to avoid adding duplicate links
	private List<String> results = new LinkedList<>();
	
	public Database(List<String> inputData) {
		this();
		linksAdded.addAll(inputData);
		results.addAll(inputData);
	}
	
	public Database() {
		linksAdded = new LinkedList<>();
		results = new LinkedList<>();
	}
	
	/**
	 * Adds a future URL to crawl, along with its priority. 
	 * @param priority the priority of the URL being added
	 * @param url the URL to add
	 * @return true if the link is distinct and has not previously been added, false otherwise.
	 */
	public boolean addLink(int priority, String url) {
		boolean added = false;
		if(!linksAdded.contains(url)) {
			if(linksToCrawl.containsKey(priority)) {
				linksToCrawl.get(priority).add(url);
			} else {
				LinkedList<String> newList = new LinkedList<>();
				newList.add(url);
				linksToCrawl.put(priority, newList);
			}
			linksAdded.add(url);
			added = true;
		}
		return added;
	}
	
	/**
	 * Adds a searched URL to the results list.
	 * @param url the URL to add
	 */
	public void addResult(String url) {
		if(!results.contains(url)) {
			results.add(url);
		}
	}
	
	/**
	 * Returns all URLs to be crawled of the given priority.
	 * @param priority the priority of the links to return
	 * @return the list of links of the given priority
	 */
	public List<String> getLinksOfPriority(int priority) {
		return linksToCrawl.get(priority);
	}
	
	/**
	 * Returns the map of links to be crawled
	 * @return the map of links to be crawled
	 */
	public Map<Integer, LinkedList<String>> getLinksToCrawl() {
		return linksToCrawl;
	}
	
	/**
	 * Returns the results list.
	 * @return the results list
	 */
	public List<String> getResults() {
		return results;
	}
}

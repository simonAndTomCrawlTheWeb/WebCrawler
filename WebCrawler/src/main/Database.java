package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database {
	/*
	 * Should we hide this class? Factory or singleton or inner or anonymous, etc.
	 */
	private Map<Integer, LinkedList<String>> linksToCrawl = new HashMap<Integer, LinkedList<String>>();
	private List<String> linksAdded = null;
	private List<String> results = new LinkedList<String>();
	
	public Database(List<String> inputData) {
		linksAdded = inputData;
	}
	
	public Database() {
		linksAdded = new LinkedList<String>();
	}
	
	public boolean addLink(int priority, String url) {
		boolean added = false;
		if(!linksAdded.contains(url)) {
			if(linksToCrawl.containsKey(priority)) {
				linksToCrawl.get(priority).add(url);
			}
			else {
				LinkedList<String> newList = new LinkedList<>();
				newList.add(url);
				linksToCrawl.put(priority, newList);
			}
			linksAdded.add(url);
			added = true;
		}
		return added;
	}
	
	public void addResult(String url) {
		if(!results.contains(url)) {
			results.add(url);
		}
	}
	
	public List<String> getLinksOfPriority(int priority) {
		return linksToCrawl.get(priority);
	}
	
	public Map<Integer, LinkedList<String>> getLinksToCrawl() {
		return linksToCrawl;
	}
	
	public List<String> getResults() {
		return results;
	}
	
	//Added by Tom; perhaps find a way to crawl without using this?
	public List<String> getLinksAdded() {
		return linksAdded;
	}
}

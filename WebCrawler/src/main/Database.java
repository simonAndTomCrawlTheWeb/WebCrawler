package main;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database implements Serializable {
	private static final long serialVersionUID = 42L;
	
	private Map<Integer, LinkedList<String>> linksToCrawl = new HashMap<Integer, LinkedList<String>>();
	private List<String> linksAdded = new LinkedList<String>();
	private List<String> results = new LinkedList<String>();
	
	public void addLink(int priority, String url) {
		if(!linksAdded.contains(url)) {
			if(linksToCrawl.containsKey(priority)) {
				linksToCrawl.get(priority).add(url);
			}
			else {
				LinkedList<String> newList = new LinkedList<>();
				newList.add(url);
				linksToCrawl.put(priority, newList);
			}
		}
		linksAdded.add(url);
	}
	
	public void addResult(String url) {
		if(!results.contains(url)) {
			results.add(url);
		}
	}
	
	public Map<Integer, LinkedList<String>> getLinksToCrawl() {
		return linksToCrawl;
	}
	
	public List<String> getResults() {
		return results;
	}
}

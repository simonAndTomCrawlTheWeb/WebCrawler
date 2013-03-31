/**
 * 
 */
package util;

import static main.HTMLread.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * A utilities class used by WebCrawler.
 * @author tomAndSimon
 */
public class CrawlerUtilities {
	/*
	 * Extracts all links from A tags on the given page, of the form <a * href="*" *>,
	 * interprets them using the context of the given page, and returns them in a list.
	 * If the given page cannot be connected to, returns a null list.
	 */	
	/**
	 * Extracts all links from anchor tags on the given page, of the form <a * href="*" *>,
	 * interprets them using the context of the given page, and returns them in a list.
	 * If the given page cannot be connected to, returns a null list.
	 * @param page the web page to extract links from
	 * @return a list of all link URLs found as strings
	 */
	public static List<String> getLinksFromPage(URL page) {
		List<String> links = null;
		InputStream stream;
		try {
			stream = page.openStream();
		} catch (IOException e1) {
			System.out.println("Could not connect to " + page.toString());
			return links;
		}		
		links = new LinkedList<>();
		boolean endOfTag = false;
		boolean endOfPage = false;
		while(!endOfPage) {
			if(readUntil(stream, '<', '\u001a')) {
				endOfTag = false;
				String tag = readString(stream, ' ', '>');  
				if(tag != null && tag.equalsIgnoreCase("a ")) {
					// found anchor - now look for href...
					while(!endOfTag) {
						if(readUntil(stream, 'h', '>')) {
							String attr = readString(stream, '=', '>');
							
							if(attr != null && attr.replaceAll("\\s", "").equalsIgnoreCase("ref=")) {
								// found the link URL -- need to find whether it's written '/like/this/' or "/like/this/"
								char openQuote = skipSpace(stream, '>');
								if(openQuote == Character.MIN_VALUE) {
									// nothing between href= and >
									endOfTag = true;
									continue;
								}
								// read out the address
								String longLink = readString(stream, openQuote, '>');
								if(longLink != null) {
									String link = longLink.substring(0, longLink.length() -1); 	// trim the trailing quote mark at end of longLink
									link = link.trim();										   	// link address can be empty/whitespace to refer to current page - we should ignore these									
									if(!link.isEmpty()) {                
										if(link.charAt(0) != '#') {								// ignore links to the same page
											try {
												URL linkUrl = new URL(page, link);              // interpret link in context of current page
												links.add(linkUrl.toString().toLowerCase());
											} catch (MalformedURLException e) {
												// Something's wrong with this link - ignore it
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

}

/**
 * 
 */
package main;

import java.net.URL;

/**
 * @author tomAndSimon
 *
 */
public abstract class WebCrawler {

	public final void crawl(URL url) {
		
		search(url);
	}
	
	abstract boolean search(URL url);
}

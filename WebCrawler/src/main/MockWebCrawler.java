/**
 * 
 */
package main;

import java.net.URL;

/**
 * An mock implementation of search() for use in testing.
 * 
 * @author tomAndSimon
 *
 */
public class MockWebCrawler extends WebCrawler {

	@Override
	boolean search(URL url) {
		if(url.toString().equals("http://en.wikipedia.org/wiki/Spider")) {
			return true;
		}
		return false;
	}

}

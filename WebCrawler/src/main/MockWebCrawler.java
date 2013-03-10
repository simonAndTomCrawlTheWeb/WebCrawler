/**
 * 
 */
package main;

/**
 * An mock implementation of search() for use in testing.
 * 
 * @author tomAndSimon
 *
 */
public class MockWebCrawler extends WebCrawler {

	@Override
	boolean search(String url) {
		if(url.toString().equals("http://en.wikipedia.org/wiki/Spider")) {
			return true;
		}
		return false;
	}

}

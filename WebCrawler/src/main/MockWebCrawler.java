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
	
	public MockWebCrawler() {
		super();
	}

	public MockWebCrawler(int i, int j) {
		super(i,j);
	}

	@Override
	boolean search(String url) {
		if(url.toString().equals("http://en.wikipedia.org/wiki/Spider")) {
			return true;
		}
		return false;
	}

}

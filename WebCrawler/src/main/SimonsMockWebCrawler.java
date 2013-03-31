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
public class SimonsMockWebCrawler extends WebCrawler {	
	public SimonsMockWebCrawler() {
		super();
	}

	public SimonsMockWebCrawler(int i, int j) {
		super(i,j);
	}

	@Override
	public boolean search(String url) {
		return true;
	}

}

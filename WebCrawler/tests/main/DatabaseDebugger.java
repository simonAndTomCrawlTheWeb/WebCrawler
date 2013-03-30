package main;

import java.io.File;
import java.util.List;

public class DatabaseDebugger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebCrawler crawler = new MockWebCrawler();
		File file = new File("dataTest.txt");
		Database db = crawler.getDatabase();
		List<String> linksAddedList = db.getLinksAdded();
		if (linksAddedList.size() == 0) {
			System.out.println("Empty results list");
		} else {
			System.out.println("Not empty");
		}
		db.addResult("http://www.checkthis.com");
		db.addResult("yo");
		crawler.writeDatabase(file);
	}

}

/**
 * 
 */
package main;

/**
 * @author tohaga01
 *
 */
public class CrawlerDebugger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebCrawler crawler = new MockWebCrawler(2,300);
	    crawler.crawl("http://www.bbc.co.uk","WCTest.txt");
	    /*
	    Database myDb = crawler.getDatabase();
	    Map<Integer, LinkedList<String>> linksToCrawl = myDb.getLinksToCrawl();
	    LinkedList<String> priorityZero = linksToCrawl.get(0);
	    LinkedList<String> priorityOne = linksToCrawl.get(1);		
	    System.out.println("The size of the priority 0 linked list is:" + priorityZero.size());
	    System.out.println("The size of the priority 1 linked list is:" + priorityOne.size());
	    for (String l: priorityZero) {
	    	System.out.println("This is in priority 0:" + l);
	    }
	    System.out.println("***************");
	    for (String next: priorityOne) {
	    	System.out.println("This is in priority 1:" + next);
	    }
	    LinkedList<String> priorityTwo = linksToCrawl.get(2);
	    System.out.println("The size of the priority 2 linked list is:" + priorityTwo.size());
	    System.out.println("***************");
	    for (String each: priorityTwo) {
	    	System.out.println("This is in priority 2:" + each);
	    }
	    System.out.println("***********Links in total list");
	    System.out.println("Total Links Found: " + myDb.getLinksAdded().size());
	
	    for (String next: myDb.getLinksAdded()) {
	    	System.out.println(next);
	    }
	    System.out.println("Total Links Found: " + myDb.getLinksAdded().size());
	    if (linksToCrawl.get(3) == null) {
	    	System.out.println("blah");
	    }
	    System.out.println("Size of priority 3 list: " + linksToCrawl.get(3).size());
	    for (String each: linksToCrawl.get(3)) {
	    	System.out.println("Priority 3 link: "+ each);
	    }
	    */
	}

}

package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Debugger {
	public static void main(String[] args) throws Exception {
		WebCrawler crawler = new SimonsMockWebCrawler(1,5);
		File proto = new File("premadeDbProto.txt");
		File premade = new File("premadeDb.txt");
		if(premade.exists()) {
			premade.delete();
		}
		premade.createNewFile();
		copyFile(proto, premade);
		
		crawler.crawl("http://www.bbc.co.uk/", "premadeDb.txt");
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}

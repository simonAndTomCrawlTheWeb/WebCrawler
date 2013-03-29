WebCrawler
==========

To-Do list (revised after asking Keith about specification):

Core Design Features
====================
* Rewrite WebCrawler so that HTMLread methods are used in the class.
* The results table is persistent and the temporary table is transient. Make it so.
* Make WebCrawler not go to links if they are already stored in the 'database info' when it is passed to crawl()

Obviously
=========
* Test,test,test!

Additional Features
===================
* Add some basic awareness of robots.txt.
* Add some concurrency; one crawler per core, with a shared temporary table.

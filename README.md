WebCrawler
==========

Stuff just done (hopefully not buggy)
=====================================
* Results table is persistent and temporary table is transient now.
* WebCrawler does not go to links if they are already in file passed to crawl()
* Crawler serializes and deserializes results as XML.

To-Do list (revised after asking Keith about specification):

Core Design Features
====================
* Rewrite WebCrawler so that HTMLread methods are used in the class.
* Type check the XML when it is deserialized to object which is case to List<String>. InstanceOf is frowned upon; use some kind of dtd/schema.
Indeed if the file contains a load of rubbish or is empty we need a way of dealing with that
* Hide Database class in some way or other; inner class?!

Obviously
=========
* Test,test,test!

Additional Features
===================
* Add some basic awareness of robots.txt.
* Add some concurrency; one crawler per core, with a shared temporary table.

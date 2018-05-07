# 163Music

A web spider music download application build on JavaFX.

# Data Source
* Music Information fetched from http://music.163.com
* Music are downloaded from https://ouo.us/fm/163/

# Development

163Music is developed using various libraries
* [Maven](http://maven.apache.org/) is used to resolve dependencies and to build 163music.
* [JSoup](https://jsoup.org/download) is a Web Crawler Library used to fetched information for website
* [mp3agic](https://github.com/mpatric/mp3agic) is used for mp3 tags edition
* [JFoenix](https://github.com/jfoenixadmin/JFoenix) is a JavaFX Material Design Library

## Building

To build 163Music, you will need:

* [JDK 8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) -Java Development Kit (version 8 or up)
* [maven](http://maven.apache.org/) - Version 3 recommended

Please Note that, for JDK 9 or up, You must uses JFoenix v9.0.4 

Change pom.xml to
'''xml
<dependency>
    <groupId>com.jfoenix</groupId>
    <artifactId>jfoenix</artifactId>
    <version>9.0.4</version>
</dependency>
'''

# Release

To uses 163Music v0.2, download the jar executable
* [163Music v0.2](https://github.com/CRonYii/163MusicDownloader/releases)

If any error occur during runtime, please locate log files under the ./log folder



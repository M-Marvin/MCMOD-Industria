# MCMOD-Industria
This is a Multi Project Build.
To get this Project working, the following steps must be executed:
- Project "IndustriaCore"
 - run 'runClient' to start game in IDE (only core mod)
 - run 'shadowJar' to produce production jar
 - run 'publishToMavenLocal'
- Project "Industria"
 - run 'shadowJar' to produce production jar
 - run 'runClient' to start game in IDE (with cotent mod)

NOTE:
This mod is still under developement, currently an additonal Project 'LIBRARY-ElectronFlow' is required to build and run this.
This Project must be downloaded and build ('publishToMavenLocal') first, before the core mod can be build.

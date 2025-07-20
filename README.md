# MCMOD-Industria
This is a Multi Project Build.
To get this Project working, the following steps must be executed:
* Project "IndustriaCore"
	* run `runClient` to start game in IDE (only core mod)
	* run `shadowJar` to produce production jar
	* run `publishToMavenLocal`
* Project "Industria"
	* run `shadowJar` to produce production jar
	* run `runClient` to start game in IDE (with content mod)

# What is this?

This is a SonarQube plugin that integrates Veracode scan results into a SonarQube project.

# Upgrading

placeholder

# Installing

Please see the RELEASE_NOTES.md file for info on what's new.

Assuming you don't want to build this from scratch (see below for instructions), download the latest version from the releases directory and copy into your {SonarQube_Home}/extensions/plugins.  Then restart the SonarQube server.  See the offical SonarQube docs [here](https://docs.sonarqube.org/display/SONAR/Installing+a+Plugin) for more info and follow the "Manual Installation" process.

You will probably also need the command-line sonar scanner as I haven't tested this with other SonarQube plugins
like their Jenkins or VSTS plugin.  Or it might just work with these plugins - feedback is appreciated.

# Using

There are a few properties that need to get set in the sonar-project.properties file:
- sonar.veracode.appName=Name of your app as it appears on the Veracode Platform
- sonar.veracode.apiId=API ID for the account that will access the Veracode Platform
- sonar.veracode.apiKey=API Key for the account that will access the Veracode Platform

Note that it is possible to pass these as parameters to the command-line scanner instead of defining them in the
properties file, for example:

`~/bin/sonar-scanner-3.1-SNAPSHOT/bin/sonar-scanner -Dsonar.veracode.apiId=XXXXX  -Dsonar.veracode.apiKey=YYYYYYYY`
    
Support for proxies is also provided - see the sample plugin-test/sonar-project.properties file.

When the scanner is run it will pull the latest report from Veracode and add the Veracode issues into the project.  Some notes:
- Since Veracode does not have the source code for the project, the issues will show up as part of the project and 
not get linked back to a specific source file.
- All issues added from the Veracode report will have the 'veracode' tag set on them.

# Building

Clone the repo and build with `mvn package` then copy the resulting .jar file into the SonarQube server's plugins directory and restart the server.

Additional debug log info will be produced if you run the scanner with the '-X' option.

# Help with problems
Please log an issue.  If possible, run the scanner with the '-X' option to produce debug output and provide me with a snippet of the scanner log showing the problem.

# A note about the author
While it's true that I work for Veracode, this is NOT an official Veracode-supported product.  I've written this in my own time in an effort to help support our customers.


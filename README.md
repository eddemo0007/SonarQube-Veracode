# What is this?

This is a SonarQube plugin that integrates Veracode scan results into a SonarQube project.

# Upgrading

placeholder

# Installing

Please see the RELEASE_NOTES.md file for info on what's new.

Assuming you don't want to build this from scratch (see below for instructions), download the latest version from the releases directory and:

placeholder

# Using

set properties in the scanner props file
    AppName
    API ID/Key
    

manually create the detailed report in the folder with the code to analyize (or it's own folder)
    rename to .vxml

run the sonar-scanner - the .vxml file will get processed and added to the results

# Configuration

XX

# Building

scanner logs - use with -X

server logs
    - props file
    - which log file to read

# Future plans

XX

# Help with problems
Please log an issue.  At a minimum I'll need your build log and the information you put into each field for the task.

# A note about the author
While it's true that I work for Veracode, this is NOT an official Veracode-supported product.  I've written this in my own time in an effort to help support our customers.


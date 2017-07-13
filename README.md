# Jenkins JMH Report Plugin

Visually explore your [JMH](http://openjdk.java.net/projects/code-tools/jmh/) benchmarks on [Jenkins](https://jenkins.io/)!

Based on [JMH Visualizer](http://jmh.morethan.io). 

Given your project runs JMH benchmarks, you can use this plugin to visualize the results of it. 
The results need to be stored in JSON therefore.

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/jmh-report-plugin/master)](https://ci.jenkins.io/job/Plugins/job/jmh-report-plugin/job/master/)


# Building the project

- Setup project for eclipse: ```mvn -DdownloadSources=true eclipse:eclipse ```
- Run a Jenkins instance with the plugin deployed
  - ```mvn hpi:run```
  - go to http://127.0.0.1:8080/jenkins
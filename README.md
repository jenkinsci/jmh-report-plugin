# jenkins-jmh

Visually explore your [JMH](http://openjdk.java.net/projects/code-tools/jmh/) benchmarks on [Jenkins](https://jenkins.io/)!
Based on [JMH Visualizer](http://jmh.morethan.io). 

# Building the project

- Setup project for eclipse: ```mvn -DdownloadSources=true eclipse:eclipse ```
- Run a Jenkins instance with the plugin deployed
  - ```mvn hpi:run```
  - go to http://127.0.0.1:8080/jenkins
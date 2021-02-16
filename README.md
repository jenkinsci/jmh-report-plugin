# Jenkins JMH Report Plugin

Visually explore your [JMH](http://openjdk.java.net/projects/code-tools/jmh/) benchmarks on [Jenkins](https://jenkins.io/)!

Based on [JMH Visualizer](http://jmh.morethan.io). 

Given your project runs JMH benchmarks, you can use this plugin to visualize the results of it. 
The results need to be stored in JSON therefore.

[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/jmh-report-plugin/master)](https://ci.jenkins.io/job/Plugins/job/jmh-report-plugin/job/master/)

# Setup in Jenkins

## As a pipeline step

```jmhReport 'build/reports/jmh/result.json'```

OR

```step([$class: 'RunPublisher', resultPath: 'build/reports/jmh/result.json'])```

A complete pipeline could look like that:
```
node {
    checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: 'https://github.com/ben-manes/caffeine.git']]])
    sh './gradlew jmh -PincludePattern=".*DelegationBenchmark.*"'
    jmhReport 'caffeine/build/reports/jmh/results.json'
}
```

## As a post-build action

![post-build action](https://wiki.jenkins.io/download/attachments/133956211/jmh-report-configuration.png?version=1&modificationDate=1501507215532&api=v2)

## As a Job-DSL-Plugin

```jmhReport { resultPath('build/reports/jmh/result.json') }```

A complete job-dsl example could look like that:
```
job('example-1') {
    steps {
        scm {
            git("https://github.com/ben-manes/caffeine.git", "master")
        }
        shell('./gradlew jmh -PincludePattern=".*DelegationBenchmark.*"')
        publishers {
            jmhReport {
                resultPath('build/reports/jmh/result.json')
            }
        }
    }
}
```

# Building the project

- Setup project for eclipse: ```mvn -DdownloadSources=true eclipse:eclipse ```
- Run a Jenkins instance with the plugin deployed
  - ```mvn hpi:run```
  - go to http://127.0.0.1:8080/jenkins
  

# Releasing the project

- (Only on jmh-visualizer update) Copy bundle.js (from jmh-visualizer) into src/main/webapp/
- (Only on major version change) Change version in pom.xml to ${version}-SNAPSHOT 
- Test the Plugin in dev mode: ```mvn hpi:run```
- Test the Plugin on a Jenkins instance:
  - Build the HPI: ```mvn hpi:hpi```
  - Download and install Jenkins (https://jenkins.io/download/)
  - Start and stop on a Mac with 
    - sudo launchctl load /Library/LaunchDaemons/org.jenkins-ci.plist
    - sudo launchctl unload /Library/LaunchDaemons/org.jenkins-ci.plist
  - Upload the HPI (*target/jmh-report.hpi*) into the [local Jenkins instance](http://localhost:8080/)  
- Commit & Push
- Release: ```mvn release:prepare release:perform```
- Update https://wiki.jenkins.io/display/JENKINS/JMH+Report+Plugin

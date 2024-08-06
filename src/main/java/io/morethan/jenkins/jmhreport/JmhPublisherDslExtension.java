package io.morethan.jenkins.jmhreport;

import hudson.Extension;
import javaposse.jobdsl.dsl.helpers.publisher.PublisherContext;
import javaposse.jobdsl.plugin.ContextExtensionPoint;
import javaposse.jobdsl.plugin.DslExtensionMethod;

@Extension(optional=true)
public class JmhPublisherDslExtension extends ContextExtensionPoint
{
	@DslExtensionMethod(context=PublisherContext.class)
	public Object jmhPublisher(String resultPath)
	{
		RunPublisher runPublisher = new RunPublisher();
		runPublisher.setResultPath(resultPath);
		return runPublisher;
	}
}

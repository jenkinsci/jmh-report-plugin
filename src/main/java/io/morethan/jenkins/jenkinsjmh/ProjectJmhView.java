package io.morethan.jenkins.jenkinsjmh;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Result;
import hudson.model.Run;

/**
 * The {@link Action} responsible for displaying the JMH report page on project
 * level.
 * 
 * <p>
 * See corresponding Jelly files under src/main/resources.
 * </p>
 */
public class ProjectJmhView implements Action, Serializable {

	private static final long serialVersionUID = 1L;

	private final AbstractProject<?, ?> _project;

	public ProjectJmhView(AbstractProject<?, ?> project) {
		_project = project;
	}

	@Override
	public String getIconFileName() {
		return Constants.ICON_NAME;
	}

	@Override
	public String getDisplayName() {
		return "JMH Report";
	}

	@Override
	public String getUrlName() {
		return "jmh-report";
	}

	public AbstractProject<?, ?> getProject() {
		return _project;
	}

	public String getProvidedJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		return new StringBuilder(contextPath).append("/job/").append(getProject().getName()).append('/')
				.append("/jmh-report/provided-").append(getProject().getLastSuccessfulBuild().getNumber()).append(".js")
				.toString();
	}

	public void doDynamic(final StaplerRequest request, final StaplerResponse response)
			throws IOException, ServletException {
		ProvidedJsBuilder jsBuilder = new ProvidedJsBuilder();
		int addedReports = 0;
		for (Run run : _project.getBuilds()) {
			if (run.getResult() == Result.SUCCESS || run.getResult() == Result.UNSTABLE) {
				File reportFile = new File(run.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
				jsBuilder.addRun(Integer.toString(run.getNumber()), reportFile);
				addedReports++;
				if (addedReports == 2) {
					break;
				}
			}
		}
		response.setContentType("text/javascript;charset=UTF-8");
		response.getWriter().println(jsBuilder.buildReverse());
	}

}

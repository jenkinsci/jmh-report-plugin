package io.morethan.jenkins.jmhreport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractBuild;
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

	private static final String URL_NAME = "jmh-report";

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
		return URL_NAME;
	}

	public AbstractProject<?, ?> getProject() {
		return _project;
	}

	public String getContextPath() {
		return Stapler.getCurrentRequest().getContextPath();
	}

	public String getProvidedJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		AbstractBuild<?, ?> lastSuccessfulBuild = getProject().getLastSuccessfulBuild();
		String providedId = lastSuccessfulBuild == null ? "none" : Integer.toString(lastSuccessfulBuild.getNumber());
		return new StringBuilder(contextPath).append("/job/").append(getProject().getName()).append('/')
				.append(URL_NAME).append("/provided-").append(providedId).append(".js").toString();
	}

	public void doDynamic(final StaplerRequest request, final StaplerResponse response)
			throws IOException, ServletException {
		ProvidedJsBuilder jsBuilder = new ProvidedJsBuilder();
		int addedReports = 0;
		for (Run run : _project.getBuilds()) {
			if (run.getResult() == Result.SUCCESS || run.getResult() == Result.UNSTABLE) {
				File reportFile = new File(run.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
				if (reportFile.exists()) {
					jsBuilder.addRun(Integer.toString(run.getNumber()), reportFile);
					addedReports++;
					if (addedReports == 2) {
						break;
					}
				}
			}
		}
		response.setContentType("text/javascript;charset=UTF-8");
		response.getWriter().println(jsBuilder.buildReverse());
	}

}

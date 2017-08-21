package io.morethan.jenkins.jmhreport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;

/**
 * The {@link Action} responsible for displaying the JMH report page for a
 * certain run.
 * 
 * <p>
 * See corresponding Jelly files under src/main/resources.
 * </p>
 */
public class RunJmhView implements Action, LastBuildAction, Serializable {

	private static final String URL_NAME = "jmh-run-report";

	private static final long serialVersionUID = 1L;

	private final Run<?, ?> _run;

	public RunJmhView(Run<?, ?> run) {
		_run = run;
	}

	/**
	 * The three functions
	 * {@link #getIconFileName()},{@link #getDisplayName()},{@link #getUrlName()}
	 * creating a link to a new page with url : http://{root}/job/{job
	 * name}/{irlName} for the page of the build.
	 */
	@Override
	public String getIconFileName() {
		return Constants.ICON_NAME;
	}

	@Override
	public String getDisplayName() {
		return "JMH Run Report";
	}

	@Override
	public String getUrlName() {
		return URL_NAME;
	}

	public Run<?, ?> getRun() {
		return _run;
	}

	public String getProjectName() {
		return _run.getParent().getName();
	}

	public int getBuildNumber() {
		return _run.getNumber();
	}

	public String getContextPath() {
		return Stapler.getCurrentRequest().getContextPath();
	}

	public String getProvidedJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		return new StringBuilder(contextPath).append("/").append(getRun().getUrl()).append(URL_NAME).append("/provided-").append(getBuildNumber())
				.append(".js").toString();
	}

	public String getBundleJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		return new StringBuilder(contextPath).append(Constants.PLUGIN_PATH).append("/bundle.js").toString();
	}

	public void doDynamic(final StaplerRequest request, final StaplerResponse response)
			throws IOException, ServletException {
		ProvidedJsBuilder jsBuilder = new ProvidedJsBuilder();
		File resultFile = new File(_run.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
		jsBuilder.addRun(getBuildNumber(), resultFile);

		Run<?, ?> previousSuccessfulBuild = _run.getPreviousNotFailedBuild();
		if (previousSuccessfulBuild != null) {
			File previousResultFile = new File(previousSuccessfulBuild.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
			if (previousResultFile.exists()) {
				jsBuilder.addRun(previousSuccessfulBuild.getNumber(), previousResultFile);
			}
		}

		response.setContentType("text/javascript;charset=UTF-8");
		response.getWriter().println(jsBuilder.buildReverse());
	}

	@Override
	public Collection<? extends Action> getProjectActions() {
		return Arrays.asList(new ProjectJmhView(_run.getParent()));
	}

}

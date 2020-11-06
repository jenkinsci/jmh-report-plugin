package io.morethan.jenkins.jmhreport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javax.servlet.ServletException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep.LastBuildAction;

/**
 * The {@link Action} responsible for displaying the JMH report page for a certain run.
 * 
 * <p>
 * See corresponding Jelly files under src/main/resources.
 * </p>
 */
public class RunJmhView implements Action, LastBuildAction, Serializable {

	static final String URL_NAME = "jmh-run-report";

	private static final long serialVersionUID = 1L;

	private final Run<?, ?> _run;
	private final String _reportName;

	public RunJmhView(Run<?, ?> run, String reportName) {
		_run = run;
		_reportName = reportName;
	}

	/**
	 * The three functions {@link #getIconFileName()},{@link #getDisplayName()},{@link #getUrlName()} creating a link to a new page with url :
	 * http://{root}/job/{job name}/{irlName} for the page of the build.
	 */
	@Override
	public String getIconFileName() {
		return Constants.ICON_NAME;
	}

	@Override
	public String getDisplayName() {
		return Archive.runDisplayName(_reportName);
	}

	@Override
	public String getUrlName() {
		return Archive.runUrl(_reportName);
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
		return new StringBuilder(contextPath).append("/").append(getRun().getUrl()).append(getUrlName())
				.append("/provided-").append(getBuildNumber()).append(".js").toString();
	}

	public String getBundleJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		return new StringBuilder(contextPath).append(Constants.PLUGIN_PATH).append("/bundle.js").toString();
	}

	public void doDynamic(final StaplerRequest request, final StaplerResponse response)
			throws IOException, ServletException {
		ProvidedJsBuilder jsBuilder = new ProvidedJsBuilder();
		File resultFile = new File(_run.getRootDir(), Archive.resultFileName(_reportName));
		jsBuilder.addRun(getBuildNumber(), resultFile);

		addPossiblePreviousBuild(jsBuilder, _run, _reportName);

		response.setContentType("text/javascript;charset=UTF-8");
		response.getWriter().println(jsBuilder.buildReverse());
	}

	private static void addPossiblePreviousBuild(ProvidedJsBuilder jsBuilder, Run<?, ?> run, String reportName) {
	    int runCount = 0;
		while ((run = run.getPreviousNotFailedBuild()) != null && runCount++ < 10) {
			File previousResultFile = new File(run.getRootDir(), Archive.resultFileName(reportName));
			if (previousResultFile.exists()) {
				jsBuilder.addRun(run.getNumber(), previousResultFile);
			}
		}
	}

	@Override
	public Collection<? extends Action> getProjectActions() {
		return new ArrayList<>();
	}
}

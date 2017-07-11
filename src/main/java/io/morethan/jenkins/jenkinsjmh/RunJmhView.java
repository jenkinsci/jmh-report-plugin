package io.morethan.jenkins.jenkinsjmh;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.model.AbstractBuild;
import hudson.model.Action;

/**
 * The {@link Action} responsible for displaying the JMH report page for a
 * certain run.
 * 
 * <p>
 * See corresponding Jelly files under src/main/resources.
 * </p>
 */
public class RunJmhView implements Action, Serializable {

	private static final long serialVersionUID = 1L;

	private final AbstractBuild<?, ?> _run;

	public RunJmhView(AbstractBuild<?, ?> run) {
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
		return "jmh-run-report";
	}

	public AbstractBuild<?, ?> getRun() {
		return _run;
	}

	public String getProjectName() {
		return _run.getProject().getName();
	}

	public int getBuildNumber() {
		return _run.getNumber();
	}

	// TODO add increase/decrease to summary
	// TODO add number of benchmarks, added, removed to summary

	public String getProvidedJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		return new StringBuilder(contextPath).append("/job/").append(getProjectName()).append('/')
				.append(getBuildNumber()).append("/jmh-run-report/provided-").append(getBuildNumber()).append(".js")
				.toString();
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

		AbstractBuild<?, ?> previousSuccessfulBuild = _run.getPreviousSuccessfulBuild();
		if (previousSuccessfulBuild != null) {
			File previousResultFile = new File(previousSuccessfulBuild.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
			if (previousResultFile.exists()) {
				jsBuilder.addRun(previousSuccessfulBuild.getNumber(), previousResultFile);
			}
		}

		response.setContentType("text/javascript;charset=UTF-8");
		response.getWriter().println(jsBuilder.buildReverse());
	}

}

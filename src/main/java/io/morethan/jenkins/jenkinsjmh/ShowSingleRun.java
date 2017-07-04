package io.morethan.jenkins.jenkinsjmh;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import hudson.model.*;

public class ShowSingleRun implements Action, Serializable {

	
	private static final long serialVersionUID = 1L;

	protected static final String ICON_NAME = Constants.PLUGIN_PATH + "/icon.png";

	private final AbstractBuild<?, ?> _run;

	public ShowSingleRun(AbstractBuild<?, ?> run) {
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
		return ICON_NAME;
	}

	@Override
	public String getDisplayName() {
		return "JMH Report";
	}

	@Override
	public String getUrlName() {
		return "jmh-report";
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
				.append(getBuildNumber()).append("/jmh-report/provided-").append(getBuildNumber()).append(".js")
				.toString();
	}

	public String getBundleJsUrl() {
		String contextPath = Stapler.getCurrentRequest().getContextPath();
		return new StringBuilder(contextPath).append(Constants.PLUGIN_PATH).append("/bundle.js").toString();
	}

	public void doDynamic(final StaplerRequest request, final StaplerResponse response)
			throws IOException, ServletException {
		File resultFile = new File(_run.getRootDir(), Constants.ARCHIVED_RESULT_FILE);

		AbstractBuild<?, ?> previousSuccessfulBuild = _run.getPreviousSuccessfulBuild();
		File previousResultFile = null;
		boolean havePreviousResult = false;
		if (previousSuccessfulBuild != null) {
			previousResultFile = new File(previousSuccessfulBuild.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
			havePreviousResult = previousResultFile.exists();
		}

		final StringBuilder builder = new StringBuilder();
		if (havePreviousResult) {
			builder.append("var providedBenchmarks = [" + (previousSuccessfulBuild.getNumber()) + "," + getBuildNumber()
					+ "];");
		} else {
			builder.append("var providedBenchmarks = [" + getBuildNumber() + "];");
		}
		builder.append("\n");
		builder.append("var providedBenchmarkStore = {");
		builder.append("\n");
		if (havePreviousResult) {
			builder.append((previousSuccessfulBuild.getNumber()) + ": ");
			Files.readLines(previousResultFile, Charsets.UTF_8, new LineProcessor<Void>() {
				@Override
				public boolean processLine(String line) throws IOException {
					builder.append(line);
					return true;
				}

				@Override
				public Void getResult() {
					return null;
				}
			});
			builder.append(",\n");
		}

		builder.append(getBuildNumber() + ": ");
		Files.readLines(resultFile, Charsets.UTF_8, new LineProcessor<Void>() {
			@Override
			public boolean processLine(String line) throws IOException {
				builder.append(line);
				return true;
			}

			@Override
			public Void getResult() {
				return null;
			}
		});
		builder.append("\n");
		builder.append("}");
		builder.append("\n");
		response.setContentType("text/javascript;charset=UTF-8");
		response.getWriter().println(builder.toString());
	}

}

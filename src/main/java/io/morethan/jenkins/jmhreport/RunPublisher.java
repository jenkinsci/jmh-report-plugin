package io.morethan.jenkins.jmhreport;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.File;
import java.io.IOException;

/**
 * A {@link Recorder} executed after each build. It copies the JMH result file
 * into the corresponding build dir.
 */
public class RunPublisher extends Recorder implements SimpleBuildStep {

	private String _resultPath;

	private String _reportName;

	public String getResultPath() {
		return _resultPath;
	}

	@DataBoundConstructor
	public RunPublisher() {
	}

	@DataBoundSetter
	public void setResultPath(String resultPath) {
		_resultPath = resultPath;
	}

	public String getReportName() {
		return _reportName;
	}

	@DataBoundSetter
	public void setReportName(String reportName) {
		_reportName = reportName;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
			throws InterruptedException, IOException {
		if (run.getResult() == Result.ABORTED || run.getResult() == Result.FAILURE
				|| run.getResult() == Result.NOT_BUILT) {
			listener.getLogger().println("Skipping JMH-Report...");
			return;
		}
		listener.getLogger().println("Executing JMH-Report...");

		// Lookup the JMH result file in the workspace
		FilePath resultFile = workspace.child(_resultPath);
		if (!resultFile.exists()) {
			throw new AbortException("Could not find JMH result at: " + _resultPath);
		}
		listener.getLogger().println("Found JMH result: " + _resultPath);

		// Copy the result file into the build dir of the Jenkins project
		File archivedResult = new File(run.getRootDir(), Archive.resultFileName(_reportName));
		resultFile.copyTo(new FilePath(archivedResult));
		listener.getLogger().println("Archived JMH result to: " + archivedResult);

		run.addAction(new RunJmhView(run, getReportName()));
		// TODO set on major decreases ?
		// build.setResult(Result.UNSTABLE);
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Symbol("jmhReport")
	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		@SuppressWarnings("rawtypes")
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "JMH Report";
		}

	}

}

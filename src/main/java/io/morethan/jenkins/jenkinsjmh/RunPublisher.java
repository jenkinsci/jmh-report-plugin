package io.morethan.jenkins.jenkinsjmh;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

public class RunPublisher extends Recorder {

	private final String _resultPath;

	@DataBoundConstructor
	public RunPublisher(String resultPath) {
		_resultPath = resultPath;
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> run, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {

		if (run.getResult() == Result.SUCCESS || run.getResult() == Result.UNSTABLE) {
			FilePath resultFile = run.getWorkspace().child(_resultPath);
			if (!resultFile.exists()) {
				listener.error("Could not find JMH result at: " + _resultPath);
				return false;
			}

			listener.getLogger().println("Found JMH result: " + _resultPath);

			// Storing the result file in the run dir
			File archivedResult = new File(run.getRootDir(), Constants.ARCHIVED_RESULT_FILE);
			resultFile.copyTo(new FilePath(archivedResult));
			listener.getLogger().println("Archived JMH result to: " + archivedResult);
			// TODO use this one
			// _run.getArtifactManager().

			run.addAction(new ShowSingleRun(run));
			// TODO set on major decreases ?
			// build.setResult(Result.UNSTABLE);
		}

		return true;

	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Override
	public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
		// TODO Auto-generated method stub
		return super.getProjectActions(project);
	}

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

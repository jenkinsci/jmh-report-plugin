package io.morethan.jmh.jenkport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

/**
 * Builds the provided.js for JMH Visualizer.
 */
public class ProvidedJsBuilder {

	private static final char NEWLINE = '\n';
	private final List<String> _runNames = new ArrayList<>();
	private final List<File> _reportFiles = new ArrayList<>();

	public ProvidedJsBuilder addRun(Number number, File reportFile) {
		return addRun(number.toString(), reportFile);
	}

	public ProvidedJsBuilder addRun(String name, File reportFile) {
		if (_runNames.size() >= 2) {
			throw new UnsupportedOperationException("Report on more then 2 file is not (yet) supported!");
		}
		_runNames.add(name);
		_reportFiles.add(reportFile);
		return this;
	}

	// TODO think the logic in jmh-visualizer should change.. to newest first
	/**
	 * Same as {@link #build()} but with the reports in reverse order. Helpful in
	 * case you filled the builder with the newest entry first.
	 * 
	 * @return provided.js as String
	 */
	public String buildReverse() {
		return build(ImmutableList.copyOf(_runNames).reverse(), ImmutableList.copyOf(_reportFiles).reverse());
	}

	/**
	 * Build the provided.js out of the given reports.
	 * 
	 * @return provided.js as String
	 */
	public String build() {
		return build(_runNames, _reportFiles);
	}

	private static String build(List<String> runNames, List<File> reportFiles) {
		final StringBuilder builder = new StringBuilder();
		builder.append("var providedBenchmarks = [").append(NEWLINE);
		for (String run : runNames) {
			builder.append("  '").append(run).append("',");
			builder.append(NEWLINE);
		}
		if (runNames.size() > 0) {
			deleteLastChars(builder, 2);
		}
		builder.append(NEWLINE);
		builder.append("];");
		builder.append(NEWLINE);

		builder.append("var providedBenchmarkStore = {").append(NEWLINE);
		for (int i = 0; i < runNames.size(); i++) {
			builder.append("  ").append(runNames.get(i)).append(": ");
			appendFile(builder, reportFiles.get(i));
			deleteLastChars(builder, 1);
			builder.append(",").append(NEWLINE);
		}
		if (runNames.size() > 0) {
			deleteLastChars(builder, 2);
		}

		builder.append(NEWLINE).append("}");
		return builder.toString();
	}

	private static void deleteLastChars(final StringBuilder builder, int numberOfCharacters) {
		for (int i = 0; i < numberOfCharacters; i++) {
			builder.deleteCharAt(builder.length() - 1);
		}
	}

	private static void appendFile(final StringBuilder builder, File file) {
		try {
			Files.readLines(file, Charsets.UTF_8, new LineProcessor<Void>() {
				@Override
				public boolean processLine(String line) throws IOException {
					if (!Strings.isNullOrEmpty(line)) {
						builder.append(line);
						builder.append(NEWLINE);
					}
					return true;
				}

				@Override
				public Void getResult() {
					return null;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException("Failed to load file " + file.getAbsolutePath());
		}
	}

	// TODO write junit test
	public static void main(String[] args) {
		String providedJs = new ProvidedJsBuilder().addRun("1", new File("/Users/jz/Desktop/gcExample.json"))
				.addRun("2", new File("/Users/jz/Desktop/gcExample.json")).build();
		System.out.println(providedJs);
	}
}

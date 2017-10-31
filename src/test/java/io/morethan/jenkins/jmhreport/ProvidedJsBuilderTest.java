package io.morethan.jenkins.jmhreport;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

public class ProvidedJsBuilderTest {

	@Rule
	public TemporaryFolder _tempFolder = new TemporaryFolder();

	@Test
	public void testEmpty() throws URISyntaxException, IOException {
		String providedJs = new ProvidedJsBuilder().build();
		assertEquals("var providedBenchmarks = [\n" + "\n" + "];\n" + "var providedBenchmarkStore = {\n" + "\n" + "}",
				providedJs);
	}

	@Test
	public void test() throws URISyntaxException, IOException {
		File file1 = _tempFolder.newFile("file1.json");
		File file2 = _tempFolder.newFile("file2.json");
		Files.write("[{benchmark1}]", file1, Charset.defaultCharset());
		Files.write("[{benchmark2}]", file2, Charset.defaultCharset());

		// File file1 = new File(getClass().getResource("/run1.json").toURI());
		// File file2 = new File(getClass().getResource("/run2.json").toURI());
		ProvidedJsBuilder builder = new ProvidedJsBuilder().addRun("1", file1).addRun("2", file2);
		// System.out.println(builder.build());
		assertEquals("var providedBenchmarks = [\n" + "  '1',\n" + "  '2'\n" + "];\n"
				+ "var providedBenchmarkStore = {\n" + "  '1': [{benchmark1}],\n" + "  '2': [{benchmark2}]\n" + "}",
				builder.build());

		// now reverse
		// System.out.println("\nreverse:\n" + builder.buildReverse());
		assertEquals("var providedBenchmarks = [\n" + "  '2',\n" + "  '1'\n" + "];\n"
				+ "var providedBenchmarkStore = {\n" + "  '2': [{benchmark2}],\n" + "  '1': [{benchmark1}]\n" + "}",
				builder.buildReverse());
	}

}

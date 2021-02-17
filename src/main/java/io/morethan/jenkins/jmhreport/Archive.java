package io.morethan.jenkins.jmhreport;

public final class Archive {

    static String resultFileName(String archiveName) {
        return String.format("%s-%s", archiveName, Constants.ARCHIVED_RESULT_FILE);
    }

    static String runUrl(String reportName) {
        return String.format("%s-%s", RunJmhView.URL_NAME, reportName);
    }

    static String runDisplayName(String reportName) {
        return String.format("%s - %s", "JMH Run Report", reportName);
    }
}

package ru.md.domain;

/**
 * Шаблон отчёта
 * @author Andrey Pavlenko
 */
public class ReportTemplate {
	private String templateName;
	private String filename;
	private Boolean reportingEngine;
	private byte[] docPattern;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getDocPattern() {
		return docPattern;
	}

	public void setDocPattern(byte[] docPattern) {
		this.docPattern = docPattern;
	}

	public Boolean getReportingEngine() {
		return (reportingEngine == null) ? false : reportingEngine;
	}

	public void setReportingEngine(Boolean reportingEngine) {
		this.reportingEngine = (reportingEngine == null) ? false : reportingEngine;
	}
}

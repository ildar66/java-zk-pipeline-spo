package ru.md.report;

import ru.masterdm.reportsystem.annotation.ReportMark;

import java.util.List;

/**
 * Created by Andrey Pavlenko on 03.06.2016.
 */
public class TestAsposeReportData extends AsposeReportData {
    private List<TestRow> list;

    @ReportMark(name = "Заголовок")
    private String testValue;
    
    public TestAsposeReportData() {
    	testValue = "default value";
    }

    @ReportMark(name = "list.", complex = true, collection = true)
    public List<TestRow> getList() {
        return list;
    }

    public void setList(List<TestRow> list) {
        this.list = list;
    }

	public String getTestValue() {
		return testValue;
	}

	public void setTestValue(String testValue) {
		this.testValue = testValue;
	}    
}

package ru.md.report;

import ru.masterdm.reportsystem.annotation.ReportMark;

/**
 * Created by Andrey Pavlenko on 03.06.2016.
 */
public class TestRow {
    @ReportMark(name = "val1")
    private Long val1;
    @ReportMark(name = "val2")
    private Long val2;
    @ReportMark(name = "name")
    private String name;

    public Long getVal1() {
        return val1;
    }

    public void setVal1(Long val1) {
        this.val1 = val1;
    }

    public Long getVal2() {
        return val2;
    }

    public void setVal2(Long val2) {
        this.val2 = val2;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TestRow(Long val1, Long val2, String name) {
        this.val1 = val1;
        this.val2 = val2;
        this.name = name;
    }
}

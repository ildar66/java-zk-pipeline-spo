package org.uit.director.report;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.uit.director.contexts.WorkflowSessionContext;


public abstract class WorkflowReport implements Serializable{

    protected boolean isReportGenerate = false;
    protected List<ComponentReport> componentList = null;
    private WorkflowSessionContext wsc;
    protected String nameReport = "Стандартный отчет";
    protected String reportHTML;
    protected List params;


    public List getComponentList() {
        return componentList;

    }

    public void generateReport() throws SQLException {
    }

    public void init(WorkflowSessionContext wsc, List params) {
        this.wsc = wsc;
        this.params = params;
        reportHTML = "<center><big>" + nameReport + "</big></center><br>";
    }


    public boolean isReportGenerate() {
        return isReportGenerate;
    }

    public WorkflowSessionContext getWsc() {
        return wsc;
    }

    public String getNameReport() {
        return nameReport;
    }

    public String getReportHTML() {
        return reportHTML;
    }


}

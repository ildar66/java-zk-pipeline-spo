package org.uit.director.db.dbobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: PD190390
 * Date: 17.03.2006
 * Time: 11:12:41
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowTypeProcessGroup  implements Serializable  {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList typesProcesses;
    private String commonName;
    private ArrayList difference;

    public WorkflowTypeProcessGroup(ArrayList typesProcesses, String commonName, ArrayList difference) {
        this.typesProcesses = typesProcesses;
        this.commonName = commonName;
        this.difference = difference;
    }

    public List getTypesProcesses() {
        return typesProcesses;
    }

    public void setTypesProcesses(ArrayList typesProcesses) {
        this.typesProcesses = typesProcesses;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public List getDifference() {
        return difference;
    }

    public void setDifference(ArrayList difference) {
        this.difference = difference;
    }

}

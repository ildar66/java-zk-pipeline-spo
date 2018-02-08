package org.uit.director.db.dbobjects;



public class WorkflowProcessParameters extends WorkflowObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value;

    public WorkflowProcessParameters(long id, String name, String value) {
        super(id, name);
        this.value = value;
    }

    public int getIdTypeProcess() {
        return (int)id.longValue();
    }

    public String getNameParameter() {
        return name;
    }

    public String getValue() {
        return value;
    }
    
    

    /**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Object getData(String field) {

        if (field.equals(Cnst.TProcPar.id)) {
			return new Long (id);
		}
        if (field.equals(Cnst.TProcPar.name)) {
			return name;
		}
        if (field.equals(Cnst.TProcPar.value)) {
			return value;
		}

        return null;
    }
    
    
}

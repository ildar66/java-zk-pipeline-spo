package org.uit.director.db.dbobjects;



public class WorkflowTypeProcess extends WorkflowObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int limitDay;
    String schema;
    String schemaImage;
    int idGroup;

    public WorkflowTypeProcess(int id, String name, int limiDay, String schema, String schemaImage, int idGroup) {
        super((long)id, name);
        limitDay = limiDay;
        this.schema = schema;
        this.schemaImage = schemaImage;        
        this.idGroup = idGroup;
    }

    public int getLimitDay() {
        return limitDay;
    }

    public void setLimitDay(int limitDay) {
        this.limitDay = limitDay;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getIdTypeProcess() {
        return (int)id.longValue();
    }

    public String getNameTypeProcess() {
        return name;
    }

    
    /**
	 * @param schemaImage the schemaImage to set
	 */
	public void setSchemaImage(String schemaImage) {
		this.schemaImage = schemaImage;
	}

	@Override
	public Object getData(String field) {

        if (field.equals(Cnst.TTypeProc.id)) {
			return id;
		}
        if (field.equals(Cnst.TTypeProc.limitDay)) {
			return limitDay;
		}
        if (field.equals(Cnst.TTypeProc.name)) {
			return name;
		}
        if (field.equals(Cnst.TTypeProc.schema)) {
			return schema;
		}
        if (field.equals(Cnst.TTypeProc.idGroup)) {
			return idGroup;
		}

        return null;

    }

    @Override
	public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}
        if (!super.equals(o)) {
			return false;
		}

        final WorkflowTypeProcess that = (WorkflowTypeProcess) o;

        if (limitDay != that.limitDay) {
			return false;
		}
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) {
			return false;
		}       

        return super.equals(o);
    }

    @Override
	public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + limitDay;
        result = 29 * result + (schema != null ? schema.hashCode() : 0);
        return result;
    }

	public String getSchemaImage() {
		return schemaImage;
	}
	
	
}

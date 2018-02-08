package org.uit.director.db.dbobjects;

import java.io.Serializable;


/**
 * Created by IntelliJ IDEA.
 * User: pd190390
 * Date: 26.12.2005
 * Time: 9:55:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class WorkflowObject implements Serializable {

    Long id;
    String name;

    protected WorkflowObject(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Object getData(String field);

    @Override
	public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}

        final WorkflowObject that = (WorkflowObject) o;

        if (id != that.id) {
			return false;
		}
        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
	public int hashCode() {
        int result;
        result = (int) (id ^ (id >>> 32));
        result = 29 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}

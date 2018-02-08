package org.uit.director.db.dbobjects;

import java.io.Serializable;

public class BasicAttribute implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    Attribute attribute;

    public BasicAttribute() {
        super();

    }

    public BasicAttribute(Attribute attribute) {
        super();
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return attribute == null ? null : attribute.toString();
    }

}

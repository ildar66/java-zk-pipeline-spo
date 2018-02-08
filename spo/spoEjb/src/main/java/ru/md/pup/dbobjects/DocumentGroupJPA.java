package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
/**
 * справочник групп документов.
 * @author Andrey Pavlenko
 *
 */
@Entity @Table(name = "DOCUMENT_GROUP")
public class DocumentGroupJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id @Column(name="ID_GROUP")
    private Long id;
    private String NAME_DOCUMENT_GROUP;
    private Long GROUP_TYPE;
    private Long systems;
    private Long is_active;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "r_document_group", joinColumns = @JoinColumn(name = "ID_DOCUMENT_GROUP"), 
            inverseJoinColumns = @JoinColumn(name = "ID_DOCUMENT_TYPE"))
    @OrderBy(value = "name")
    private List<DocumentTypeJPA> types;

    
    public DocumentGroupJPA() {
        super();
    }
    
    public DocumentGroupJPA(Long id) {
        super();
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNAME_DOCUMENT_GROUP() {
    	return NAME_DOCUMENT_GROUP;
    }
    public String getRestrictedNameDocumentGroup() {
    	String name = NAME_DOCUMENT_GROUP;
    	if(name.length()>80)
    		return name.substring(0, 77) + "...";
        return NAME_DOCUMENT_GROUP;
    }
    public void setNAME_DOCUMENT_GROUP(String nAME_DOCUMENT_GROUP) {
        NAME_DOCUMENT_GROUP = nAME_DOCUMENT_GROUP;
    }
    public Long getGROUP_TYPE() {
        return GROUP_TYPE;
    }
    public void setGROUP_TYPE(Long gROUP_TYPE) {
        GROUP_TYPE = gROUP_TYPE;
    }
    public List<DocumentTypeJPA> getTypes() {
        return types;
    }
    public void setTypes(List<DocumentTypeJPA> types) {
        this.types = types;
    }

	public Long getSystems() {
		return systems;
	}

	public void setSystems(Long systems) {
		this.systems = systems;
	}

	public boolean isActive() {
		return is_active==null || is_active.equals(1L);
	}

	public void setIs_active(Long is_active) {
		this.is_active = is_active;
	}
    
}

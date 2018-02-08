package ru.md.pup.dbobjects;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * справочник типов документов.
 * @author Andrey Pavlenko
 *
 */
@Entity @Table(name = "documents_type")
public class DocumentTypeJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id @Column(name="ID_DOCUMENT_TYPE")
    private Long id;
    @Column(name="NAME_DOCUMENT_TYPE")
    private String name;;
    private String forcc;
    private Long is_active;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
    	return name;
    }
    public String getRestrictedName() {
    	if(name.length()>80)
    		return name.substring(0, 77) + "...";
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getForcc() {
        return forcc;
    }
    public void setForcc(String forcc) {
        this.forcc = forcc;
    }
    public DocumentTypeJPA() {
        super();
    }
    public DocumentTypeJPA(Long id) {
        super();
        this.id = id;
    }
	public boolean isActive() {
		return is_active==null || is_active.equals(1L);
	}

	public void setIs_active(Long is_active) {
		this.is_active = is_active;
	}
    
}

package ru.md.pup.dbobjects;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Прикрепленный документ.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "appfiles")
public class AttachDataJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String unid;
    
    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] filedata;

    @Override
    public String toString() {
        return "AttachDataJPA [unid=" + unid + "]";
    }

    public String getUnid() {
        return unid;
    }

    public void setUnid(String unid) {
        this.unid = unid;
    }

	public byte[] getFiledata() {
		return filedata;
	}

	public void setFiledata(byte[] filedata) {
		this.filedata = filedata;
	}

   
}

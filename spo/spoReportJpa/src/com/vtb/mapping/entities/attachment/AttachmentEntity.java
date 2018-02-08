package com.vtb.mapping.entities.attachment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "APPFILES")
public class AttachmentEntity implements Serializable {
	@Id
	private String unid;

	private String filename;

	@Lob
	private byte[] filedata;

	private String filetype;

	@Column(name="ID_OWNER")
	private String idOwner;

	@Column(name="OWNER_TYPE")
	private BigDecimal ownerType;

	@Column(name="WHO_ADD")
	private BigDecimal whoAdd;

	@Column(name="DATE_OF_ADDITION")
	private Date dateOfAddition;

	@Column(name="DATE_OF_EXPIRATION")
	private Date dateOfExpiration;

	private BigDecimal isaccepted;

	private BigDecimal whoaccepted;

	@Column(name="DATE_OF_ACCEPT")
	private Date dateOfAccept;

	@Lob
	private byte[] signature;

	@Column(name="ID_APPL")
	private BigDecimal idAppl;

	@Column(name="ID_GROUP")
	private BigDecimal idGroup;

    @Column(name="ID_DOCUMENT_TYPE")
    private Long idType;

	private String forcc;

	private String contenttype;

	private static final long serialVersionUID = 1L;

	public AttachmentEntity() {
		super();
	}

	public String getUnid() {
		return this.unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getFiledata() {
		return this.filedata;
	}

	public void setFiledata(byte[] filedata) {
		this.filedata = filedata;
	}

	public String getFiletype() {
		return this.filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getIdOwner() {
		return this.idOwner;
	}

	public void setIdOwner(String idOwner) {
		this.idOwner = idOwner;
	}

	public BigDecimal getOwnerType() {
		return this.ownerType;
	}

	public void setOwnerType(BigDecimal ownerType) {
		this.ownerType = ownerType;
	}

	public BigDecimal getWhoAdd() {
		return this.whoAdd;
	}

	public void setWhoAdd(BigDecimal whoAdd) {
		this.whoAdd = whoAdd;
	}

	public Date getDateOfAddition() {
		return this.dateOfAddition;
	}

	public void setDateOfAddition(Date dateOfAddition) {
		this.dateOfAddition = dateOfAddition;
	}

	public Date getDateOfExpiration() {
		return this.dateOfExpiration;
	}

	public void setDateOfExpiration(Date dateOfExpiration) {
		this.dateOfExpiration = dateOfExpiration;
	}

	public BigDecimal getIsaccepted() {
		return this.isaccepted;
	}

	public void setIsaccepted(BigDecimal isaccepted) {
		this.isaccepted = isaccepted;
	}

	public BigDecimal getWhoaccepted() {
		return this.whoaccepted;
	}

	public void setWhoaccepted(BigDecimal whoaccepted) {
		this.whoaccepted = whoaccepted;
	}

	public Date getDateOfAccept() {
		return this.dateOfAccept;
	}

	public void setDateOfAccept(Date dateOfAccept) {
		this.dateOfAccept = dateOfAccept;
	}

	public byte[] getSignature() {
		return this.signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public BigDecimal getIdAppl() {
		return this.idAppl;
	}

	public void setIdAppl(BigDecimal idAppl) {
		this.idAppl = idAppl;
	}

	public BigDecimal getIdGroup() {
		return this.idGroup;
	}

	public void setIdGroup(BigDecimal idGroup) {
		this.idGroup = idGroup;
	}

	public String getForcc() {
		return this.forcc;
	}

	public void setForcc(String forcc) {
		this.forcc = forcc;
	}

	public String getContenttype() {
		return this.contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }
}

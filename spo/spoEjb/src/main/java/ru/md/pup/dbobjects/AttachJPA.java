package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vtb.util.Formatter;

/**
 * Прикрепленный документ.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "appfiles")
public class AttachJPA implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String unid;
    
    private Long ISACCEPTED;
    @ManyToOne @JoinColumn(name = "WHOACCEPTED")
    private UserJPA whoAccepted;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date DATE_OF_ACCEPT;
    @Temporal(TemporalType.TIMESTAMP)
    private Date DATE_OF_ADDITION;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date_of_sign;
    @Temporal(TemporalType.TIMESTAMP)
    private Date DATE_OF_EXPIRATION;
    
    private String FILEURL;
    private String title;
    private String FILENAME;
    private String FILETYPE;
    private String ID_OWNER;
    private String kz_backup;
    private Long OWNER_TYPE;
    @ManyToOne @JoinColumn(name = "WHO_ADD")
    private UserJPA whoAdd;
    @ManyToOne @JoinColumn(name = "who_sign")
    private UserJPA whoSign;
    private String FORCC;
    private String CONTENTTYPE;
    private String reason;

    @ManyToOne @JoinColumn(name = "ID_GROUP")
    private DocumentGroupJPA group;
    @ManyToOne @JoinColumn(name = "ID_DOCUMENT_TYPE")
    private DocumentTypeJPA documentType;
    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] SIGNATURE;
    @Lob @Basic(fetch = FetchType.LAZY)
    private byte[] ACCEPT_SIGNATURE;

    @ManyToOne @JoinColumn(name = "WHO_DEL")
    private UserJPA whoDel;
    @Temporal(TemporalType.TIMESTAMP)
    private Date DATE_OF_DEL;
    
    @Override
    public String toString() {
        return "AttachJPA [FILENAME=" + FILENAME + "]";
    }

    public String getUnid() {
        return unid;
    }

    public void setUnid(String unid) {
        this.unid = unid;
    }

    public Long getISACCEPTED() {
        return ISACCEPTED;
    }

    public void setISACCEPTED(Long iSACCEPTED) {
        ISACCEPTED = iSACCEPTED;
    }

    public UserJPA getWhoAccepted() {
        return whoAccepted;
    }

    public void setWhoAccepted(UserJPA whoAccepted) {
        this.whoAccepted = whoAccepted;
    }

    public UserJPA getWhoAdd() {
        return whoAdd;
    }

    public void setWhoAdd(UserJPA whoAdd) {
        this.whoAdd = whoAdd;
    }

    public Date getDATE_OF_ACCEPT() {
        return DATE_OF_ACCEPT;
    }

    public void setDATE_OF_ACCEPT(Date dATE_OF_ACCEPT) {
        DATE_OF_ACCEPT = dATE_OF_ACCEPT;
    }

    public Date getDATE_OF_ADDITION() {
        return DATE_OF_ADDITION;
    }

    public void setDATE_OF_ADDITION(Date dATE_OF_ADDITION) {
        DATE_OF_ADDITION = dATE_OF_ADDITION;
    }

    public Date getDATE_OF_EXPIRATION() {
    	return DATE_OF_EXPIRATION;
    }
    public String getFormatedDateOfExpiration() {
        return Formatter.format(DATE_OF_EXPIRATION);
    }
    public boolean isExpired(){
    	if(DATE_OF_EXPIRATION==null)
    		return false;
    	return DATE_OF_EXPIRATION.before(new Date());
    }

    public void setDATE_OF_EXPIRATION(Date dATE_OF_EXPIRATION) {
        DATE_OF_EXPIRATION = dATE_OF_EXPIRATION;
    }

    public String getFILENAME() {
        String name = FILENAME;
        if (name==null && FILEURL!=null) return FILEURL;
        if (name==null) return "";
        Pattern p = Pattern.compile(".*[\\\\/](.*?)");
        Matcher m = p.matcher(name);
        if(m.matches()) return m.group(1);
        return name;
    }

    /**
     * Это сгенерённый дубликат, который (с отметкой об ЭЦП)
     */
    public boolean isDuplicate() {
        return getFILENAME().contains("(с отметкой об ЭЦП)");
    }
    public boolean enable4ccButton() {
        return !getSIGNATURE().isEmpty() || isDuplicate();
    }
    public void setFILENAME(String fILENAME) {
        FILENAME = fILENAME;
    }

    public String getFILETYPE() {
        return FILETYPE==null?"":FILETYPE;
    }

    public void setFILETYPE(String fILETYPE) {
        FILETYPE = fILETYPE;
    }

    public String getID_OWNER() {
        return ID_OWNER;
    }

    public void setID_OWNER(String iD_OWNER) {
        ID_OWNER = iD_OWNER;
    }

    public Long getOWNER_TYPE() {
        return OWNER_TYPE;
    }

    public void setOWNER_TYPE(Long oWNER_TYPE) {
        OWNER_TYPE = oWNER_TYPE;
    }

    public String getFORCC() {
        return FORCC;
    }
    public boolean isFORCC() {
        if (FORCC==null) return false;
        return FORCC.equalsIgnoreCase("Y") && (!getSIGNATURE().isEmpty() || isDuplicate());
    }

    public void setFORCC(String fORCC) {
        FORCC = fORCC;
    }

    public String getCONTENTTYPE() {
        return CONTENTTYPE;
    }

    public void setCONTENTTYPE(String cONTENTTYPE) {
        CONTENTTYPE = cONTENTTYPE;
    }

    public DocumentGroupJPA getGroup() {
        return group;
    }

    public void setGroup(DocumentGroupJPA group) {
        this.group = group;
    }


    public DocumentTypeJPA getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentTypeJPA documentType) {
        this.documentType = documentType;
    }

    public String getSIGNATURE() {
        if (SIGNATURE==null || SIGNATURE.length==0) return "";
        return new String(SIGNATURE);
    }

    public void setSIGNATURE(byte[] sIGNATURE) {
        SIGNATURE = sIGNATURE;
    }

    public boolean isFILEURL() {
        return FILEURL!=null && FILEURL.length()>0;
    }
    public String getFILEURL() {
        if (FILEURL==null) return "download.do?unid="+this.unid;
        return FILEURL;
    }

    public void setFILEURL(String fILEURL) {
        FILEURL = fILEURL;
    }

	public UserJPA getWhoDel() {
		return whoDel;
	}

	public void setWhoDel(UserJPA whoDel) {
		this.whoDel = whoDel;
	}

	public Date getDATE_OF_DEL() {
		return DATE_OF_DEL;
	}

	public void setDATE_OF_DEL(Date dATE_OF_DEL) {
		DATE_OF_DEL = dATE_OF_DEL;
	}

	public String getACCEPT_SIGNATURE() {
		if (ACCEPT_SIGNATURE==null || ACCEPT_SIGNATURE.length==0) return "";
        return new String(ACCEPT_SIGNATURE);
	}

	public void setACCEPT_SIGNATURE(byte[] aCCEPT_SIGNATURE) {
		ACCEPT_SIGNATURE = aCCEPT_SIGNATURE;
	}

	public String getTitle() {
		return (title==null||title.isEmpty())?getFILENAME():title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    /**
     * Returns .
     * @return
     */
    public Date getDate_of_sign() {
        return date_of_sign;
    }

    /**
     * Sets .
     * @param date_of_sign
     */
    public void setDate_of_sign(Date date_of_sign) {
        this.date_of_sign = date_of_sign;
    }

    /**
     * Returns .
     * @return
     */
    public UserJPA getWhoSign() {
        return whoSign;
    }

    /**
     * Sets .
     * @param whoSign
     */
    public void setWhoSign(UserJPA whoSign) {
        this.whoSign = whoSign;
    }

    /**
     * Returns .
     * @return
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets .
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Returns .
     * @return
     */
    public String getKz_backup() {
        return kz_backup;
    }

    /**
     * Sets .
     * @param kz_backup
     */
    public void setKz_backup(String kz_backup) {
        this.kz_backup = kz_backup;
    }
}

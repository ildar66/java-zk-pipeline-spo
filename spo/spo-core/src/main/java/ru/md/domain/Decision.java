package ru.md.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrey Pavlenko on 31.08.2017.
 */
public class Decision {
    private String contenttype;
    private byte[] filedata;
    private List<String> decisionBody= new ArrayList<String>();
    private Date decisionDate;
    private String protocolNo;
    private String filename;
    private Long idDecision;

    /**
     * Returns .
     * @return
     */
    public String getContenttype() {
        return contenttype;
    }

    /**
     * Sets .
     * @param contenttype
     */
    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    /**
     * Returns .
     * @return
     */
    public byte[] getFiledata() {
        return filedata;
    }

    /**
     * Sets .
     * @param filedata
     */
    public void setFiledata(byte[] filedata) {
        this.filedata = filedata;
    }

    /**
     * Returns .
     * @return
     */
    public List<String> getDecisionBody() {
        return decisionBody;
    }

    /**
     * Sets .
     * @param decisionBody
     */
    public void setDecisionBody(List<String> decisionBody) {
        this.decisionBody = decisionBody;
    }

    /**
     * Returns .
     * @return
     */
    public Date getDecisionDate() {
        return decisionDate;
    }

    /**
     * Sets .
     * @param decisionDate
     */
    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    /**
     * Returns .
     * @return
     */
    public String getProtocolNo() {
        return protocolNo;
    }

    /**
     * Sets .
     * @param protocolNo
     */
    public void setProtocolNo(String protocolNo) {
        this.protocolNo = protocolNo;
    }

    /**
     * Returns .
     * @return
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets .
     * @param filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns .
     * @return
     */
    public Long getIdDecision() {
        return idDecision;
    }

    /**
     * Sets .
     * @param idDecision
     */
    public void setIdDecision(Long idDecision) {
        this.idDecision = idDecision;
    }
}

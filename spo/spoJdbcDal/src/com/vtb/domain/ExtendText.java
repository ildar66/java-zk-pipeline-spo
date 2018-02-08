package com.vtb.domain;

/**
 * Текстовые поля
 * @author Какунин К. Ю. (создано для jira VTBSPO-398)
 */
public class ExtendText extends VtbObject {
    private static final long serialVersionUID = 1L;
    private java.sql.Date approvedDate;
    private String approvedSignature;

    private String context;

    private java.sql.Date createDate;
    private String description;
    private long id;
    private Long idApprovedAuthor;
    private Long idAuthor;
    private String signature;
    private java.sql.Date validTo;

    public java.sql.Date getApprovedDate() {
        return approvedDate;
    }

    public String getApprovedSignature() {
        return approvedSignature;
    }

    public String getContext() {
        return context;
    }

    public java.sql.Date getCreateDate() {
        return createDate;
    }

    public String getDescriptionWithPrefix() {
        return "L_"+description;
    }

    public String getDescription() {
        return description;
    }
    public long getId() {
        return id;
    }

    public Long getIdApprovedAuthor() {
        return idApprovedAuthor;
    }

    public Long getIdAuthor() {
        return idAuthor;
    }

    public String getSignature() {
        return signature;
    }

    public java.sql.Date getValidTo() {
        return validTo;
    }

    public void setApprovedDate(java.sql.Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public void setApprovedSignature(String approvedSignature) {
        this.approvedSignature = approvedSignature;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setCreateDate(java.sql.Date createDate) {
        this.createDate = createDate;
    }

    public void setDescription(String description) {
        this.description = description.substring(2);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdApprovedAuthor(Long idApprovedAuthor) {
        this.idApprovedAuthor = idApprovedAuthor;
    }

    public void setIdAuthor(Long idAuthor) {
        this.idAuthor = idAuthor;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setValidTo(java.sql.Date validTo) {
        this.validTo = validTo;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("description").append(description);
        s.append(", idAuthor").append(idAuthor);
        s.append(", idApprovedAuthor").append(idApprovedAuthor);
        s.append(", createDate").append(createDate);
        s.append(", approvedDate").append(approvedDate);
        s.append(", validto").append(validTo);
        return s.toString();
    }

}

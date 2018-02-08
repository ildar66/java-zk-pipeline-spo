package com.vtb.mapping.entities.report;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents template entity.
 * @author Michael Kuznetsov 
 */
@Entity
@Table(name = "V_REPORT_TEMPLATE")
public class ReportTemplateJPA implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_TEMPLATE")
    @SequenceGenerator(name = "ReportTemplateSequenceGenerator", sequenceName = "REPORT_TEMPLATE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ReportTemplateSequenceGenerator")
    private Long idDocTemplate;

    @Column(name = "TEMPLATE_NAME")
    private String name;
    
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "TEMPLATE_DATA")
    private String text;

    @Column(name = "FILENAME")
    private String filename;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "DOC_PATTERN")
    private byte[] docPattern;
    
    @ManyToOne
    @JoinColumn(name = "TYPE")
    private ReportTemplateTypeJPA type;

    @Column(name = "system")
    private String system;
    
    @Column(name = "full_hierarchy")
    private String fullHierarchy;
    
    public ReportTemplateJPA() {
        super();    
    }
    
	/**
	 * @param idDocTemplate
	 * @param questionType
	 */
	public ReportTemplateJPA(long idDocTemplate, String name) {
		super();
		this.idDocTemplate = idDocTemplate;
		this.name = name;
	}

    /**
     * Returns template identifier.
     * @return template identifier
     */
    public Long getIdDocTemplate() {
        return idDocTemplate;
    }

    /**
     * Sets template identifier.
     * @param idDocTemplate template identifier
     */
    public void setIdDocTemplate(Long idDocTemplate) {
        this.idDocTemplate = idDocTemplate;
    }

    /**
     * Returns template name.
     * @return template name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets template name.
     * @param name template name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns template text.
     * @return template text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets template text.
     * @param text template text
     */
    public void setText(String text) {
        this.text = text;
    }

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
    /**
     * Returns MSWOrd representation of report, if the type is PRINT_REPORT_DOC.
     * @return MSWOrd representation of report, if the type is PRINT_REPORT_DOC
     */
    public byte[] getDocPattern() {
        return docPattern;
    }

    /**
     * Sets MSWOrd representation of report, if the type is PRINT_REPORT_DOC
     * @param docPattern MSWOrd representation of report, if the type is PRINT_REPORT_DOC
     */
    public void setDocPattern(byte[] docPattern) {
        this.docPattern = docPattern;
    }
    
    /**
     *  Get system (SPO, MAC, etc)
     */
    public String getSystem() {
        return system;
    }

    /**
     * Set system (SPO, MAC, etc)
     * @param system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * Set report template type 
     * @param type
     */
    public void setType(ReportTemplateTypeJPA type) {
        this.type = type;
    }

    /**
     * Get report template type 
     */
    public ReportTemplateTypeJPA getType() {
        return type;
    }

    /**
     * Get whether report should be created with full hierarchy (limit, sublimit, opportunity) or not
     */
    public String getFullHierarchy() {
        return fullHierarchy;
    }

    /**
     * Set whether report should be created with full hierarchy (limit, sublimit, opportunity) or not
     * @param fullHierarchy Y or N
     */
    public void setFullHierarchy(String fullHierarchy) {
        this.fullHierarchy = fullHierarchy;
    }
}

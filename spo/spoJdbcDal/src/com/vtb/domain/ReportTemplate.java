package com.vtb.domain;

/**
 * VtbObject "шаблон отчета"
 * @author Michael Kuznetsov
 */
public class ReportTemplate extends VtbObject {
	
    private static final long serialVersionUID = 1L;
	
	public enum ReportTemplateTypeEnum {
		REPORT_TYPE("REPORT"),
		PRINT_FORM_TYPE("PRINT_FORM"),
		PRINT_DOC_REPORT_TYPE("PRINT_FORM_WORD"),
		PRINT_EXCEL_REPORT_TYPE("PRINT_FORM_EXCEL");
		
		private String value;
		
		/**
		 * Constructor
		 * @param value value of report type
		 */
		ReportTemplateTypeEnum(String value) {
			this.value = value;
		}
		
		/**
		 * Returns value of report type
		 * @return value of report type
		 */
		public String getValue() { return value; }
		
		/**
		 * Checks the equality 
		 * @param type value of report type 
		 * @return <code>true</code> - if are equal
		 */
		public boolean equals(String value) { return (this.value.equals(value)); }
	};
	
	private Long id = null; // Код
	private String name = " "; // имя
	private String text = " "; // Собственно, текст.
	private String type = null; // Тип шаблона (doc или XSLT)
    private String filename = null; // Имя файла (если есть). Для связи с doc-файлами
    private byte[] docPattern;
    private boolean fullHierarchy = false;
	
	/**
	 * Конструктор "шаблон отчета". 
	 * @param aId
	 * @param aName 
	 */
	public ReportTemplate(Long aId, String aName, String aText, String type, String filename, boolean fullHierarchy) {
		setId(aId);
		setName(aName); 
		setText(aText);
		setType(type);
		setFilename(filename);
		setFullHierarchy(fullHierarchy);
	}
	
	/**
	 * Конструктор "шаблон отчета".
	 * @param aId
	 * Код "тип вопроса для кредитных комитетов"
	 */
	public ReportTemplate(Long aId) {
		setId(aId);
		setName(" ");
		setText(" ");
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof ReportTemplate)) {
			return false;
		}
		ReportTemplate aDocTemplate = (ReportTemplate) anObject;
		return aDocTemplate.getId().intValue() == getId().intValue();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("DocTemplate: ");
		sb.append(getId() + "(" + getName() + ")");
		return sb.toString();
	}

	/**
	 * Возвращает Код "шаблон отчета"
	 * @return
	 */
	public Long  getId() {
		return id;
	}

	/**
	 * Устанавливает Код "шаблон отчета"
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает имя "шаблон отчета"
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Устанавливает имя "шаблон отчета"
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Возвращает текст шаблона
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Устанавливает текст шаблона
	 * @param typeName
	 */
	public void setText(String text) {
		this.text = text;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

    public boolean isFullHierarchy() {
        return fullHierarchy;
    }

    public void setFullHierarchy(boolean fullHierarchy) {
        this.fullHierarchy = fullHierarchy;
    }

}

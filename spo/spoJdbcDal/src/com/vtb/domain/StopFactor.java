package com.vtb.domain;

/**
 * This class describes the type of stop factors in the system
 * 
 * Class is deprecated. Use instead a StopFactor in Compendium project instead -- MK.
 * 
 * @author Tormozov M.G.
 * 
 */
@Deprecated
public class StopFactor extends VtbObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code = null;//
	private String description = null;
	private String type=null;//тип стопфактора. От клиентского менеджера или от безопасности.

	@Override
	@Deprecated
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!(anObject instanceof StopFactor)) {
			return false;
		}
		StopFactor aStopFactor = (StopFactor) anObject;
		return aStopFactor.getCode().equals(getCode());
	}

	@Override
	@Deprecated
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("StopFactor: ");
		sb.append(getCode() + "(" + getDescription() + ")");
		// sb.append('\n');
		// sb.append(" IsActive: ");
		// sb.append(getIsActive());

		return sb.toString();
	}

	@Deprecated
	public StopFactor(String code) {
		super();
		this.code = code;
	}

	@Deprecated
	public StopFactor(String code, String description, String type) {
		super();
		this.code = code;
		this.description = description;
		this.type = type;
	}

	@Deprecated
	public String getCode() {
		return code;
	}

	@Deprecated
	public void setCode(String code) {
		this.code = code;
	}

	@Deprecated
	public String getDescription() {
		return description;
	}

	@Deprecated
	public void setDescription(String description) {
		this.description = description;
	}

	@Deprecated
    public String getType() {
        return type;
    }

	@Deprecated
    public void setType(String type) {
        this.type = type;
    }

}

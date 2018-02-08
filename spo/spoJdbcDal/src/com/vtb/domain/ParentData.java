package com.vtb.domain;
/**
 * This class describes the data of parent Task, if present
 * @author Michael Kuznetsov
 */
public class ParentData extends VtbObject {
    private static final long serialVersionUID = 1L;

    private String sublimitNumber = ""; // номер сублимита (расширенный, с указанием номера лимита)

    /**
     * Конструктор
     */
    public ParentData() {
        super();
    }

    /**
     * Получить номер сублимита (расширенный, с указанием номера лимита)
     * @return номер сублимита (расширенный, с указанием номера лимита)
     */
	public String getSublimitNumber() {
		return sublimitNumber;
	}

	/**
	 * Задать номер сублимита (расширенный, с указанием номера лимита)
	 * @param sublimitNumber номер сублимита (расширенный, с указанием номера лимита)
	 */
	public void setSublimitNumber(String sublimitNumber) {
		this.sublimitNumber = sublimitNumber;
	}

}

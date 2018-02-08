package ru.md.compare;

/**
 * @author rislamov
 */

public class CompareSublimitsInfo {
	String ids;
	String number;
	String type;

	public CompareSublimitsInfo(String ids, String number, String type) {
		super();
		this.ids = ids;
		this.number = number;
		this.type = type;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeader() {
		String head = "";
		if (getType().equals("product"))
			head = "Сделка ";
		else if (getType().equals("limit"))
			head = "Лимит ";
		else if (getType().equals("sublimit"))
			head = "Сублимит ";
		return head + getNumber();
	}
}
package ru.md.compare;

import java.util.ArrayList;
import java.util.List;

/**
 * Шапка для сравнения указывает, какие поля требуются для сравнения
 * @author rislamov
 */
public class HeaderElement {

	List<HeaderElement> keys = new ArrayList<HeaderElement>();
	String key = null;
	boolean isList = false;
	boolean isHidden = false;

	public HeaderElement() {}

	public HeaderElement(String key) {
		this.key = key;
	}

	public HeaderElement(String key, boolean isList) {
		this.key = key;
		this.isList = isList;
	}

	public HeaderElement(String key, boolean isList, boolean isHidden) {
		this.key = key;
		this.isList = isList;
		this.isHidden = isHidden;
	}

	public boolean isKey() {
		return (keys == null || keys.size() == 0) && !isList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<HeaderElement> getKeys() {
		return keys;
	}

	public boolean isList() {
		return isList;
	}

	public void setList(boolean isList) {
		this.isList = isList;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	@Override
	public String toString() {
		return toString("");
	}

	private String toString(String tab) {
		String res = tab + "key =" + key + ", values = {\n";
		if (keys != null && keys.size() > 0)
			for (HeaderElement e : keys)
				res += e.toString(tab + "\t");
		else res += tab + "[empty]";
		res += tab + "}\n";
		return res;
	}

}

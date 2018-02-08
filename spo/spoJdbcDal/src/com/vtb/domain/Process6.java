/**
 * 
 */
package com.vtb.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Процессы с атрибутами из 6 was СПО.
 * @author user
 *
 */
public class Process6 extends VtbObject {
	private static final long serialVersionUID = 8767056901430555418L;
	private Long idprocess;
	private LinkedHashMap<String,ArrayList<String>> attr;
	private ArrayList<AttachmentFile> files;
	/**
	 * @return the idprocess
	 */
	public Long getIdprocess() {
		return idprocess;
	}
	/**
	 * @param idprocess the idprocess to set
	 */
	public void setIdprocess(Long idprocess) {
		this.idprocess = idprocess;
	}
	public Process6(Long idprocess) {
		super();
		this.idprocess = idprocess;
		attr=new LinkedHashMap<String, ArrayList<String>>();
		files = new ArrayList<AttachmentFile>();
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return attr.get("Заявка №").get(0);
	}
	/**
	 * @return the sum
	 */
	public String getSum() {
		return attr.get("Сумма лимита").get(0)+" "+attr.get("Валюта").get(0);
	}
	/**
	 * @return the org
	 */
	public String getOrg() {
		String res="";
		for(String org :attr.get("CRM_Контрагенты")){
			res=res+org+"\n";
		}
		return res;
	}
	/**
	 * @return the attr
	 */
	public LinkedHashMap<String, ArrayList<String>> getAttr() {
		return attr;
	}
	/**
	 * @return the files
	 */
	public ArrayList<AttachmentFile> getFiles() {
		return files;
	}
}

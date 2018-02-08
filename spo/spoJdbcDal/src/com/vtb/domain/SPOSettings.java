package com.vtb.domain;
/**
 * VtbObject "настройки SPO для филиалов"
 * 
 * @author admin
 * 
 */
public class SPOSettings extends VtbObject {
	public static final int CONST_SERVER_TYPE_WAS = 0;
	public static final int CONST_SERVER_TYPE_DOMINO = 1;	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5851082713567677445L;
	long id_department;
	String fileHost_IP;
	int fileHost_Type;
	
	String mq_hostname;
	String mq_port;
	String mq_queuemanagerName;
	String mq_serverChannel;
	String mq_queueName;
	
	public String getFileHost_IP() {
		return fileHost_IP;
	}
	public void setFileHost_IP(String fileHost_IP) {
		this.fileHost_IP = fileHost_IP;
	}
	public int getFileHost_Type() {
		return fileHost_Type;
	}
	public void setFileHost_Type(int fileHost_Type) {
		this.fileHost_Type = fileHost_Type;
	}
	public long getId_department() {
		return id_department;
	}
	public void setId_department(long id_department) {
		this.id_department = id_department;
	}
	public String getMq_hostname() {
		return mq_hostname;
	}
	public void setMq_hostname(String mq_hostname) {
		this.mq_hostname = mq_hostname;
	}
	public String getMq_port() {
		return mq_port;
	}
	public void setMq_port(String mq_port) {
		this.mq_port = mq_port;
	}
	public String getMq_queuemanagerName() {
		return mq_queuemanagerName;
	}
	public void setMq_queuemanagerName(String mq_queuemanagerName) {
		this.mq_queuemanagerName = mq_queuemanagerName;
	}
	public String getMq_queueName() {
		return mq_queueName;
	}
	public void setMq_queueName(String mq_queueName) {
		this.mq_queueName = mq_queueName;
	}
	public String getMq_serverChannel() {
		return mq_serverChannel;
	}
	public void setMq_serverChannel(String mq_serverChannel) {
		this.mq_serverChannel = mq_serverChannel;
	}
	public SPOSettings(long id_department) {
		super();
		this.id_department = id_department;
	} 
}

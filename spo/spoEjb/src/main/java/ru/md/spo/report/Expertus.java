package ru.md.spo.report;

import java.util.ArrayList;
import java.util.List;

import ru.md.pup.dbobjects.UserJPA;

/*
 * Экспертиза
 */
public class Expertus {
	private String name;
	private String dataStart;
	private String dataEnd;
	private UserJPA user;
	private List<UserJPA> group=new ArrayList<UserJPA>();//Экспертная группа
	private boolean canEdit=false;
	private int rowspan=0;//сколько итераций экспертизы
	public String getDataStart() {
		return dataStart;
	}
	public void setDataStart(String dataStart) {
		this.dataStart = dataStart;
	}
	public String getDataEnd() {
		return dataEnd;
	}
	public void setDataEnd(String dataEnd) {
		this.dataEnd = dataEnd;
	}
	public UserJPA getUser() {
		return user;
	}
	public void setUser(UserJPA user) {
		this.user = user;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<UserJPA> getGroup() {
		return group;
	}
	public void setGroup(List<UserJPA> group) {
		this.group = group;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public int getRowspan() {
		return rowspan;
	}
	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
	
}

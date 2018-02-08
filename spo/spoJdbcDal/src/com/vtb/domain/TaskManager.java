package com.vtb.domain;

import ru.masterdm.compendium.domain.User;

/**
 * Менеджер в заявке
 * @author Andrey Pavlenko
 *
 */
public class TaskManager  extends VtbObject {
	private static final long serialVersionUID = 4026619850608087327L;

	private String name;
	private User user;
	private Long id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public TaskManager(String name, User user, Long id) {
		super();
		this.name = name;
		this.user = user;
		this.id = id;
	}
}

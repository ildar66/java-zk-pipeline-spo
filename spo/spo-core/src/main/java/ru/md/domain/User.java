package ru.md.domain;

/**
 * пользователь
 * @author Andrey Pavlenko
 */
public class User {
	private Long id;
	private Long idDepartment;
	private String depname;
	private String email;
	private String login;
	private String lastName;
	private String firstName;
	private String patronymic;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Возвращает почту пользователя
	 * 
	 * @return почта пользователя
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Устанавливает почту пользователя
	 * 
	 * @param email почта пользователя
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Возвращает логин пользователя
	 * 
	 * @return логин пользователя
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * Устанавливает логин пользователя
	 * 
	 * @param login логин пользователя
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Устанавливает фамилию пользователя
	 * 
	 * @return фамилия пользователя
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Устанавливает фамилию пользователя
	 * 
	 * @param lastName фамилия пользователя
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Возвращает имя пользователя
	 * 
	 * @return имя пользователя
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * Устанавливает имя пользователя
	 * 
	 * @param firstName имя пользователя
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Возвращает отчество пользователя
	 * 
	 * @return отчество пользователя
	 */
	public String getPatronymic() {
		return this.patronymic;
	}

	/**
	 * Устанавливает отчество пользователя
	 * 
	 * @param patronymic отчество пользователя
	 */
	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	/**
	 * Возвращает {@link String полное имя}
	 * 
	 * @return {@link String полное имя}
	 */
	public String getFullName() {
    	StringBuilder sb = new StringBuilder();
    	if (lastName != null && lastName != "")
    		sb.append(lastName);
    	if (firstName != null && firstName != "")
    		sb.append(" " + firstName);
    	if (patronymic != null && patronymic != "")
    		sb.append(" " + patronymic);
    	return sb.toString();
	}

	public Long getIdDepartment() {
		return idDepartment;
	}

	public void setIdDepartment(Long idDepartment) {
		this.idDepartment = idDepartment;
	}

	public String getDepname() {
		return depname;
	}

	public void setDepname(String depname) {
		this.depname = depname;
	}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", idDepartment=" + idDepartment +
                ", login='" + login + '\'' +
                '}';
    }
}

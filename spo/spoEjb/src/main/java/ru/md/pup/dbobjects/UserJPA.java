package ru.md.pup.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import ru.masterdm.compendium.value.DepartmentTypeTypes;

/**
 * JPA object "Пользователь системы СПО".
 * 
 */
@Entity
@Table(name = "Users")
public class UserJPA implements Serializable {
    private static final long serialVersionUID = 3L;
    public static final String ACCESS_DOWNLOAD = "Загрузчик из access";//роль для загрузки из access
    public static final String ACCESS_DLD_CNTRL = "Контролер загрузки из access";//роль для контроля загрузки из access

    @Id
    @Column(name = "ID_USER")
    @SequenceGenerator(name = "UsersSequenceGenerator", sequenceName = "users_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UsersSequenceGenerator")
    private Long idUser;

    @Column(name = "MAIL_USER")
    private String mailUser;
    @Column(name="ALL_EMAILS")
    private String allEmails;

    private String login;

    private String surname;

    private String name;

    private String patronymic;

    @ManyToOne @JoinColumn(name="ID_DEPARTMENT")
    private DepartmentJPA department;

    @Column(name = "IS_ACTIVE")
    private BigDecimal isActive;

    @Column(name = "IS_GSS")
    private Boolean isGss;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_active", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "id_role"))
    private List<RoleJPA> roles;

    @OneToMany(mappedBy = "userTo", fetch = FetchType.LAZY)
    private Set<AssignJPA> assigns;

    public String toString() {
        return surname + " (" + login + ", email="+getAllEmails()+")";
    }

    /**
     * Конструктор JPA object "Пользователь системы СПО".
     */
    public UserJPA() {
        super();
    }

    public UserJPA(Long idUser) {
        super();
        this.idUser = idUser;
    }

    /**
     * ID пользователя
     * 
     * @return long
     */
    public Long getIdUser() {
        return this.idUser;
    }

    /**
     * ID пользователя
     * 
     * @param idUser
     */
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    /**
     * почта пользователя
     * 
     * @return
     */
    public String getMailUser() {
        return this.mailUser;
    }

    /**
     * почта пользователя
     * 
     * @param mailUser
     */
    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    /**
     * Логин пользователя
     * 
     * @return
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Логин пользователя
     * 
     * @param login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Фамилия пользователя
     * 
     * @return
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     * Фамилия пользователя
     * 
     * @param surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Имя пользователя
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Имя пользователя
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Отчество пользователя
     * 
     * @return
     */
    public String getPatronymic() {
        return this.patronymic;
    }

    /**
     * Отчество пользователя
     * 
     * @param patronymic
     */
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    /**
     * подразделение
     */
    public DepartmentJPA getDepartment() {
        return this.department;
    }

    /**
     * подразделение
     * @param idDepartment
     */
    public void setDepartment(DepartmentJPA department) {
        this.department = department;
    }

    /**
     * Признак активности
     * 
     * @return
     */
    public BigDecimal getIsActive() {
        return this.isActive;
    }

    /**
     * Признак активности
     * 
     * @return
     */
    public boolean isActive() {
        return this.isActive == null || this.isActive.longValue() == 1;
    }

    /**
     * Признак активности
     * 
     * @param isActive
     */
    public void setIsActive(BigDecimal isActive) {
        this.isActive = isActive;
    }

    /**
     * Full name of the user.
     * 
     * @return String
     */
    public String getFullName() {
    	StringBuilder sb = new StringBuilder();
    	if (surname != null && surname != "")
    		sb.append(surname);
    	if (name != null && name != "")
    		sb.append(" " + name);
    	if (patronymic != null && patronymic != "")
    		sb.append(" " + patronymic);
    	return sb.toString();
    }

    public String getFullNameWithRoles(Long processTypeId) {
        StringBuilder sb = new StringBuilder();
        if (surname != null && surname != "")
            sb.append(surname);
        if (name != null && name != "")
            sb.append(" " + name);
        if (patronymic != null && patronymic != "")
            sb.append(" " + patronymic);
        String roles = "";
        for (RoleJPA role : this.getRoles()){
            if(processTypeId==null || processTypeId.equals(role.getProcess().getIdTypeProcess())){
            	if(!roles.isEmpty()) roles += ", ";
            	roles += role.getNameRole();
            }
        }
        return sb.toString() + "(" + roles + ")";
    }

    /**
     * @return roles
     */
    public List<RoleJPA> getRoles() {
        return roles;
    }

    /**
     * @param roles
     *            roles
     */
    public void setRoles(List<RoleJPA> roles) {
        this.roles = roles;
    }

    /**
     * @return assigns
     */
    public Set<AssignJPA> getAssigns() {
        return assigns;
    }

    /**
     * @param assigns
     *            assigns
     */
    public void setAssigns(Set<AssignJPA> assigns) {
        this.assigns = assigns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idUser == null) ? 0 : idUser.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserJPA other = (UserJPA) obj;
        if (idUser == null) {
            if (other.idUser != null)
                return false;
        } else if (!idUser.equals(other.idUser))
            return false;
        return true;
    }

    /** этот пользователь - простой сотрудник (не начальник).*/
    public boolean isWorker() {
        return isWorker(null);
    }
    /** этот пользователь - простой сотрудник (не начальник).*/
    public boolean isWorker(Long idTypeProcess){
    	for (RoleJPA role : this.getRoles()){
    		if (idTypeProcess!=null && !role.getProcess().getIdTypeProcess().equals(idTypeProcess))
    			continue;
    		if(role.getNameRole().startsWith("Руководитель")) return false;
    		if(role.getNameRole().startsWith("Начальник")) return false;
    		if(role.getNameRole().startsWith("Аудитор")) return false;
    		if(role.getChildRoles().size()>0) return false;
    		if(role.getParentRoles().size()==0) return false;
    	}
    	return true;
    }
    public boolean isBoss(Long idTypeProcess){
        for (RoleJPA role : this.getRoles()){
            if (idTypeProcess!=null && !role.getProcess().getIdTypeProcess().equals(idTypeProcess))
                continue;
            if(role.getChildRoles().size()>0) return true;
        }
        return false;
    }
    public boolean hasRole(RoleJPA role){
    	return hasRole(role.getProcess().getIdTypeProcess(), role.getNameRole());
    }
    public boolean hasRole(Long idTypeProcess, String roleName){
    	for (RoleJPA role : this.getRoles()){
            if(idTypeProcess!=null && !idTypeProcess.equals(role.getProcess().getIdTypeProcess()))
                continue;
            if(role.getNameRole().equals(roleName)) return true;
        }
        return false;
    }
    /** этот пользователь - большой аудитор (не только своего департамента).
     * он может видеть все заявки всех подразделений в рамках своего БП*/
    public boolean isAuditor(Long idTypeProcess){
    	return hasRole(idTypeProcess,"Аудитор") || hasRole(idTypeProcess,"Руководитель мидл-офиса") || isAdmin();
    }
    /** 
     * пользователь - редактор нормативных сроков
     */
    public boolean isStandardPeriodEditor(Long idTypeProcess){
        return hasRole(idTypeProcess,"Редактор нормативных сроков");
    }
    /** этот пользователь - большой аудитор (не только своего департамента).
     * он может видеть все хаявки всех подразделений в рамках своего БП*/
    public boolean isAuditor(){
        return isAuditor(null);
    }
    /** этот пользователь - эксперт.*/
    public boolean isExpert() {
        return department.getDepTypeList().contains(
                new DepTypeJPA(DepartmentTypeTypes.EXPERT.getValue()));
    }
    /** этот пользователь - администратор системы.*/
    public boolean isAdmin(){
    	return hasRole(null,"Администратор системы");
    }
    public boolean isStructurator(Long idTypeProcess){
    	return hasRole(idTypeProcess,"Структуратор") || hasRole(idTypeProcess,"Руководитель структуратора")
    			|| hasRole(idTypeProcess,"Структуратор (за МО)")|| hasRole(idTypeProcess,"Руководитель структуратора (за МО)")
                || hasRole(null,UserJPA.ACCESS_DOWNLOAD) || hasRole(null,UserJPA.ACCESS_DLD_CNTRL);
    }
    public boolean isStructurator(){
        return hasRole(null,"Структуратор") || hasRole(null,"Руководитель структуратора")
                || hasRole(null,UserJPA.ACCESS_DOWNLOAD) || hasRole(null,UserJPA.ACCESS_DLD_CNTRL);
    }
    public boolean isCanEditStandardPeriod(Long idTypeProcess){
    	return hasRole(idTypeProcess,"Руководитель структуратора") 
    			|| hasRole(idTypeProcess,"Руководитель мидл-офиса");
    }

    /**
     * Проверяет, что пользователь по нужному БП может только просматривать заявку, но не прикреплять документ.
     */
	public boolean isReadOnlyUser(Long idTypeProcess) {
		if(idTypeProcess==null || idTypeProcess.equals(0L))
			return false;//если это сделка без процесса, то можно редактировать документы по заявке
		for (RoleJPA role : this.getRoles()){
            if(idTypeProcess!=null && !idTypeProcess.equals(role.getProcess().getIdTypeProcess()))
                continue;
            if(role.getNameRole().equals("Аудитор")
            		||role.getNameRole().equals("Аудитор департамента")
            		||role.getNameRole().equals("Секретарь")
            		||role.getNameRole().equals("Редактор нормативных сроков")
            		||role.getNameRole().startsWith("Администратор")) {
            	//do nothing
            } else {
            	return false;
            }
        }
        return true;
	}
	public String getIdStr(){
		return idUser.toString();
	}
	/**
	 * вся почта оператора, включая дополнительную
	 * @return
	 */
	public String getAllEmails() {
		return allEmails;
	}
	
	/**
	 * вся почта оператора, включая дополнительную
	 */
	public void setAllEmails(String allEmails) {
		this.allEmails = allEmails;
	}

    public Boolean getGss() {
        return isGss;
    }

    public void setGss(Boolean gss) {
        isGss = gss;
    }
}

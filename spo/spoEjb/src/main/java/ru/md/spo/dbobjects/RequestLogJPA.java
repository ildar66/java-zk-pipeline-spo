package ru.md.spo.dbobjects;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.md.pup.dbobjects.UserJPA;

/**
 * Логирование запросов. 
 * @author Michael Kuznetsov
 */
@Entity
@Table(name = "request_log")
public class RequestLogJPA implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_log")
    @SequenceGenerator(name = "request_log_seq", sequenceName = "request_log_seq", allocationSize = 1)
    @GeneratedValue(generator = "request_log_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
	
    @ManyToOne @JoinColumn(name="ID_MDTASK")
	TaskJPA task;
	
	@ManyToOne @JoinColumn(name = "id_sender", referencedColumnName = "id_user")
	UserJPA from;

	@ManyToOne @JoinColumn(name = "id_recepient", referencedColumnName = "id_user")
	UserJPA to;
	
    @ManyToMany
    @JoinTable(name = "r_request_log_recepient",
        joinColumns = { @JoinColumn(name = "id_log")},
        inverseJoinColumns = { @JoinColumn(name = "id_recepient")})
    private List<UserJPA> recepients;

	@Column(name = "subject")
	String subject;
	
	@Column(name = "body")
	String body;
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dt_send")
	Date date;
    
	public TaskJPA getTask() {
		return task;
	}
	public void setTask(TaskJPA task) {
		this.task = task;
	}
	public UserJPA getFrom() {
		return from;
	}
	public void setFrom(UserJPA from) {
		this.from = from;
	}
	public UserJPA getTo() {
		return to;
	}
	public void setTo(UserJPA to) {
		this.to = to;
	}
	public String getSubject() {
		if(subject==null) return "";
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<UserJPA> getRecepients() { return recepients; }
	public void setRecepients(List<UserJPA> recepients) { this.recepients = recepients;	}
	
	/**
     * Преобразуем список получателей в строку через запятую 
     * @param recepients список получателей
     * @return список получателей в строку через запятую
     */
	public String recepientsToString() {
    	if (recepients == null) return "";
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i< recepients.size(); i++) {
    		sb.append(recepients.get(i).getFullName());
    		if (i < recepients.size() - 1) sb.append(", ");
    	}
    	return sb.toString();
    }
}

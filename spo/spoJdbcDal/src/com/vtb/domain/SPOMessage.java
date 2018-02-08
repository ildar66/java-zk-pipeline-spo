package com.vtb.domain;

import java.sql.Timestamp;
import java.util.Set;

import javax.mail.internet.InternetAddress;

/**
 * @author Sergey
 * @author Какунин Константин (изменил 7.04.2009)
 *
 * Объект представляющий уведомления, использующиеся внутри СПО
 */
public class SPOMessage extends VtbObject {
    private static final long serialVersionUID = 1L;
    
    // subject formats
    public static final String executionSubjectMessageFormat = "{0} поступил(а) к Вам на исполнение";
    public static final String waitSubjectMessageFormat = "Поступил(а) на обработку {0}";
    public static final String assignSubjectMessageFormat = "Вы назначены исполнителем операции {0} по {1}";
    public static final String pastDueSubjectMessageFormat = "Заявка №{0} просрочена на {1} дн.";

    // body formats
    public static final String executionBodyMessageFormat = 
        "{6} № <a href=\"{0}/showTaskList.do?typeList=perform&searchNumber="+
        "{1}\">{2}</a> ({3}) поступил(а) к Вам на исполнение на {4} по процессу {5} (см. список заявок \"Назначенные мне\")";
        
    public static final String waitBodyMessageFormat = 
        "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
        +"{6} с {3} № <a href=\"{0}/showTaskList.do?typeList=noAccept&searchNumber={1}\">{2}</a> "
        +" поступил(а) к Вам на обработку на {4} по процессу {5} (см. список заявок \"Ожидающие обработки\")"+"</body></html>";
    
    
    public static final String assignBodyMessageFormat = "Вы назначены исполнителем операции {0} по {7} {4} № "+
        "<a href=\"{1}/showTaskList.do?typeList=perform&searchNumber={2}\">{3}</a> процесса {5} для роли {6}";
    
    public static final String pastDueExecutorBodyMessageFormat = 
        "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
        + "Заявка № <a href=\"{0}/showTaskList.do?typeList=accept&searchNumber={1}\">{2}</a> ({3}"
        + "), поступившая к Вам на обработку на {4} по процессу {5} (см. список заявок \"операции в работе\")"
        + ", просрочена на {6} дн.  "
        +"</body></html>";
    
    public static final String pastDueManagerBodyMessageFormat = 
        "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
        + "Заявка № <a href=\"{0}/showTaskList.do?typeList=all&searchNumber={1}\">{2}</a> ({3}"
        + "), поступившая на обработку сотруднику {7} на операцию {4} по процессу {5} (см. список заявок \"Все заявки\")"
        + ", просрочена на {6} дн. "
        +"</body></html>";
    
    public static final String pastDueNotAssignedBodyMessageFormat = 
        "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"></head><body>"
        + "Заявка № <a href=\"{0}/showTaskList.do?typeList=noAccept&searchNumber={1}\">{2}</a> ({3}"
        + "), поступившая на обработку в Ваше подразделение на {4} по процессу {5} (см. список заявок \"Ожидающие обработки\")"
        + ", просрочена на {6} дн. По заявке не был назначен исполнитель."
        +"</body></html>";
    
    private Long idMessage;

    private String body;

    /**
     * Информация о логины операционистов отправителей и их признак прочтения
     */
    private Set<SPOMessageReceiver> receivers;

    private String subject;

    private Timestamp timestamp;
    private Operator operator;

    public SPOMessage() {
    }

    public SPOMessage(Long idMessage, Operator operator, Set<SPOMessageReceiver> receivers, String subject, String body, Timestamp timestamp) {
        this.idMessage = idMessage;
        this.receivers = receivers;
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.operator = operator;
    }

    public String getBody() {
        return body;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public Set<SPOMessageReceiver> getReceivers() {
        return receivers;
    }

    public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getSubject() {
        return subject;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    public void setReceivers(Set<SPOMessageReceiver> receivers) {
        this.receivers = receivers;

    }


    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("idMessage=").append(idMessage);
        s.append(", sender=").append(operator.getLogin());
        s.append(", receivers=").append(receivers);
        s.append(", subject=").append(subject);
        s.append(", message=").append(body);
        s.append(", timestamp=").append(timestamp);
        return s.toString();
    }
    
    /**
     * Test whether the recipient e-mail is valid according to RFC.
     * @param mail recipient e-mail
     * @return true if recipient e-mail is valid, false otherwise
     */
    public static boolean isMailValid(String mail) {
        if ((mail == null) || (mail.equals(""))) {
            return false;
        }
        try {
            InternetAddress[] addresses = InternetAddress.parse(mail, true);
            for (InternetAddress address : addresses) {
                address.validate();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

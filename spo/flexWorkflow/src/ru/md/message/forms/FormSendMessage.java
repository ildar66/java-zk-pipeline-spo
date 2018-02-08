package ru.md.message.forms;

import java.util.Arrays;

import org.apache.struts.action.ActionForm;

/**
 * Форма для создания нового сообщения
 * @author Какунин Константин Юрьевич
 *
 */
public class FormSendMessage extends ActionForm {
    private static final long serialVersionUID = 1L;

    private String body;

    /**
     * Множество получателей письма в виде логинов операторов
     */
    private String[] receivers;

    private String messageSubject;
    
    public String getBody() {
        return body;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public String getMessageSubject() {
        return messageSubject;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public void setMessageSubject(String subject) {
        this.messageSubject = subject;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("receivers: ").append(Arrays.toString(receivers));
        s.append(", subject: ").append(messageSubject);
        s.append(", body: ").append(body);
        return s.toString();
    }

}

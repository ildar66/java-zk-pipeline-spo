package ru.md.message.data;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Данные для просмотра одного сообщения
 * @author Какунин Константин Юрьевич
 *
 */
public class DataViewMessage {

    private String senderFullName;

    private String subject;

    private String body;

    public String getSubject() {
        return subject == null ? "" : subject;
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

    private final Set<String> receiverFullNames = new LinkedHashSet<String>();

    public Set<String> getReceiverFullNames() {
        return receiverFullNames;
    }
    
    public String getFormatTo(){
        StringBuilder s = new StringBuilder(); 
        for (String name : receiverFullNames){
            if (s.length()>0) s.append(", ");
            s.append(name);
        }
        return s.toString();
    }

    public String getSenderFullName() {
        return senderFullName;
    }

    public void setSenderFullName(String senderFullName) {
        this.senderFullName = senderFullName;
    }

    public void addReceiverFullNames(String receiverFullName) {
        receiverFullNames.add(receiverFullName);

    }

}

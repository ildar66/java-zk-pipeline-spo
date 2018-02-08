package ru.md.message.data;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

public class DataMessageItem {
    public String messagePageLink;
    private String subject;
    private String from;
    private Set<String> to;
    private Timestamp time;
    private long messageId;
    private boolean isRead;
    private String cssClass;

    public DataMessageItem(long messageId, String messagePageLink, String subject, 
    		String from, Set<String> to, Timestamp time, boolean isRead, String cssClass) {
        this.messageId = messageId;
        this.messagePageLink = messagePageLink;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.time = time;
        this.isRead = isRead;
        this.cssClass = cssClass;
    }

    public String getFormatTo() {
        StringBuilder s = new StringBuilder();
        for (String name : to) {
            if (s.length() > 0)
                s.append(", ");
            s.append(name);
        }
        return s.toString();
    }

    public String getFrom() {
        return from;
    }

    public String getMessagePageLink() {
        return messagePageLink;
    }

    public String getSubject() {
        return subject;
    }

    public Timestamp getTime() {
        return time;
    }

    public Set<String> getTo() {
        return to;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCssClass() {
		return cssClass;
	}

	@Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("linkMessage = ").append(messagePageLink);
        s.append(", subject = ").append(subject);
        s.append(", from = ").append(from);
        s.append(", to = ").append(to);
        s.append(", time = ").append(time);
        s.append(", isRead = ").append(isRead);
        return s.toString();
    }

    public long getMessageId() {
        return messageId;
    }

}
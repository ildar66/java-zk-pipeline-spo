package ru.md.domain;

/**
 * данные для отчета Аудит прохождения заявки. Комментарий
 * @author Andrey Pavlenko
 */
public class TaskComment {
	private String text;
	private String author;
	private String depname;
	private String commenttime;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDepname() {
		return depname;
	}

	public void setDepname(String depname) {
		this.depname = depname;
	}

	public String getCommenttime() {
		return commenttime;
	}

	public void setCommenttime(String commenttime) {
		this.commenttime = commenttime;
	}
}

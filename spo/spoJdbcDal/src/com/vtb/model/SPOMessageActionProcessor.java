package com.vtb.model;

import com.vtb.exception.MappingException;

/**
 * @author Sergey
 * 
 * Интерфейс взаимодействия с уведомлениями внутри СПО
 */
public interface SPOMessageActionProcessor {
	
	/**
	 * Отправка сообщения через почтовый сервер
	 * 
	 * @param senderMail адрес отправителя
	 * @param senderName видимо, имя отправителя
	 * @param recipients адреса получателей
	 * @param subject тема сообщения
	 * @param body тело сообщения
	 * @throws MappingException 
	 */
	@Deprecated
	public void send(String senderMail, String senderName, String recipients, String subject, String body) throws MappingException;
}

package com.vtb.mapping;

import java.sql.SQLException;
import java.util.ArrayList;
import com.vtb.domain.SPOMessage;
import com.vtb.domain.SPOMessageReceiver;
import com.vtb.exception.MappingException;

/**
 * @author Sergey
 * Расширение интерфейса <code>{@link com.vtb.mapping.Mapper Mapper}</code>
 *
 */
public interface SPOMessageMapper extends Mapper<SPOMessage> {
	
	/**
	 * Выборка всех писем. Фильтр по отправителю <code>sender</code>
	 * 
	 * @param sender Отправитель
	 * @param orderBy Список полей, подлещащих сортировке в SQL формате 
	 * @return Список писем отфильтрованных по отправителю <code>{@link java.util.ArrayList ArrayList}</code>
	 * @throws SQLException 
	 * @throws MappingException
	 */
	public ArrayList<SPOMessage> findBySender(SPOMessage domainObjectWithSender, String orderBy) throws MappingException, SQLException;
	
	/**
	 * Выборка всех писем. Фильтр по приемнику <code>receiver</code>
	 * 
	 * @param receiver Приемник
	 * @param orderBy Список полей, подлещащих сортировке в SQL формате 
	 * @return Список писем отфильтрованных по приемнику <code>{@link java.util.ArrayList ArrayList}</code>
	 * @throws SQLException 
	 * @throws MappingException 
	 */
	public ArrayList<SPOMessage> findByReceiver(SPOMessage domainObjectWithReceiver, String orderBy) throws MappingException, SQLException;
    
    /**
     * Обновить признак прочтения сообщения по указанному получателю письма
     * @param message сообщение
     * @param forReceiver получатель письма
     * @throws MappingException
     */
    public void updateForReceiver(SPOMessage message, SPOMessageReceiver forReceiver) throws MappingException;
}

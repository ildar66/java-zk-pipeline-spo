package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.vtb.domain.Operator;
import com.vtb.domain.SPOMessage;
import com.vtb.domain.SPOMessageReceiver;
import com.vtb.exception.MappingException;
import com.vtb.mapping.MapperFactory;
import com.vtb.mapping.OperatorMapper;
import com.vtb.util.SQLUtils;

/**
 * @author Sergey
 * @author Какунин Константин (изменил 10.04.2009)
 *
 * JDBC-реализация интерфейса <code>SPOMessageMapper</code>  
 */
public class SPOMessageMapper extends JDBCMapperExt<SPOMessage> implements com.vtb.mapping.SPOMessageMapper {
	private static final Logger LOGGER = Logger.getLogger(SPOMessageMapper.class.getName());
    static final private String SELECT_PREFIX = "SELECT m.idMessage, m.mfrom, m.mSubject, m.message, m.mTimestamp, r.mto, r.isread FROM message m, message_receiver r WHERE r.idMessage=m.idMessage";
    
    static final private String CMD_FIND_ALL = SELECT_PREFIX;

    static final private String CMD_FIND_BY_KEY = SELECT_PREFIX + " AND m.idMessage=?";

    static final private String CMD_FIND_BY_RECEIVER = SELECT_PREFIX + " AND r.mto=?";

    static final private String CMD_FIND_BY_SENDER = SELECT_PREFIX + " AND m.mFrom=?";

    static final private String CMD_INSERT_MESSAGE = "INSERT INTO message (IDMessage, mFrom, mSubject, message, mTimestamp) VALUES (?, ?, ?, ?, ?)";

    static final private String CMD_INSERT_MESSAGE_RECEIVER = "INSERT INTO message_receiver (idmessage, id, mto, isread) VALUES (?, ?, ?, ?)";

    static final private String CMD_REMOVE = "DELETE FROM message WHERE idMessage = ?";

    static final private String CMD_UPDATE = "UPDATE message SET mfrom=?, mSubject=?, message=?, mTimestamp =? WHERE idMessage=?";

    @Override
    protected SPOMessage createImpl(Connection conn, SPOMessage m) throws SQLException, MappingException {
        //сохранение сообщение
        PreparedStatement p = SQLUtils.getP(conn, CMD_INSERT_MESSAGE);
        try {
            p.setLong(1, m.getIdMessage());
            p.setString(2, m.getOperator().getLogin());
            p.setString(3, m.getSubject());
            p.setString(4, m.getBody());
            p.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            int count = p.executeUpdate();
            if (count != 1)
                throw new SQLException("Ошибка сохранения сообщения (сохранено " + count + " записей) по запросу: " + CMD_INSERT_MESSAGE);

        } finally {
            SQLUtils.close(p);
        }

        //сохранение получателей сообщения
        PreparedStatement p2 = SQLUtils.getP(conn, CMD_INSERT_MESSAGE_RECEIVER);
        try {
            p2.setLong(1, m.getIdMessage());
            for (SPOMessageReceiver r : m.getReceivers()) {
                p2.setLong(2, r.getId());
                p2.setString(3, r.getOperatorLogin());
                p2.setBoolean(4, r.isRead());
                int count = p2.executeUpdate();
                if (count != 1)
                    throw new SQLException("Ошибка сохранения получателя письма (сохранено " + count + " записей) по запросу: "
                            + CMD_INSERT_MESSAGE_RECEIVER);
            }
        } finally {
            SQLUtils.close(p2);
        }

        return m;
    }

    @Override
    protected List<SPOMessage> findAllImpl(Connection conn) throws SQLException, MappingException {
        PreparedStatement p = SQLUtils.getP(conn, CMD_FIND_ALL);
        return findMessages(CMD_FIND_ALL, p);
    }

    @Override
    protected SPOMessage findByPrimaryKeyImpl(Connection conn, SPOMessage message) throws SQLException, MappingException {
        List<SPOMessage> messages = findMessages(CMD_FIND_BY_KEY, java.sql.Types.BIGINT, message.getIdMessage());
        return messages.size() > 0 ? messages.get(0) : null;
    }

    public ArrayList<SPOMessage> findByReceiver(SPOMessage m, String orderBy) throws SQLException, MappingException {
        ArrayList<SPOMessage> result = new ArrayList<SPOMessage>();
        for (SPOMessageReceiver receiver : m.getReceivers()) {
            String sql = isEmpty(orderBy) ? CMD_FIND_BY_RECEIVER : CMD_FIND_BY_RECEIVER + " order by " + orderBy;
            List<SPOMessage> ms = findMessages(sql, java.sql.Types.VARCHAR, receiver.getOperatorLogin());
            result.addAll(ms);
        }
        return result;
    }

    public ArrayList<SPOMessage> findBySender(SPOMessage m, String orderBy) throws SQLException, MappingException {
        String sql = isEmpty(orderBy) ? CMD_FIND_BY_SENDER : CMD_FIND_BY_SENDER + " order by " + orderBy;
        return findMessages(sql, java.sql.Types.VARCHAR, m.getOperator().getLogin());
    }

    private ArrayList<SPOMessage> findMessages(String sql, int sqlType, Object param) throws SQLException, MappingException {
        Connection conn = getConnection();
        PreparedStatement p = SQLUtils.getP(conn, sql);
        p.setObject(1, param, sqlType);
        return findMessages(sql, p);
    }

    /**
     * Выбрать записи про сформированному запросу
     * @param sql
     * @param p
     * @return
     * @throws SQLException
     * @throws MappingException
     */
    private ArrayList<SPOMessage> findMessages(String sql, PreparedStatement p) throws SQLException, MappingException {
        ArrayList<SPOMessage> listMessages = new ArrayList<SPOMessage>();
        ResultSet rs = null;
        try {
            rs = p.executeQuery();

            //ids - соответсвие id письма к списку id получателей 
            Map<Long, Set<SPOMessageReceiver>> ids = new HashMap<Long, Set<SPOMessageReceiver>>();
            while (rs.next()) {
            	try{
                Long idMessage = rs.getLong("idMessage");
                Set<SPOMessageReceiver> receivers = ids.get(idMessage);
                
                if (receivers == null) {
                    //получено следующее сообщение
                    receivers = new HashSet<SPOMessageReceiver>();
                    SPOMessage message = parseMessage(rs, receivers);
                    listMessages.add(message);
                    ids.put(idMessage, receivers);
                }
                
                //добавление информации о получателе в множество 
                boolean isRead = rs.getInt("isRead") > 0;
                String mTo = rs.getString("mTo");
                SPOMessageReceiver r = new SPOMessageReceiver(mTo, isRead);
                receivers.add(r);
            	} catch (Exception e){
            		//если какое-то сообщение не удалось обработать, то пропускаем
            		LOGGER.info("ERROR findMessages "+e.getMessage());
            	}
            }
        } catch (SQLException e) {
            throw new MappingException(e, "Ошибка при выполнении sql-запроса: " + sql);
        } finally {
            SQLUtils.close(rs);
            SQLUtils.close(p);
        }

        return listMessages;
    }

    @Override
    protected void insertImpl(Connection conn, SPOMessage anObject) throws SQLException, MappingException {
        this.createImpl(conn, anObject);
    }

    private boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    private SPOMessage parseMessage(ResultSet rs, Set<SPOMessageReceiver> receivers) throws SQLException, MappingException {
        long idMessage = rs.getLong("idmessage");
        String sender = rs.getString("mfrom");
        String subject = rs.getString("msubject");
        String message = rs.getString("message");
        Timestamp timestamp = rs.getTimestamp("mtimestamp");
        
        OperatorMapper operatorMapper = (OperatorMapper) MapperFactory.getSystemMapperFactory().getMapper(Operator.class);
        
        return new SPOMessage(idMessage, operatorMapper.findOperatorByLogin(sender), receivers, subject, message, timestamp);
    }

    @Override
    protected void removeImpl(Connection conn, SPOMessage message) throws SQLException, MappingException {
        //удаление писем
        PreparedStatement p = SQLUtils.getP(conn, CMD_REMOVE);
        try {
            p.setLong(1, message.getIdMessage());
            int count = p.executeUpdate();
            if (count != 1)
                throw new SQLException("Ошибка при удалении сообщения, удалено " + count + " записей");
        } finally {
            SQLUtils.close(p);
        }

    }

    public void updateForReceiver(SPOMessage message, SPOMessageReceiver forReceiver) throws MappingException {
        // TODO Автоматически созданная заглушка метода

    }

    @Override
    protected void updateImpl(Connection conn, SPOMessage message) throws SQLException, MappingException {

        PreparedStatement p = SQLUtils.getP(conn, CMD_UPDATE);
        try {
            p.setString(1, message.getOperator().getLogin());
            p.setString(3, message.getSubject());
            p.setString(4, message.getBody());
            p.setTimestamp(5, new java.sql.Timestamp(message.getTimestamp().getTime()));
            p.setLong(6, message.getIdMessage());
            p.executeUpdate();
        } finally {
            SQLUtils.close(p);
        }

    }

}

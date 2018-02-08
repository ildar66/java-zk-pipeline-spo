package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.system.AppService;
import com.vtb.system.TraceCapable;

public abstract class JDBCMapperExt<T> extends JDBCMapper<T> {

    public List<T> findAll() throws MappingException {
        Connection conn = null;
        List<T> objectList = null;
        try {
            // get a connection
            conn = getConnection();
            // single transaction.          
            //conn.setAutoCommit(false);
            objectList = findAllImpl(conn);
            //conn.commit();
        } catch (Exception e) {
            AppService.log(TraceCapable.ERROR_LEVEL,"Exception " + e + " caught in insert()");
            throw new NoSuchObjectException("Wrapped Exception " + e + " caught in insert()");
        } finally {
            close(conn);
        }
        return objectList;
    }   
    /**
     * Update this object (e.g. change its state in the store)
     *
     */
    protected abstract List<T> findAllImpl(Connection conn) throws SQLException, MappingException;

    public void insert(T anObject) throws DuplicateKeyException,
            MappingException {
        Connection conn = null;
        try {
            // get a connection
            conn = getConnection();
            // single transaction.          
            //conn.setAutoCommit(false);
            insertImpl(conn, anObject);
            //conn.commit();
        } catch (Exception e) {
            throw new NoSuchObjectException(e, "Ошибка при добавления объекта " + anObject.getClass());
        } finally {
            close(conn);
        }
    }
    
    /**
     * Update this object (e.g. change its state in the store)
     *
     */
    protected abstract void insertImpl(Connection conn, T anObject) throws SQLException, MappingException;

}

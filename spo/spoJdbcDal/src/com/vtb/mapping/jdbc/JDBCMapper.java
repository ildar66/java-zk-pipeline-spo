package com.vtb.mapping.jdbc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.vtb.exception.DuplicateKeyException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.Mapper;
import com.vtb.system.AppService;
import com.vtb.util.ApplProperties;
import com.vtb.util.EJBClientHelper;
/**
 * This is the abstract superclass of all DomainFactories.
 *
 * Creation date: (2/26/00 3:48:50 PM)
 * @author:ILSUser
 * @author Какунин Константин (изменил 09.04.2009)
 */
public abstract class JDBCMapper<T> implements Mapper<T> {
	protected static final String REFERENCE_NAME_PREFIX = "java:comp/env/";
	static Properties contextProperties = new Properties();
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	{
		contextProperties.put("java.naming.provider.url", "iiop:///");
		contextProperties.put("java.naming.factory.initial", "com.ibm.websphere.naming.WsnInitialContextFactory");
	}

	/**
	 * DomainFactory constructor.
	 */
	public JDBCMapper() {
		super();
	}

	/**
	 * Create a new object into the persistent store; return the
	 * primary key object
	 */
	public T create(T domainObject) throws MappingException {
		Connection conn = null;
		try {
			conn = getConnection();
			T key = createImpl(conn, domainObject);
			//conn.commit();
			return key;
		} catch (Exception e) {
			throw new MappingException(e, "Ошибка в методе create");
		} finally {
			close(conn);
		}

	}

	/**
	 * Create a new object into the persistent store; return the
	 * primary key object
	 */
	protected T create(Connection conn, T domainObject) throws SQLException, MappingException {
		return createImpl(conn, domainObject);
	}

	/**
	 * Create a new object into the persistent store; return the
	 * primary key object
	 */
	protected abstract T createImpl(Connection conn, T domainObject) throws SQLException, MappingException;

	/**
	 * Return a List of all ILSDomainObject (use carefully in practice!)
	 * We use this extensively in our example, but in fact more "wise" enumerators
	 * That would directly query the datasource (e.g. through EJB finders)
	 *
	 * @return List
	 */
	public java.util.List<T> findAllObjects() throws MappingException {
		throw new MappingException("findAllObjects not valid for this type");
	}

	/**
	 * Retrieve a single object matching this object.
	 *
	 * @return ILSDomainObject
	 */
	public T findByPrimaryKey(T domainObjectWithKeyValues) throws NoSuchObjectException {
		Connection conn = null;
		T object = null;
		try {
			conn = getConnection();
			object = findByPrimaryKeyImpl(conn, domainObjectWithKeyValues);
		} catch (Exception e) {
			throw new NoSuchObjectException(e, "Ошибка в методе findByPrimaryKey()");
		} finally {
			close(conn);
		}
		return object;

	}

	/**
	 * Retrieve a single object matching this object.
	 *
	 * @return ILSDomainObject
	 */
	protected T findByPrimaryKey(Connection conn, T domainObjectWithKeyValues) throws SQLException, MappingException {
		return findByPrimaryKeyImpl(conn, domainObjectWithKeyValues);
	}

	/**
	 * Retrieve a single object matching this object.
	 *
	 * @return ILSDomainObject
	 */
	protected abstract T findByPrimaryKeyImpl(Connection conn, T domainObjectWithKeyValues) throws SQLException, MappingException;

	/**
	 * Remove the domain object from the persistent store.
	 *
	 * Creation date: (3/20/00 11:55:18 AM)
	 * @param domainObject ILSDomainObject
	 */
	public void remove(T domainObject) throws NoSuchObjectException {
		Connection conn = null;
		try {
			conn = getConnection();
			removeImpl(conn, domainObject);
		} catch (Exception e) {
			throw new NoSuchObjectException(e, "Ошибка при удалении записи");
		} finally {
			close(conn);
		}

	}

	/**
	 * Remove the domain object from the persistent store.
	 *
	 * Creation date: (3/20/00 11:55:18 AM)
	 * @param domainObject ILSDomainObject
	 */
	public void remove(Connection conn, T domainObject) throws SQLException, MappingException {
		removeImpl(conn, domainObject);
	}

	/**
	 * Remove the domain object from the persistent store.
	 *
	 */
	protected abstract void removeImpl(Connection conn, T domainObject) throws SQLException, MappingException;

	/**
	 * Update this object (e.g. change its state in the store)
	 *
	 */
	public void update(T domainObject) throws MappingException {
		Connection conn = null;
		try {
			conn = getConnection();
			updateImpl(conn, domainObject);
		} catch (Exception e) {
			throw new MappingException(e, "Ошибка при обновлении записи");
		} finally {
			close(conn);
		}
	}

	/**
	 * Update this object (e.g. change its state in the store)
	 *
	 */
	protected void update(Connection conn, T anObject) throws SQLException, MappingException {
		updateImpl(conn, anObject);
	}

	/**
	 * Update this object (e.g. change its state in the store)
	 *
	 */
	protected abstract void updateImpl(Connection conn, T anObject) throws SQLException, MappingException;

	/**
	 * Access a Connection from the Datasource
	 * @return a managed Connection
	 */
	public static Connection getConnection() throws SQLException {
		// get a connection
		DataSource ds = getDataSource();
		return ds.getConnection();

	}

	public static void commitConnection() throws SQLException {
		// in XA transaction 
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/8/00 12:24:41 PM)
	 * @return javax.sql.DataSource
	 */
	public static DataSource getDataSource() {
		DataSource ds = null;
		String dsName = "";
		try {
			InitialContext context = EJBClientHelper.getInitialContext();
			dsName = REFERENCE_NAME_PREFIX + ApplProperties.getDatasourceJndiName();
			ds = (DataSource) context.lookup(dsName);
		} catch (javax.naming.NamingException ne) {
			MappingException e = new MappingException("NamingException: cannot find DataSource in initialContext: " + dsName);
			AppService.handle(e);
		}
		return ds;
	}

	/**
     * Закрыть Connection
     * @param rs
     */
	protected void close(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Ошибка при закрытии Connection", e);
        }
    }
	
	/**
	 * Закрыть Statement
	 * @param p
	 */
	protected void close(Statement p) {
        try {
            if (p != null)
                p.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Ошибка при закрытии Statement", e);
        }
    }

	
	/**
	 * Закрыть ResultSet
	 * @param rs
	 */
	protected void close(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Ошибка при закрытии ResultSet", e);
        }
    }

	/**
	 * 
	 */
	public void insert(T anObject) throws DuplicateKeyException, MappingException {
		Connection conn = null;
		try {
			conn = getConnection();
			createImpl(conn, anObject);
			return;
		} catch (SQLException se) {
			throw new DuplicateKeyException(se, "Insert Failed " + anObject);
		} finally {
			close(conn);
		}
	}

}
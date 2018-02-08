package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.FileRequest;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.system.AppService;
import com.vtb.system.TraceCapable;

public class FileRequestMapper extends JDBCMapperExt<FileRequest> implements com.vtb.mapping.FileRequestMapper {
    public final static String TABLE = "FILE_REQUEST";

    final static String FLD_ID = "id_request";

    final static String FLD_PRIORITY = "priority";

    final static String FLD_STATUS = "status";

    final static String FLD_UNID = "unid";

    final static String FLD_ID_DEPARTMENT = "id_dep_requested";

    final String CMD_FIND_REQUEST_FOR_EXEC = "select " + FLD_ID + ", " + FLD_PRIORITY + "," + FLD_STATUS + "," + FLD_UNID + "," + FLD_ID_DEPARTMENT
            + " " + "from " + TABLE + " " + " where " + FLD_STATUS + "=" + FileRequest.REQ_WAITED + "" + " ";

    final String CMD_FIND_REQUEST_FOR_EXEC_BY_UNID_AND_ID_DEP = "select " + FLD_ID + ", " + FLD_PRIORITY + "," + FLD_STATUS + "," + FLD_UNID + ","
            + FLD_ID_DEPARTMENT + " " + "from " + TABLE + " " + " where " + FLD_STATUS + "=" + FileRequest.REQ_WAITED + " and " + FLD_UNID + "=?"
            + " and " + FLD_ID_DEPARTMENT + "=? ";

    static final String CMD_CREATE = "insert into FILE_REQUEST(id_request, priority, status, unid, ID_DEP_REQUESTED) VALUES(?,?,?,?,?)";

    static final String CMD_REMOVE = "delete from FILE_REQUEST where id_request=?";

    static final String CMD_UPDATE = "update FILE_REQUEST set priority=?, status=?, unid=?, ID_DEP_REQUESTED=? where id_request=?";

    static final String CMD_FIND_BY_KEY = "select * from FILE_REQUEST where id_request=?";

    static final String CMD_FIND_ALL = "select * from FILE_REQUEST";

    @Override
    protected FileRequest createImpl(Connection conn, FileRequest domainObject) throws SQLException, MappingException {
        // TODO Автоматически созданная заглушка метода
        FileRequest fileRequest = domainObject;
        //GENERATE ID			
        insertImpl(conn, fileRequest);
        return null;
    }

    @Override
    protected FileRequest findByPrimaryKeyImpl(Connection conn, FileRequest domainObjectWithKeyValues) throws SQLException, MappingException {
        // TODO Автоматически созданная заглушка метода
        boolean isExist = false;
        if (domainObjectWithKeyValues instanceof FileRequest) {
            FileRequest fileRequest = domainObjectWithKeyValues;
            PreparedStatement stmn = conn.prepareStatement(CMD_FIND_BY_KEY);
            stmn.setLong(1, fileRequest.getId());
            ResultSet rs = stmn.executeQuery();
            if (rs.next()) {
                isExist = true;
                fileRequest.setPriority(rs.getInt("priority"));
                fileRequest.setStatus(rs.getInt("status"));
                fileRequest.setUnid(rs.getString("unid"));
                fileRequest.setId_department(rs.getInt("ID_DEP_REQUESTED"));
            }
            stmn.close();
        } else {
            throw new MappingException("ERROR: incorrect mapping to FileRequest object");
        }

        return (isExist == true ? domainObjectWithKeyValues : null);
    }

    @Override
    protected void removeImpl(Connection conn, FileRequest domainObject) throws SQLException, MappingException {
        // TODO Автоматически созданная заглушка метода

        FileRequest fileRequest = domainObject;
        PreparedStatement stmn = conn.prepareStatement(CMD_REMOVE);
        stmn.setLong(1, fileRequest.getId());
        stmn.execute();
        stmn.close();
    }

    @Override
    protected void updateImpl(Connection conn, FileRequest anObject) throws SQLException, MappingException {
        // TODO Автоматически созданная заглушка метода

        FileRequest fileRequest = (FileRequest) anObject;
        PreparedStatement stmn = conn.prepareStatement(CMD_UPDATE);
        stmn.setInt(1, fileRequest.getPriority());
        stmn.setInt(2, fileRequest.getStatus());
        stmn.setString(3, fileRequest.getUnid());
        stmn.setInt(4, fileRequest.getId_department());
        stmn.setLong(5, fileRequest.getId());
        stmn.execute();
        stmn.close();

    }

    public List<FileRequest> findAll() throws MappingException {
        // TODO Автоматически созданная заглушка метода					

        return null;
    }

    @Override
    protected void insertImpl(Connection conn, FileRequest anObject) throws SQLException, MappingException {
        // TODO Автоматически созданная заглушка метода

        FileRequest fileRequest = (FileRequest) anObject;
        //Checks, whether file already being requested  
        if (findNotCompletedRequestForExecByUnidAndIdDep(fileRequest.getUnid(), fileRequest.getId_department())!= null) {
        	return;
        }
        PreparedStatement stmn = conn.prepareStatement(CMD_CREATE);

        stmn.setLong(1, fileRequest.getId());
        stmn.setInt(2, fileRequest.getPriority());
        stmn.setInt(3, fileRequest.getStatus());
        stmn.setString(4, fileRequest.getUnid());
        AppService.log(1, "STATUS insert : " + fileRequest.getStatus());
        stmn.setInt(5, fileRequest.getId_department());

        stmn.execute();
        stmn.close();

    }

    @Override
    protected List<FileRequest> findAllImpl(Connection conn) throws SQLException, MappingException {
        // TODO Автоматически созданная заглушка метода
        return null;
    }

    public ArrayList<FileRequest> findNotCompletedRequestForExec() throws MappingException {
        Connection conn = null;
        ArrayList<FileRequest> objectList = new ArrayList<FileRequest>();
        try {
            // get a connection
            conn = getConnection();
            // single transaction.			
            //conn.setAutoCommit(false);
            PreparedStatement stmn = conn.prepareStatement(CMD_FIND_REQUEST_FOR_EXEC);
            AppService.log(1, "SQL: " + CMD_FIND_REQUEST_FOR_EXEC);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                FileRequest request = new FileRequest();
                mapTableRow(request, rs);
                objectList.add(request);
            }

            //conn.commit();
        } catch (Exception e) {
            AppService.log(TraceCapable.ERROR_LEVEL, "Exception " + e + " caught in findNotCompletedRequestForExec()");
            throw new NoSuchObjectException("Wrapped Exception " + e + " caught in findNotCompletedRequestForExec()");
        } finally {
            close(conn);
        }
        return objectList;
    }

    public FileRequest findNotCompletedRequestForExecByUnidAndIdDep(String unid, long id_department) throws MappingException {
        Connection conn = null;
        FileRequest fileRequest = null;
        boolean isExist = false;
        try {
            // get a connection
            conn = getConnection();
            // single transaction.			
            //conn.setAutoCommit(false);
            PreparedStatement stmn = conn.prepareStatement(CMD_FIND_REQUEST_FOR_EXEC_BY_UNID_AND_ID_DEP);

            AppService.log(1, "SQL: " + CMD_FIND_REQUEST_FOR_EXEC_BY_UNID_AND_ID_DEP);
            stmn.setString(1, unid);
            stmn.setLong(2, id_department);
            ResultSet rs = stmn.executeQuery();
            while (rs.next()) {
                fileRequest = new FileRequest();
                mapTableRow(fileRequest, rs);
                isExist = true;
            }

            //conn.commit();
        } catch (Exception e) {
            AppService.log(TraceCapable.ERROR_LEVEL, "Exception " + e + " caught in findNotCompletedRequestForExec()");
            throw new NoSuchObjectException("Wrapped Exception " + e + " caught in findNotCompletedRequestForExec()");
        } finally {
            close(conn);
        }
        return (isExist == true ? fileRequest : null);
    }

    protected void mapTableRow(FileRequest request, ResultSet rs) throws SQLException {
        request.setId(rs.getLong(FLD_ID));
        request.setPriority(rs.getInt(FLD_PRIORITY));
        request.setStatus(rs.getInt(FLD_STATUS));
        request.setUnid(rs.getString(FLD_UNID));
        request.setId_department(rs.getInt(FLD_ID_DEPARTMENT));

    }

}

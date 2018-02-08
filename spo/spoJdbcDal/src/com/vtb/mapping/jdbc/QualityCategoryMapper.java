package com.vtb.mapping.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vtb.domain.QualityCategory;
import com.vtb.exception.MappingException;

public class QualityCategoryMapper extends JDBCMapper<QualityCategory> implements com.vtb.mapping.QualityCategoryMapper {

    /**
     * Запрос выборки все записей
     */
    private static final String SELECT_QUALITY_CATEGORY = "select id_category, name_category from " 
            + " quality_category where LOWER(name_category) like LOWER(?)";

    private static final String FIND_QUALITY_CATEGORY = "select id_category, name_category from " 
            + " quality_category where id_category=?";
    /**
     * Запрос выборки все записей
     */
    private static final String INSERT_QUALITY_CATEGORY = "insert into " 
            + " quality_category (id_category, name_category) values (quality_category_seq.nextval, ?)";

    /**
     * Запрос обновления записи
     */
    private static final String UPDATE_QUALITY_CATEGORY = "update " 
            + " quality_category SET name_category=? WHERE id_category=?";

    /**
     * Запрос на удаление одной записи
     */
    private static final String DELETE_QUALITY_CATEGORY = "delete from " 
            + " quality_category where id_category=?";

    @Override
    protected QualityCategory createImpl(Connection conn, QualityCategory q) throws SQLException, MappingException {
        PreparedStatement p = null;
        try {
            p = conn.prepareStatement(INSERT_QUALITY_CATEGORY);
            p.setString(1, q.getNameCategory());
            int count = p.executeUpdate();
            if (count != 1)
                throw new MappingException("Добавлено " + count + " записей");
            return q;
        } catch (SQLException e) {
            throw new MappingException(e, "Ошибка");
        } finally {
            close(p);
        }

    }

    @Override
    protected QualityCategory findByPrimaryKeyImpl(Connection conn, QualityCategory q) throws SQLException, MappingException {
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = conn.prepareStatement(FIND_QUALITY_CATEGORY);
            p.setLong(1, q.getIdCategory());
            rs = p.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id_category");
                String name = rs.getString("name_category");
                QualityCategory res = new QualityCategory();
                res.setIdCategory(id);
                res.setNameCategory(name);
                return res;
            } else
                throw new MappingException("Не найдена запись quality_category");
        } catch (SQLException e) {
            throw new MappingException(e, "Ошибка");
        } finally {
            close(rs);
            close(p);
        }
    }

    @Override
    protected void removeImpl(Connection conn, QualityCategory q) throws SQLException, MappingException {
        PreparedStatement p = null;
        try {
            p = conn.prepareStatement(DELETE_QUALITY_CATEGORY);
            p.setLong(1, q.getIdCategory());
            int count = p.executeUpdate();
            if (count != 1)
                throw new MappingException("Удалено " + count + " записей");
        } catch (SQLException e) {
            throw new MappingException(e, "Ошибка");
        } finally {
            close(p);
        }
    }

    @Override
    protected void updateImpl(Connection conn, QualityCategory q) throws SQLException, MappingException {
        PreparedStatement p = null;
        try {
            p = conn.prepareStatement(UPDATE_QUALITY_CATEGORY);
            p.setString(1, q.getNameCategory());
            p.setLong(2, q.getIdCategory());
            int count = p.executeUpdate();
            if (count != 1)
                throw new MappingException("Обновлено " + count + " записей");
        } catch (SQLException e) {
            throw new MappingException(e, "Ошибка");
        } finally {
            close(p);
        }
    }

    public List<QualityCategory> findQualityCategory(String searchStr, String orderBy) throws MappingException {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement p = null;
        try {
            conn = getConnection();
            StringBuilder sql = new StringBuilder(SELECT_QUALITY_CATEGORY);
            if (orderBy != null && orderBy.length() > 0) {
                sql.append(" order by ").append(orderBy);
            }

            p = conn.prepareStatement(sql.toString());
            p.setString(1, searchStr);
            rs = p.executeQuery();
            List<QualityCategory> res = new ArrayList<QualityCategory>();
            while (rs.next()) {
                long id = rs.getLong("id_category");
                String name = rs.getString("name_category");
                QualityCategory q = new QualityCategory();
                q.setIdCategory(id);
                q.setNameCategory(name);
                res.add(q);
            }
            return res;
        } catch (SQLException e) {
            throw new MappingException(e, "Ошибка");
        } finally {
            close(rs);
            close(p);
            close(conn);
        }
    }

    public List<QualityCategory> findAll() throws MappingException {
        return null;
    }

}

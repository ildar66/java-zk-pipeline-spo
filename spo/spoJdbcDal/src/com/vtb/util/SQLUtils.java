package com.vtb.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.vtb.system.AppService;

/**
 * Утилитный класс для работы в БД
 * @author Какунин Константин Юрьевич
 *
 */
public class SQLUtils {

    /**
     * Получить PreparedStatement для запроса
     * @param conn доступ к базе данных
     * @param sql запрос
     * @return
     * @throws SQLException
     */
    public static PreparedStatement getP(Connection conn, String sql) throws SQLException {
        PreparedStatement p = conn.prepareStatement(sql);
        AppService.debug("[SQL] " + sql);
        return p;
    }

    /**
     * Освободить ресурс базы данных
     * @param rs
     */
    public static void close(ResultSet rs) {
        if (rs != null)
            try {
                rs.close();
            } catch (SQLException e) {
                AppService.error("Ошибка при закрытии resulset", e);
            }
    }

    /**
     * Освободить ресурс базы данных
     * @param p
     */
    public static void close(Statement p) {
        if (p != null)
            try {
                p.close();
            } catch (SQLException e) {
                AppService.error("Ошибка при закрытии Statement", e);
            }
    }
}

package ru.md.spo.util;

import java.sql.Types;

import org.hibernate.Hibernate;

/**
 * Диалект для базы данных Оракл, в котором заменяется тип char на тип String.
 * Позволяет обойти следующую проблему: 
 * hibernate не правильно определяет тип для колонок БД устаревшего типа "char(xx)" и возвращает только первый символ.
 */
public class HibernateDialect extends org.hibernate.dialect.Oracle10gDialect {
    public HibernateDialect() {
        super();
        registerHibernateType (Types.CHAR, Hibernate.STRING.getName ());
        registerHibernateType( Types.NVARCHAR, Hibernate.STRING.getName());
    }
}

package ru.masterdm.spo.dashboard.model;

/**
 * Если алгоритм создания модели является сложным то его можно реализовать в фабрике
 * @author pmasalov
 */
public interface ModelFactory<T> {

    T createModel();
}

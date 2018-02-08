package ru.md.domain.dict;

/**
 * Список ролей.
 * @author Sergey Valiev
 */
@SuppressWarnings("javadoc")
public enum Role {
    STRUCTURE_INSPECTOR_CHIEF("Руководитель структуратора"),
    STRUCTURE_INSPECTOR_STAFF("Структуратор"),
    STRUCTURE_INSPECTOR_CHIEF_MO("Руководитель структуратора (за МО)"),
    STRUCTURE_INSPECTOR_STAFF_MO("Структуратор (за МО)");

    private String value;

    /**
     * Конструктор.
     * @param value значение
     */
    private Role(String value) {
        this.value = value;
    }

    /**
     * Возвращает значение.
     * @return значение
     */
    public String getValue() {
        return value;
    }
}

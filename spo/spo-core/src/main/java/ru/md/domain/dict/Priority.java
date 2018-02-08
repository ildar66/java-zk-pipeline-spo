package ru.md.domain.dict;

/**
 * Справочник приоритетов.
 * @author Sergey Valiev
 */
public enum Priority {
    HIGH("Высокий"),
    MEDIUM("Средний"),
    LOW("Низкий");
    
    private String value;
    
    /**
     * Конструктор.
     * @param value значение
     */
    private Priority(String value) {
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

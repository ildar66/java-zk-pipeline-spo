package ru.md.domain.dict;

/**
 * Справочник размерностей комисиий.
 * @author Sergey Valiev
 */
public enum ComissionDimension {
    PERCENT("%"),
    ANNUAL_PERCENT("%годовых");
    
    private String value;
    
    /**
     * Конструктор.
     * @param value значение
     */
    private ComissionDimension(String value) {
        this.value = value;
    }

    /**
     * Возвращает значение.
     * @return значение
     */
    public String getValue() {
        return value;
    }

    /**
     * Поиск значения по строке.
     * @param value строка
     * @return значение списка
     */
    public static ComissionDimension find(String value) {
        for (ComissionDimension comissionDimension : ComissionDimension.values()) {
            if (comissionDimension.getValue().equalsIgnoreCase(value)) {
                return comissionDimension;
            }
        }
        return null;
    }
}

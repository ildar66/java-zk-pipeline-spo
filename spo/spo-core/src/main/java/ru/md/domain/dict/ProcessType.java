package ru.md.domain.dict;

/**
 * Справочник типов процессов СПО.
 * @author Sergey Valiev
 */
@SuppressWarnings("javadoc")
public enum ProcessType {
    MAJOR_BUSINESS_HEAD_OFFICE("Крупный бизнес ГО"),
    MAJOR_BUSINESS_HEAD_OFFICE_STRUCTURE_INSPECTOR("Крупный бизнес ГО (Структуратор за МО)"),
    PIPELINE("Pipeline");

    private String value;

    /**
     * Конструктор.
     * @param value значение
     */
    private ProcessType(String value) {
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
    public static ProcessType find(String value) {
        for (ProcessType processType : ProcessType.values()) {
            if (processType.getValue().equalsIgnoreCase(value)) {
                return processType;
            }
        }
        return null;
    }
}
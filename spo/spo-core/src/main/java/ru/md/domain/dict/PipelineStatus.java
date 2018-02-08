package ru.md.domain.dict;

/**
 * Список статусов.
 * @author Sergey Valiev
 */
public enum PipelineStatus {
    LIVE("Живая"), CLOSED("Закрыта"), PAUSED("Приостановлена"), CANCELED("Отменена");
    
    private String value;
    
    /**
     * Конструктор.
     * @param value значение
     */
    private PipelineStatus(String value) {
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

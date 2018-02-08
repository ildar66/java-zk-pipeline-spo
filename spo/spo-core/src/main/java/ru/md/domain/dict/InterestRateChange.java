package ru.md.domain.dict;

/**
 * Действия изменения процентной ставки.
 * @author Sergey Valiev
 */
public enum InterestRateChange {
    /** Направить на акцепт. */
    TO_ACCEPT,
    /** Акцептовать. */
    ACCEPTED,
    /** Отправить на доработку. */
    RETURN;
}

package ru.masterdm.spo.list;

/**
 * Состояние заявки
 * Надписи используются из базы
 *
 * @author Andrey Pavlenko
 */
public enum EDashStatus {
	PRODUCT_NEW(1L, "Новые"),
    PRODUCT_FIX(2L, "Одобренные"),
    PRODUCT_TRANCE(3L, "Выборка"),
    PRODUCT_LOST(4L, "Отказанные"),
    LIMIT_NEW(5L, "Новые"),
    CROSS_NEW(6L, "Новые"),
    WAIVER_NEW(7L, "Новые"),
    WAIVER_FIX(8L, "Одобренные"),
    WAIVER_LOST(9L, "Отказанные"),
    CROSS_SELL_ACCEPT(10L, "Одобренные"),
	CROSS_SELL_LOST(11L, "Отказанные"),
    LIMIT_INPROGRESS(12L, "В работе"),
	LIMIT_ACCEPT(14L, "Одобренные"),
    LIMIT_LOST(15L, "Отказанные");

	String name;
	Long id;

	EDashStatus(Long id, String name) {
		this.name = name;
		this.id = id;
	}

    /**
     * Returns .
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Returns .
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     * Поиск экземпляра перечисления по идентификатору
     * @param id
     * @return
     */
    static public EDashStatus find(long id) {
        for (EDashStatus status : EDashStatus.values()) {
            if (status.getId() == id)
                return status;
        }
        return null;
    }
}

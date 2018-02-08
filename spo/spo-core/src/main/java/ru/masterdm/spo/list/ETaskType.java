package ru.masterdm.spo.list;

/**
 * Task Type кодификатор
 * @author pmasalov
 */
public enum ETaskType {
    CROSS_SELL("cross-sell", "Кросс-селл"),
    LIMIT("limit", "Лимиты", "Лимиты (со всеми изменениями)"),
    PRODUCT("product", "Сделки", "Сделки (изменения не входят)"),
    WAIVER("waiver", "Изменённые и вейверы", "изменения сделок и вейверы");

    private String code;
    private String name;
    private String titleName;

    ETaskType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    ETaskType(String code, String name, String titleName) {
        this.code = code;
        this.name = name;
        this.titleName = titleName;
    }

    /**
     * Returns .
     * @return
     */
    public String getCode() {
        return code;
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
    public String getTitleName() {
        return titleName;
    }

    /** Определяет равенство строки коду данного taskType */
    public boolean isEqual(String otherCode) {
        return (code.equals(otherCode));
    }

    public static ETaskType findByCode(String code) {
        if (code == null)
            return null;

        for (ETaskType v : values()) {
            if (v.getCode().equals(code))
                return v;
        }
        return null;
    }
}

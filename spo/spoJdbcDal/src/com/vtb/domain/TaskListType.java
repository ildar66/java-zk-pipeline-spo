package com.vtb.domain;

public enum TaskListType {
    // typeList == 0 - входящие 1 - обработка, 2 - на исполнение (назначенные)
    NOT_ACCEPT("noAccept"),
    ACCEPT("accept"),
    ASSIGN("perform"),
    ACCEPT_FOR_REFUSE("refuse");

    private String type;

    TaskListType(String type) {
        this.type = type;
    }

    /**
     * Returns .
     *
     * @return
     */
    public String getType() {
        return type;
    }

    public static TaskListType getByType(String type) {
        if (type == null)
            throw new RuntimeException("parameter 'type' is null");

        TaskListType result = null;
        if (type != null)
            for (TaskListType eType : TaskListType.values())
                if (type.equals(eType.getType())) {
                    result = eType;
                    break;
                }

        if (result == null)
            throw new RuntimeException("can't find TaskListType by type '" + type + "'");

        return result;
    }

    /**
     * флаг режима 'все заявки'
     */
    public static boolean isAllMode(String type) {
        return (type != null && type.equalsIgnoreCase("all"));
    }
}

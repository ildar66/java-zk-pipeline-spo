package org.uit.director.report;

public class ComponentReport {

    String type;
    String description;
    Object value;
    Object addition = null;
    int indexSelect = 0; // индекс выбранного элемента (только для компонента  SELECT)

    public static class referensType {
        public static final int stagesInTypProcess = 0;
        public static final int rolesInTypeProcess = 1;
        public static final int usersInTypeProcess = 2;
        public static final int variablesInTypProcess = 3;
        public static final int usersInDepartment = 4;  
        public static final int subordinateUsersForUser = 5;
    }

    /**
     * Тип принимает значения: string, select, check. Если тип = select, то
     * значение value - строка, с перечисленными значениями через символ &.
     * Если тип - check, то значение value = true или false.
     *
     * @param type
     * @param description
     * @param value
     */
    public ComponentReport(String type, String description, Object value) {
        this.type = type;
        this.description = description;
        this.value = value;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getAddition() {
        return addition;
    }

    public void setAddition(Object addition) {
        this.addition = addition;
    }

    public int getIndexSelect() {
        return indexSelect;
    }

    public void setIndexSelect(int indexSelect) {
        this.indexSelect = indexSelect;
    }


}

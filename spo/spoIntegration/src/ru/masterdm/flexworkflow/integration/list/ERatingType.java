package ru.masterdm.flexworkflow.integration.list;
/**
 * Какие рейтинги можно получить через getCalcHistoryByPartnerId.
 * @author Andrey Pavlenko
 */
public enum ERatingType {
    calculated(0,"Расчётный"),expCreditFilial(2,"Экспертный (кредитное подразд. филиала)"),
    expCredit(3,"Экспертный (кредитное подразд. ГО)"), 
    client(1,"Клиентский"), expRisk(5,"Экспертный (подразд. по анализу рисков)");
    ERatingType(int id, String displayName) {
        this.id=id;
        this.displayName=displayName;
    }
    private int id;
    public int getId() {
        return id;
    }
    private String displayName;
    public String getDisplayName() {
        return displayName;
    }
}

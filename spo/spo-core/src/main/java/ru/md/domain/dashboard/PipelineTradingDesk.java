package ru.md.domain.dashboard;

/**
 * Pipeline: Трейдинг Деск  (таблица CD_PIPELINE_TRADING_DESK)
 * @author pmasalov
 */
public class PipelineTradingDesk {

    public static final PipelineTradingDesk OTHERS = new PipelineTradingDesk(-1, "ПРОЧИЕ");

    int id;
    String name;

    public PipelineTradingDesk(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public PipelineTradingDesk() {

    }

    /**
     * Returns .
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Sets .
     * @param id
     */
    public void setId(int id) {
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
     * Sets .
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PipelineTradingDesk{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PipelineTradingDesk that = (PipelineTradingDesk) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

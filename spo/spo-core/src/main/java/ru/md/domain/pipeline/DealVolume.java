package ru.md.domain.pipeline;

/**
 * Deal volume(Объемы сделок отраслей компаний) VO.
 * Created by Ildar Shafigullin on 05.10.2017.
 */
public class DealVolume extends BaseVO {

    private String industryName;
    private long volume;

    /**
     * Return industry Name.
     * @return
     */
    public String getIndustryName() {
        return industryName;
    }

    /**
     * Sets industry Name.
     * @param industryName industry Name.
     */
    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    /**
     * Return volume for Industry.
     * @return volume for Industry.
     */
    public long getVolume() {
        return volume;
    }

    /**
     * Sets volume for Industry.
     * @param volume for Industry.
     */
    public void setVolume(long volume) {
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DealVolume that = (DealVolume) o;

        return industryName.equals(that.industryName);

    }

    @Override
    public int hashCode() {
        return industryName.hashCode();
    }
}

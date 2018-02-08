package ru.md.domain.pipeline;

/**
 * Deal count and sum(Количество сделок по отраслям организаций) VO.
 * Created by Ildar Shafigullin on 05.10.2017.
 */
public class DealCount extends BaseVO {

    private String industryName;
    private String orgName;
    private int count;
    private long sum;
    private long sumRUR;
    private long sumUSD;
    private long sumEUR;

    /**
     * Returns .
     * @return
     */
    public String getIndustryName() {
        return industryName;
    }

    /**
     * Sets .
     * @param industryName
     */
    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    /**
     * Returns .
     * @return
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * Sets .
     * @param orgName
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * Returns .
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets .
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Returns .
     * @return
     */
    public long getSum() {
        return sum;
    }

    /**
     * Sets .
     * @param sum
     */
    public void setSum(long sum) {
        this.sum = sum;
    }

    /**
     * Returns .
     * @return
     */
    public long getSumRUR() {
        return sumRUR;
    }

    /**
     * Sets .
     * @param sumRUR
     */
    public void setSumRUR(long sumRUR) {
        this.sumRUR = sumRUR;
    }

    /**
     * Returns .
     * @return
     */
    public long getSumUSD() {
        return sumUSD;
    }

    /**
     * Sets .
     * @param sumUSD
     */
    public void setSumUSD(long sumUSD) {
        this.sumUSD = sumUSD;
    }

    /**
     * Returns .
     * @return
     */
    public long getSumEUR() {
        return sumEUR;
    }

    /**
     * Sets .
     * @param sumEUR
     */
    public void setSumEUR(long sumEUR) {
        this.sumEUR = sumEUR;
    }
}

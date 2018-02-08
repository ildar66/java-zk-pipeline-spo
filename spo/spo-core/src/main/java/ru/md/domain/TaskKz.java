package ru.md.domain;

/**
 * Клиентская запись
 * Created by Andrey Pavlenko on 02.06.15.
 */
public class TaskKz {
    private Long idR;
    private String kzid;
    private String ratingpkr;
    private Long orderDisp;

    public Long getIdR() {
        return idR;
    }

    public void setIdR(Long idR) {
        this.idR = idR;
    }

    public String getKzid() {
        return kzid;
    }

    public void setKzid(String kzid) {
        this.kzid = kzid;
    }

    public String getRatingpkr() {
        return ratingpkr;
    }

    public void setRatingpkr(String ratingpkr) {
        this.ratingpkr = ratingpkr;
    }

    public Long getOrderDisp() {
        return orderDisp;
    }

    public void setOrderDisp(Long orderDisp) {
        this.orderDisp = orderDisp;
    }
    public boolean isMainOrg(){
        return orderDisp!=null && orderDisp.equals(0L);
    }
}

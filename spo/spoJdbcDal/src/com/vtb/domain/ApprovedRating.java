package com.vtb.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ApprovedRating implements Serializable {
    private static final long serialVersionUID = -7924801561943072879L;
    // СѓРЅРёРєР°Р»СЊРЅС‹Р№ СЃРёСЃС‚РµРјРЅС‹Р№ ID СѓС‚РІРµСЂР¶РґС‘РЅРЅРѕРіРѕ СЂРµР№С‚РёРЅРіР°
    private long id;
    // РєРѕРґ РєРѕРЅС‚СЂР°РіРµРЅС‚Р°
    private String partnerId;
    // СѓРЅРёРєР°Р»СЊРЅС‹Р№ СЃРёСЃС‚РµРјРЅС‹Р№ ID РєСЂРµРґРёС‚РЅРѕРіРѕ РєРѕРјРёС‚РµС‚Р°
    private long ccId;
    // РЅР°Р·РІР°РЅРёРµ РєСЂРµРґРёС‚РЅРѕРіРѕ РєРѕРјРёС‚РµС‚Р°
    private String name;
    // РґР°С‚Р° Р·Р°СЃРµРґР°РЅРёСЏ
    private Date date;
    // РЅРѕРјРµСЂ РїСЂРѕС‚РѕРєРѕР»Р°
    private Integer protocolNumber;
    // С‚РёРї СЂРµР№С‚РёРЅРіР°
    // 1) Р Р°СЃС‡С‘С‚РЅС‹Р№ (ratingType = 1)
    // 2) Р­РєСЃРїРµСЂС‚РЅС‹Р№ (РєСЂРµРґРёС‚РЅРѕРµ РїРѕРґСЂР°Р·Рґ.) (ratingType = 3)
    // 3) Р­РєСЃРїРµСЂС‚РЅС‹Р№ (РїРѕРґСЂР°Р·Рґ. РїРѕ Р°РЅР°Р»РёР·Сѓ СЂРёСЃРєРѕРІ) (ratingType = 4)
    // 4) Р?РЅРґРёРІРёРґСѓР°Р»СЊРЅС‹Р№ (ratingType = 5)
    private long ratingType;
    // СЂРµР№С‚РёРЅРі
    private String rating;
    // СЃСѓРјРјР°СЂРЅР°СЏ Р±Р°Р»СЊРЅР°СЏ РѕС†РµРЅРєР°
    private BigDecimal totalScore;
    // РґР°С‚Р° РѕС‚С‡С‘С‚РЅРѕСЃС‚Рё
    private Date repDate;
    // РєРѕРјРјРµРЅС‚Р°СЂРёР№
    private String comment;
    // РїРѕР»СЊР·РѕРІР°С‚РµР»СЊ РєРѕС‚РѕСЂС‹Р№ СЃРѕС…СЂР°РЅРёР» РґРѕРєСѓРјРµРЅС‚
    private String username;
    // РІСЂРµРјСЏ СЃРѕС…СЂР°РЅРµРЅРёСЏ
    private Date modified;

    public ApprovedRating() {
        super();
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public long getCcId() {
        return ccId;
    }

    public void setCcId(long ccId) {
        this.ccId = ccId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(Integer protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public long getRatingType() {
        return ratingType;
    }

    public void setRatingType(long ratingType) {
        this.ratingType = ratingType;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public Date getRepDate() {
        return repDate;
    }

    public void setRepDate(Date repDate) {
        this.repDate = repDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRatingTypeName() {
        if (getRatingType() == 1) {
            return "Р Р°СЃС‡С‘С‚РЅС‹Р№";
        } else if (getRatingType() == 3) {
            return "Р­РєСЃРїРµСЂС‚РЅС‹Р№ (РєСЂРµРґРёС‚РЅРѕРµ РїРѕРґСЂР°Р·Рґ.)";
        } else if (getRatingType() == 4) {
            return "Р­РєСЃРїРµСЂС‚РЅС‹Р№ (РїРѕРґСЂР°Р·Рґ. РїРѕ Р°РЅР°Р»РёР·Сѓ СЂРёСЃРєРѕРІ)";
        } else if (getRatingType() == 5) {
            return "Р?РЅРґРёРІРёРґСѓР°Р»СЊРЅС‹Р№";
        } else {
            return null;
        }
    }
}

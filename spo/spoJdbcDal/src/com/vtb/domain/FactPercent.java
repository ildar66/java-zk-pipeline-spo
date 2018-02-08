package com.vtb.domain;
/**
 * FactPercent, transferred from FactPercentJPA.
 */

import java.util.Date;

import javax.persistence.Transient;

import com.vtb.util.Formatter;

public class FactPercent extends VtbObject {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Double riskpremium;//Премия за кредитный риск
    private String riskpremium_print = "";//Премия за кредитный риск для ПКР

    private CdRiskpremium riskpremiumtype;
    private Double riskpremium_change;
    private String riskpremium_change_print = "";
    
    private String indcondition;
    
    private Date end_date;//период по
    private Date start_date;//период с
    private Double fondrate; //Ставка фондирования
    private Double rate2; //Надбавка к процентной ставке за поддержание кредитовых оборотов менее установленного размера
    private Double rate3; //Плата за экономический капитал
    private Double rate4; //Ставка размещения
    private Double rate5; //Компенсирующий спрэд за фиксацию процентной ставки
    private Double rate6; //Компенсирующий спрэд за досрочное погашение
    private Double rate7; //Покрытие прямых расходов
    private Double rate8; //Покрытие общебанковских расходов
    private Double rate9; //Комиссия за выдачу
    private Double rate10; //Комиссия за сопровождение
    private Double rate11; //КТР
    private String supply; //Обеспечение по периоду
    private Long tranceId;//для какого транша

    private String rate4Print;  // индикативная ставка + ставка размещения. Для печати.
    
    //информация из рейтингов. Временно ведем это в СПО
    private Double rating_fondrate;//Ставка фондирования рейтингов
    private Double rating_riskpremium;//Премия за кредитный риск для рейтингов
    private Double rating_rate3;//Плата за экономический капитал для рейтингов
    private Double rating_с1;//Коэффициент С1 для рейтингов
    private Double rating_с2;//Коэффициент С2 для рейтингов
    private Double rating_calc;//Расчетная ставка для рейтингов
    private Double rating_ktr;//Расчетная ставка для рейтингов
    
    /**Расчетная ставка*/
    public Double getCalcRate1(){
        /*Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск + 
        Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение + 
        Покрытие прямых расходов + Покрытие общебанковских расходов + Плата за экономический капитал*/
        Double res = getCalcRate2();
        if(rate3!=null) res +=rate3;
        if(rate8!=null) res +=rate8;
        calc1=res;
        return res;
    }
    /**Расчетная защищенная ставка*/
    public Double getCalcRate2(){
        /*Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск + 
        Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение + Покрытие прямых расходов*/
        Double res = 0.0;
        if(fondrate!=null) res +=fondrate;
        if(riskpremium!=null) res +=riskpremium;
        if(rate5!=null) res +=rate5;
        if(rate6!=null) res +=rate6;
        if(rate7!=null) res +=rate7;
        if(riskpremiumtype!=null){
            if("уменьшенная".equals(riskpremiumtype.getValue()) && riskpremium_change!=null)
                res -= riskpremium_change;
            if("увеличенная".equals(riskpremiumtype.getValue()) && riskpremium_change!=null)
                    res += riskpremium_change;
        }
        calc2=res;
        return res;
    }
    /**Эффективная ставка*/
    public Double getCalcRate3(){
        Double res = 0.0;
        if(rate4!=null) res +=rate4;
        if(rate9!=null) res +=rate9;
        if(rate10!=null) res +=rate10;
        calc3=res;
        return res;        
    }
    @SuppressWarnings("unused") @Transient
    private Double calc1;
    @SuppressWarnings("unused") @Transient
    private Double calc2;
    @SuppressWarnings("unused") @Transient
    private Double calc3;
    
    /**
     * Возвращает {@link Long первичный ключь}
     * 
     * @return {@link Long первичный ключь}
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает {@link Long первичный ключь}
     * 
     * @return id {@link Long первичный ключь}
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Double getRiskpremium() {
        return riskpremium;
    }

    public void setRiskpremium(Double riskpremium) {
        this.riskpremium = riskpremium;
        if (riskpremium != null)
        	riskpremium_print = Formatter.toMoneyFormat(riskpremium) + " % годовых";
        else riskpremium_print = "";
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Double getFondrate() {
        return fondrate;
    }

    public void setFondrate(Double fondrate) {
        this.fondrate = fondrate;
    }

    public Double getRate2() {
        return rate2;
    }

    public void setRate2(Double rate2) {
        this.rate2 = rate2;
    }

    public Double getRate3() {
        return rate3;
    }

    public void setRate3(Double rate3) {
        this.rate3 = rate3;
    }

    public Double getRate4() {
        return rate4;
    }

    public void setRate4(Double rate4) {
        this.rate4 = rate4;
    }

    public Double getRate5() {
        return rate5;
    }

    public void setRate5(Double rate5) {
        this.rate5 = rate5;
    }

    public Double getRate6() {
        return rate6;
    }

    public void setRate6(Double rate6) {
        this.rate6 = rate6;
    }

    public Double getRate7() {
        return rate7;
    }

    public void setRate7(Double rate7) {
        this.rate7 = rate7;
    }

    public Double getRate8() {
        return rate8;
    }

    public void setRate8(Double rate8) {
        this.rate8 = rate8;
    }

    public Double getRate9() {
        return rate9;
    }

    public void setRate9(Double rate9) {
        this.rate9 = rate9;
    }

    public Double getRate10() {
        return rate10;
    }

    public void setRate10(Double rate10) {
        this.rate10 = rate10;
    }

    public Double getRate11() {
        return rate11;
    }

    public void setRate11(Double rate11) {
        this.rate11 = rate11;
    }

    public String getSupply() {
        if(supply==null)
            return "";
        return supply;
    }

    public void setSupply(String supply) {
        this.supply = supply;
    }

    public boolean showRiskpremiumChange(){
        if(riskpremiumtype==null)
            return false;
        String val = riskpremiumtype.getValue();
        return "увеличенная".equals(val) || "уменьшенная".equals(val);
    }
    public String getRiskpremiumtypeID() {
        if(riskpremiumtype==null)
            return "";
        return riskpremiumtype.getId().toString();
    }
    
    public CdRiskpremium getRiskpremiumtype() {
        return riskpremiumtype;
    }
    
    public void setRiskpremiumtype(CdRiskpremium riskpremiumtype) {
        this.riskpremiumtype = riskpremiumtype;
    }
    
    public Double getRiskpremium_change() {
        return riskpremium_change;
    }
    public void setRiskpremium_change(Double riskpremium_change) {
        this.riskpremium_change = riskpremium_change;
        if (riskpremium_change != null)
        	riskpremium_change_print = Formatter.toMoneyFormat(riskpremium_change) + " % годовых";
        else riskpremium_change_print = "";
    }
    
    public String getRiskpremiumTypeDisplay() {
        if(riskpremiumtype==null)
            return "не выбрана";
        return riskpremiumtype.getDescription();
    }
    public Long getTranceId() {
        return tranceId;
    }
    public void setTranceId(Long tranceId) {
        this.tranceId = tranceId;
    }
	public Double getRating_fondrate() {
		return rating_fondrate;
	}
	public void setRating_fondrate(Double rating_fondrate) {
		this.rating_fondrate = rating_fondrate;
	}
	public Double getRating_riskpremium() {
		return rating_riskpremium;
	}
	public void setRating_riskpremium(Double rating_riskpremium) {
		this.rating_riskpremium = rating_riskpremium;
	}
	public Double getRating_rate3() {
		return rating_rate3;
	}
	public void setRating_rate3(Double rating_rate3) {
		this.rating_rate3 = rating_rate3;
	}
	public Double getRating_с1() {
		return rating_с1;
	}
	public void setRating_с1(Double rating_с1) {
		this.rating_с1 = rating_с1;
	}
	public Double getRating_с2() {
		return rating_с2;
	}
	public void setRating_с2(Double rating_с2) {
		this.rating_с2 = rating_с2;
	}
	public Double getRating_calc() {
		return rating_calc;
	}
	public void setRating_calc(Double rating_calc) {
		this.rating_calc = rating_calc;
	}
	public Double getRating_ktr() {
		return rating_ktr;
	}
	public void setRating_ktr(Double rating_ktr) {
		this.rating_ktr = rating_ktr;
	}

	/**
	 * @return the rate4Print
	 */
	public String getRate4Print() {
		return rate4Print;
	}
	
	/**
	 * Устанавливаем ставку размещения для печати как ставка размещения + индикативная ставка 
	 * @param indicative индикативная ставка
	 */
	public void setRate4Print(String indicative) {
		if (indicative == null || indicative.isEmpty()) this.rate4Print = Formatter.toMoneyFormat(rate4);
		else this.rate4Print =  indicative + " + " + Formatter.toMoneyFormat(rate4); 
	}
	public String getIndcondition() {
		return indcondition;
	}
	public void setIndcondition(String indcondition) {
		this.indcondition = indcondition;
	}
}

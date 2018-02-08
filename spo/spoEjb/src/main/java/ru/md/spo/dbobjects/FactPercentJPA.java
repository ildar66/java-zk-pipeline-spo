package ru.md.spo.dbobjects;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.vtb.util.CollectionUtils;
import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.md.dict.dbobjects.RiskStepupFactorJPA;

import com.vtb.domain.VtbObject;
import com.vtb.util.Formatter;

@Entity
@Table(name = "factpercent")
public class FactPercentJPA extends VtbObject {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "FactPercentSequenceGenerator", sequenceName = "factpercent_seq", allocationSize = 1)
    @GeneratedValue(generator = "FactPercentSequenceGenerator", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne 
    @JoinColumn(name="id_mdtask")
    private TaskJPA task;
    
    private Double riskpremium;//Премия за кредитный риск
    @ManyToOne @JoinColumn(name="riskpremiumtype")
    private CdRiskpremiumJPA riskpremiumtype;
    private Double riskpremium_change;
    private Date end_date;//период по
    private Date start_date;//период с
    private String manual_fondrate; //ручная Ставка фондирования
    private Double fondrate; //Ставка фондирования
    private Double rate3; //Плата за экономический капитал
    private Double rate4; //Ставка размещения
    private String rate4Desc; //Ставка размещения
    private Double rate11; //КТР
    private Double effrate; //Эффективная ставка для траншей
    private String supply; //Обеспечение по периоду
    private String indcondition; //индивидуальные условия
    @ManyToOne @JoinColumn(name="riskStepupFactor")
    private RiskStepupFactorJPA riskStepupFactor;//Повыш. коэфф. за риск
    //информация из рейтингов. Временно ведем это в СПО
    private Double rating_fondrate;//Ставка фондирования рейтингов
    private Double rating_riskpremium;//Премия за кредитный риск для рейтингов
    private Double rating_rate3;//Плата за экономический капитал для рейтингов
    private Double rating_c1;//Коэффициент С1 для рейтингов
    private Double rating_c2;//Коэффициент С2 для рейтингов
    private Double rating_calc;//Расчетная ставка для рейтингов
    private Double rating_ktr;//Расчетная ставка для рейтингов
    @ManyToOne @JoinColumn(name="rating_riskStepupFactor")
    private RiskStepupFactorJPA rating_riskStepupFactor;//Повыш. коэфф. за риск
    @ManyToOne @JoinColumn(name="premiumtype")
    private CdPremiumTypeJPA premiumType;
    private Double premiumvalue;//Вознаграждения величина
    private String premiumcurr;//Вознаграждения валюта
    private String premiumtext;//Вознаграждения формула
    //Только для траншей
    @ManyToOne 
    @JoinColumn(name="tranceId")
    private TranceJPA trance;//для какого транша
    private Double rate5; //Компенсирующий спрэд за фиксацию процентной ставки
    private Double rate6; //Компенсирующий спрэд за досрочное погашение
    private Double rate9; //Комиссия за выдачу
    private Double rate10; //Комиссия за сопровождение

    @Column(name = "interest_rate_fixed")
    private Boolean interestRateFixed = false;
    @Column(name = "interest_rate_derivative")
    private Boolean interestRateDerivative = false;

    private Date usefrom;//Применяется с

    private String reason;//Основание

    /**Расчетная ставка*/
    public Double getCalcRate1(){
        /*Рассчитывается по формуле: Ставка фондирования + Премия за кредитный риск + 
        Компенсирующий спрэд за фиксацию процентной ставки + Компенсирующий спрэд за досрочное погашение + 
        Покрытие прямых расходов + Покрытие общебанковских расходов + Плата за экономический капитал*/
        Double res = getCalcRate2();
        if(rate3!=null) res +=rate3;
        if(getTask()!=null && getTask().getRate8()!=null) res +=getTask().getRate8();
        if(getRiskStepupFactorValue()!=null) res += getRiskStepupFactorValue();
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
        
        if(getRate5()==null){
            if(getTask()!=null&&getTask().getRate5()!=null) res +=getTask().getRate5();
        } else 
            res +=getRate5();
        
        if(getRate6()==null){
            if(getTask()!=null&&getTask().getRate6()!=null) res +=getTask().getRate6();
        } else
            res +=getRate6();
        
        if(getTask()!=null&&getTask().getRate7()!=null) res +=getTask().getRate7();
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
        if (rate4 != null ) {
        	res += rate4;
        }
        calc3 = res;
        return res;        
    }
    
    /**Эффективная ставка*/
    @Transient
    public Double getCalcRate3(Double comissionSum){
        Double res = 0.0;
        if (rate4 != null ) {
        	res += rate4;
        }
        LOGGER.info("comissionSum=" + comissionSum);
        if (comissionSum != null) {
        	res += comissionSum;
        }
        calc3 = res;
        return res;        
    }
    
    @SuppressWarnings("unused") @Transient
    private Double calc1;
    @SuppressWarnings("unused") @Transient
    private Double calc2;
    @SuppressWarnings("unused") @Transient
    private Double calc3;
    @Transient
    private Double riskStepupFactorValue;
    
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

    
    /**
     * Возвращает {@link TaskJPA }
     * 
     * @return
     */

    public TaskJPA getTask() {
        return task;
    }
    
    /**
     * Устанавливает {@link TaskJPA }
     * 
     * @param task
     */
    public void setTask(TaskJPA task) {
        this.task = task;
    }

    public Double getRiskpremium() {
        return riskpremium;
    }

    public void setRiskpremium(Double riskpremium) {
        this.riskpremium = riskpremium;
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
    public CdRiskpremiumJPA getRiskpremiumtype() {
        return riskpremiumtype;
    }
    public void setRiskpremiumtype(CdRiskpremiumJPA riskpremiumtype) {
        this.riskpremiumtype = riskpremiumtype;
    }
    public Double getRiskpremium_change() {
        return riskpremium_change;
    }
    public void setRiskpremium_change(Double riskpremium_change) {
        this.riskpremium_change = riskpremium_change;
    }
    public String getRiskpremiumTypeDisplay() {
        if(riskpremiumtype==null)
            return "не выбрана";
        return riskpremiumtype.getDescription();
    }
    public Long getTranceId() {
        return trance==null?null:getTrance().getId();
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
    public Double getRating_c1() {
        return rating_c1;
    }
    public void setRating_c1(Double rating_с1) {
        this.rating_c1 = rating_с1;
    }
    public Double getRating_c2() {
        return rating_c2;
    }
    public void setRating_c2(Double rating_с2) {
        this.rating_c2 = rating_с2;
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
    public CdPremiumTypeJPA getPremiumType() {
        return premiumType;
    }
    public void setPremiumType(CdPremiumTypeJPA premiumType) {
        this.premiumType = premiumType;
    }
    public Double getPremiumvalue() {
        return premiumvalue;
    }
    public void setPremiumvalue(Double premiumvalue) {
        this.premiumvalue = premiumvalue;
    }
    public String getPremiumcurr() {
        return premiumcurr;
    }
    public void setPremiumcurr(String premiumcurr) {
        this.premiumcurr = premiumcurr;
    }
    public String getPremiumtext() {
        if(premiumtext==null)
            return "";
        return premiumtext;
    }
    public void setPremiumtext(String premiumtext) {
        this.premiumtext = premiumtext;
    }
    /**поле Размер вознаграждения*/
    public String getPremiumSizeDisplay(){
        if(premiumType==null)
            return "";
        String type = premiumType.getValue();
        if("Валюта".equals(type) || "Валюта/ %".equals(type)){
            return Formatter.format(premiumvalue)+" "+premiumcurr;
        }
        if("Формула".equals(type)){
            return getPremiumtext();
        }
        return type;

    }
    /**поле Вознаграждения*/
    public String getPremiumTypeDisplay() {
        if(premiumType==null)
            return "";
        return premiumType.getPremium_name();
    }
    public Double getEffrate() {
        return effrate;
    }
    public void setEffrate(Double effrate) {
        this.effrate = effrate;
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
	@Override
	public String toString() {
		return "FactPercentJPA [id=" + id + ", end_date=" + end_date
				+ ", start_date=" + start_date + "]";
	}
	public TranceJPA getTrance() {
		return trance;
	}
	public void setTrance(TranceJPA trance) {
		this.trance = trance;
	}
	public RiskStepupFactorJPA getRiskStepupFactor() {
		return riskStepupFactor;
	}
	public String getRiskStepupFactorID() {
		if(riskStepupFactor==null)
			return "";
		return riskStepupFactor.getItem_id();
	}
	public String getrating_RiskStepupFactorID() {
		if(rating_riskStepupFactor==null)
			return "";
		return rating_riskStepupFactor.getItem_id();
	}
	public void setRiskStepupFactor(RiskStepupFactorJPA riskStepupFactor) {
		this.riskStepupFactor = riskStepupFactor;
	}
	public void setRiskStepupFactorValue(Double riskStepupFactor) {
        this.riskStepupFactorValue = riskStepupFactor;
    }
	public Double getRiskStepupFactorValue() {
		if(riskStepupFactorValue==null && getRiskStepupFactor()!=null)
			return Formatter.parseDouble(getRiskStepupFactor().getText());
		return riskStepupFactorValue;
	}
	public boolean isManualFondrate() {
		return manual_fondrate==null || manual_fondrate.equalsIgnoreCase("y");
	}
	public void setManual_fondrate(String manual_fondrate) {
		this.manual_fondrate = manual_fondrate;
	}
	public String getIndcondition() {
		if(indcondition==null)
            return "";
        return indcondition;
	}
	public void setIndcondition(String indcondition) {
		this.indcondition = indcondition;
	}
	public String getRate4Desc() {
		return rate4Desc==null?"":rate4Desc;
	}
	public void setRate4Desc(String rate4Desc) {
		this.rate4Desc = rate4Desc;
	}

    public boolean isInterestRateFixed() {
        if(interestRateFixed==null)
            return false;
        return interestRateFixed;
    }

    public void setInterestRateFixed(Boolean interestRateFixed) {
        this.interestRateFixed = interestRateFixed;
    }

    public boolean isInterestRateDerivative() {
        if(interestRateDerivative==null)
            return false;
        return interestRateDerivative;
    }

    public void setInterestRateDerivative(Boolean interestRateDerivative) {
        this.interestRateDerivative = interestRateDerivative;
    }

    public Date getUsefrom() {
        return usefrom;
    }

    public void setUsefrom(Date usefrom) {
        this.usefrom = usefrom;
    }

    public String getReason() {
        return Formatter.str(reason);
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Возвращает тип ставки (для freemaker).
     * @return <code>true</code> если ставка фиксированная, иначе плавающая
     */
    public String getFixedRateDisplay() {
        String res = "";
        if(isInterestRateFixed())
            res += "фиксированная";
        if(isInterestRateDerivative()){
            if(!res.isEmpty())
                res += ", ";
            res += "плавающая";
        }
        return res;
    }

    @Override
    public int hashCode() {
        int result = riskpremium != null ? riskpremium.hashCode() : 0;
        result = 31 * result + (riskpremium_change != null ? riskpremium_change.hashCode() : 0);
        result = 31 * result + (manual_fondrate != null ? manual_fondrate.hashCode() : 0);
        result = 31 * result + (fondrate != null ? fondrate.hashCode() : 0);
        result = 31 * result + (rate3 != null ? rate3.hashCode() : 0);
        result = 31 * result + (rate4 != null ? rate4.hashCode() : 0);
        result = 31 * result + (rate4Desc != null ? rate4Desc.hashCode() : 0);
        result = 31 * result + (rate11 != null ? rate11.hashCode() : 0);
        result = 31 * result + (supply != null ? supply.hashCode() : 0);
        result = 31 * result + (rating_fondrate != null ? rating_fondrate.hashCode() : 0);
        result = 31 * result + (rating_riskpremium != null ? rating_riskpremium.hashCode() : 0);
        result = 31 * result + (premiumvalue != null ? premiumvalue.hashCode() : 0);
        result = 31 * result + (premiumcurr != null ? premiumcurr.hashCode() : 0);
        result = 31 * result + (premiumtext != null ? premiumtext.hashCode() : 0);
        result = 31 * result + (rate5 != null ? rate5.hashCode() : 0);
        result = 31 * result + (rate6 != null ? rate6.hashCode() : 0);
        result = 31 * result + (rate9 != null ? rate9.hashCode() : 0);
        result = 31 * result + (rate10 != null ? rate10.hashCode() : 0);
        result = 31 * result + (usefrom != null ? usefrom.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (riskStepupFactorValue != null ? riskStepupFactorValue.hashCode() : 0);
        return result;
    }
}

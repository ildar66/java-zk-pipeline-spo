package com.vtb.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import ru.masterdm.compendium.domain.Currency;
import ru.md.domain.Withdraw;

import com.vtb.util.CollectionUtils;

public class Trance extends VtbObject{
    private static final long serialVersionUID = -2237896314262357253L;
    public static final Set<String> TranceComment = 
        CollectionUtils.set(
                "Допускается использование транша несколькими суммами", 
                "Не допускается использование транша несколькими суммами", 
                "Допускается присоединение неиспользованной суммы транша к следующему траншу",
                "Не допускается присоединение неиспользованной суммы транша к следующему траншу"
                );
    private BigDecimal sum;
    private Long id;     // чтобы пронумеровать транши
    private Currency currency;
    private Date usedatefrom;//дата начала использования лимита, сделки
    private Date usedate;    //дата окончания использования лимита, сделки
    private List<Withdraw> withdraws;
    
    public Trance() {
        super();
    }
    
    
    public Trance(Long id, BigDecimal sum, Currency currency, Date usedatefrom, Date usedate) {
        super();
        this.id = id;
        this.sum = sum;
        this.currency = currency;
        this.usedatefrom = usedatefrom;
        this.usedate = usedate;
    }
    
    /**
     * @return the sum
     */
    public BigDecimal getSum() {
        return sum;
    }
    
    /**
     * @param sum the sum to set
     */
    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
    
    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }
    
    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    
    /**
     * @return the usedatefrom
     */
    public Date getUsedatefrom() {
        return usedatefrom;
    }
    
    /**
     * @param usedatefrom the usedatefrom to set
     */
    public void setUsedatefrom(Date usedatefrom) {
        this.usedatefrom = usedatefrom;
    }
    
    /**
     * @return the usedate
     */
    public Date getUsedate() {
        return usedate;
    }

    /**
     * @param usedate the usedate to set
     */
    public void setUsedate(Date usedate) {
        this.usedate = usedate;
    }

    public String toString(){
        return this.getSum().toString()+" "+this.getCurrency().getCode();
    }
    
    public String getIdStr() {
    	if(id==null)
    		return "";
    	return id.toString();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


	public List<Withdraw> getWithdraws() {
		return withdraws;
	}


	public void setWithdraws(List<Withdraw> withdraws) {
		this.withdraws = withdraws;
	}

    @Override
    public int hashCode() {
        int result = sum != null ? sum.hashCode() : 0;
        result = 31 * result + (currency != null && currency.getCode()!=null ? currency.getCode().hashCode() : 0);
        result = 31 * result + (usedatefrom != null ? usedatefrom.hashCode() : 0);
        result = 31 * result + (usedate != null ? usedate.hashCode() : 0);
        return result;
    }
}

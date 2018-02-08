package ru.md.crm.dbobjects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name="V_SPO_FB_LIMIT",schema="sysdba")
public class LimitJPA implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="FB_LIMITID")
    private String id;//id
    
    @ManyToMany(fetch=FetchType.LAZY) @JoinTable(name="V_SPO_FB_LIMIT_ACCOUNT",schema="sysdba",
            joinColumns = @JoinColumn(name = "FB_LIMITID"),
            inverseJoinColumns = @JoinColumn(name = "ACCOUNTID"))
    private List<AccountJPA> accounts;
    
    @Column(name="LIMIT_NAME")
    private String limit_name;//Наименование
    
    @Column(name="CURR")
    private String curr;//Валюта
    
    @Column(name="ACT_BEGIN")
    private Date act_begin;//Дата начала действия
    
    @Column(name="ACT_END")
    private Date act_end;//Дата окончания действия
    
    @Column(name="USE_BEGIN")
    private Date use_begin;//Дата начала использования
    
    @Column(name="USE_END")
    private Date use_end;//Дата окончания использования
    
    @Column(name="CURRS")
    private String currs;//Перечень валют, допустимых в рамках лимита/сублимита
    
    @Column(name="USE_SROK")
    private String use_srok;//Период использования лимита
    
    @Column(name="LIMITID")
    private String limitid;//ID вышестоящего лимита/сублимита
    
    @Column(name="LIMITNO")
    private String limitno;//Номер
    
    @Column(name="LIMIT_VID")
    private String limit_vid;//Вид лимита
    
    @Column(name="STATUS")
    private String status;//Статус лимита
    
    @ManyToOne
    @JoinColumn(name="MANAGERID")
    private UserInfoJPA manager;//менеджер
        
    @Column(name="PARENTID")
    private String parentid;//ID родителя
    
    @Column(name="CODE")
    private String code;//Код лимита/сублимита

    @Column(name="SUMMA")
    private BigDecimal sum;
    
    public String toString(){
        return code;
    }
    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return limit_name
     */
    public String getLimit_name() {
        return limit_name;
    }

    /**
     * @param limit_name limit_name
     */
    public void setLimit_name(String limit_name) {
        this.limit_name = limit_name;
    }

    /**
     * @return curr
     */
    public String getCurr() {
        return curr;
    }

    /**
     * @param curr curr
     */
    public void setCurr(String curr) {
        this.curr = curr;
    }

    /**
     * @return act_begin
     */
    public Date getAct_begin() {
        return act_begin;
    }

    /**
     * @param act_begin act_begin
     */
    public void setAct_begin(Date act_begin) {
        this.act_begin = act_begin;
    }

    /**
     * @return act_end
     */
    public Date getAct_end() {
        return act_end;
    }

    /**
     * @param act_end act_end
     */
    public void setAct_end(Date act_end) {
        this.act_end = act_end;
    }

    /**
     * @return use_begin
     */
    public Date getUse_begin() {
        return use_begin;
    }

    /**
     * @param use_begin use_begin
     */
    public void setUse_begin(Date use_begin) {
        this.use_begin = use_begin;
    }

    /**
     * @return use_end
     */
    public Date getUse_end() {
        return use_end;
    }

    /**
     * @param use_end use_end
     */
    public void setUse_end(Date use_end) {
        this.use_end = use_end;
    }

    /**
     * @return currs
     */
    public String getCurrs() {
        return currs;
    }

    /**
     * @param currs currs
     */
    public void setCurrs(String currs) {
        this.currs = currs;
    }

    /**
     * @return use_srok
     */
    public String getUse_srok() {
        return use_srok;
    }

    /**
     * @param use_srok use_srok
     */
    public void setUse_srok(String use_srok) {
        this.use_srok = use_srok;
    }

    /**
     * @return limitid
     */
    public String getLimitid() {
        return limitid;
    }

    /**
     * @param limitid limitid
     */
    public void setLimitid(String limitid) {
        this.limitid = limitid;
    }

    /**
     * @return limitno
     */
    public String getLimitno() {
        return limitno;
    }

    /**
     * @param limitno limitno
     */
    public void setLimitno(String limitno) {
        this.limitno = limitno;
    }

    /**
     * @return limit_vid
     */
    public String getLimit_vid() {
        return limit_vid;
    }

    /**
     * @param limit_vid limit_vid
     */
    public void setLimit_vid(String limit_vid) {
        this.limit_vid = limit_vid;
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public UserInfoJPA getManager() {
        return manager;
    }

    public void setManager(UserInfoJPA manager) {
        this.manager = manager;
    }

    /**
     * @return parentid
     */
    public String getParentid() {
        return parentid;
    }

    /**
     * @param parentid parentid
     */
    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code code
     */
    public void setCode(String code) {
        this.code = code;
    }
    /**
     * @return sum
     */
    public BigDecimal getSum() {
        return sum;
    }
    /**
     * @param sum sum
     */
    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
    public String getAccountsName(){
        String res = "";
        for(AccountJPA a : accounts){
            res += a.getName() + "<br />";
        }
        return res;
    }
    /**
     * @return accounts
     */
    public List<AccountJPA> getAccounts() {
        return accounts;
    }
    /**
     * @param accounts accounts
     */
    public void setAccounts(List<AccountJPA> accounts) {
        this.accounts = accounts;
    }
    
}

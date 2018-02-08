package ru.md.spo.dbobjects;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vtb.util.Formatter;

/**
 * pipeline.
 * @author Andrey Pavlenko
 *
 */
@Entity
@Table(name = "pipeline")
public class PipelineJPA {
    @Id
    private Long id_mdtask;
    @Temporal(TemporalType.TIMESTAMP)
	public Date plan_date;//Плановая  Дата  Выборки
    private String status;//Статус Сделки
	private Double close_probability;//Вероятность Закрытия
    private String status_manual;//вероятность закрытия введена вручную
    private String law;//Применимое Право
    private String geography;//География
    private String supply;//Обеспечение
    private String description;//Описание Сделки, Включая Структуру, Деривативы и т.д.
    private String cmnt;//Комментарии по Статусу Сделки, Следующие Шаги
    private String addition_business;//Дополнительный Бизнес, Сроки, Примерный Объём в млн. дол. США
    private String syndication;//Возможность Синдикации
    private String syndication_cmnt;//Комментарии по Синдикации
	private Double wal;//Средневзвешенный Срок Погашения (WAL)
    private Double hurdle_rate;//Минимальная Ставка (Hurdle Rate) 
	private Double markup;//Маркап
	private Double margin;
	private Double pc_cash;//PCs: Кеш, млн. дол. США
	private Double pc_res;//PCs: Резервы, млн. дол. США
	private Double pc_der;//PCs: Деривативы, млн. дол. США
	private Double pc_total;//PCs: Всего, млн. дол. США
	private Double line_count;//Выбранный Объём Линии, млн. дол. США
    private String pub;//Публичная Сделка
    private String priority;//Приоритет Менеджмента
    private String new_client;//Новый Клиент
    private String flow_investment;//Сделка Flow / Investment
    private Integer trade_finance;//Сделка Flow / Investment
    private String rating;//Рейтинг Клиента
	private Double factor_product_type;//Коэффициент Типа Сделки
	private Double factor_period;//Коэффициент по Сроку Погашения
    private String contractor;//Фондирующая Компания
    private String vtb_contractor;//Контрагент со стороны Группы ВТБ
    private String trade_desc;//Трейдинг Деск
    private String prolongation;//Пролонгация
    private String hideinreport;//не показывать в отчете
    private String hideinreporttraders;//не показывать в отчете

	public PipelineJPA(Long id_mdtask) {
		super();
		this.id_mdtask = id_mdtask;
	}
	public Long getId_mdtask() {
		return id_mdtask;
	}
	public void setId_mdtask(Long id_mdtask) {
		this.id_mdtask = id_mdtask;
	}
	public String getPlan_date() {
		return Formatter.format(plan_date);
	}
	public void setPlan_date(Date plan_date) {
		this.plan_date = plan_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getClose_probability() {
		if (close_probability == null)
			return "";
		String s = Formatter.format(close_probability.longValue());
		return s;
	}
	public void setClose_probability(Double close_probability) {
		this.close_probability = close_probability;
	}
	public String getLaw() {
		return law;
	}
	public void setLaw(String law) {
		this.law = law;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	public String getSupply() {
		if(supply==null)
			return "";
		return supply;
	}
	public void setSupply(String supply) {
		this.supply = supply;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCmnt() {
		return cmnt;
	}
	public void setCmnt(String cmnt) {
		this.cmnt = cmnt;
	}
	public String getAddition_business() {
		return addition_business;
	}
	public void setAddition_business(String addition_business) {
		this.addition_business = addition_business;
	}
	public String getSyndication() {
		if(syndication==null)
			return "";
		return syndication;
	}
	public void setSyndication(String syndication) {
		this.syndication = syndication;
	}
	public String getSyndication_cmnt() {
		return syndication_cmnt;
	}
	public void setSyndication_cmnt(String syndication_cmnt) {
		this.syndication_cmnt = syndication_cmnt;
	}
	public String getWal() {
		if(wal==null)
			return "";
		return Formatter.format2point(wal);
	}
	public void setWal(Double wal) {
		this.wal = wal;
	}
	public Double getHurdleRateValue() {
		return hurdle_rate;
	}
	public String getHurdle_rate() {
		return Formatter.format3point(hurdle_rate);
	}
	public void setHurdle_rate(Double hurdle_rate) {
		this.hurdle_rate = hurdle_rate;
	}
	public String getMarkup() {
		return Formatter.format3point(markup);
	}
	public void setMarkup(Double markup) {
		this.markup = markup;
	}
	public String getPc_cash() {
		return Formatter.format1point(pc_cash);
	}
	public void setPc_cash(Double pc_cash) {
		this.pc_cash = pc_cash;
	}
	public String getPc_res() {
		return Formatter.format1point(pc_res);
	}
	public void setPc_res(Double pc_res) {
		this.pc_res = pc_res;
	}
	public String getPc_der() {
		return Formatter.format1point(pc_der);
	}
	public void setPc_der(Double pc_der) {
		this.pc_der = pc_der;
	}
	public String getPc_total() {
		return Formatter.format1point(pc_total);
	}
	public void setPc_total(Double pc_total) {
		this.pc_total = pc_total;
	}
	public String getLine_count() {
		return Formatter.format1point(line_count);
	}
	public void setLine_count(Double line_count) {
		this.line_count = line_count;
	}
	public String getPub() {
		if(pub==null)
			return "";
		return pub;
	}
	public void setPub(String pub) {
		this.pub = pub;
	}
	public String getPriority() {
		if(priority==null)
			return "";
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getNew_client() {
		if(new_client==null)
			return "";
		return new_client;
	}
	public void setNew_client(String new_client) {
		this.new_client = new_client;
	}
	public String getFlow_investment() {
		if(flow_investment==null)
			return "";
		return flow_investment;
	}
	public void setFlow_investment(String flow_investment) {
		this.flow_investment = flow_investment;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getFactor_product_type() {
		String s = Formatter.format2point(factor_product_type);
		if(s.endsWith(",00"))
			return s.replaceAll(",00","");
		return s;
	}
	public void setFactor_product_type(Double factor_product_type) {
		this.factor_product_type = factor_product_type;
	}
	public String getFactor_period() {
		String s = Formatter.format2point(factor_period);
		if(s.endsWith(",00"))
			return s.replaceAll(",00","");
		return s;
	}
	public void setFactor_period(Double factor_period) {
		this.factor_period = factor_period;
	}
	public String getContractor() {
		return contractor;
	}
	public void setContractor(String contractor) {
		this.contractor = contractor;
	}
	public String getVtb_contractor() {
		return vtb_contractor;
	}
	public void setVtb_contractor(String vtb_contractor) {
		this.vtb_contractor = vtb_contractor;
	}
	public String getTrade_desc() {
		return trade_desc;
	}
	public void setTrade_desc(String trade_desc) {
		this.trade_desc = trade_desc;
	}
	public String getProlongation() {
		if(prolongation==null)
			return "";
		return prolongation;
	}
	public void setProlongation(String prolongation) {
		this.prolongation = prolongation;
	}
	public String getHideinreport() {
		if(hideinreport==null)
			return "";
		return hideinreport;
	}
	public void setHideinreport(String hideinreport) {
		this.hideinreport = hideinreport;
	}
	public PipelineJPA() {
		super();
		geography="Россия";
	}
//	public boolean isStatusManual() {
//		return status_manual==null || status_manual.equalsIgnoreCase("y");
//	}
	public void setStatusManual(String close_probability_is_manual) {
		this.status_manual = close_probability_is_manual;
	}
	public String getStatusManual() {
		return status_manual==null?"n":status_manual;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Double getMargin() {
		return margin;
	}

	/**
	 * Sets .
	 * @param margin
	 */
	public void setMargin(Double margin) {
		this.margin = margin;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Integer getTrade_finance() {
		return trade_finance;
	}

	/**
	 * Sets .
	 * @param trade_finance
	 */
	public void setTrade_finance(Integer trade_finance) {
		this.trade_finance = trade_finance;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getHideinreporttraders() {
		return hideinreporttraders;
	}

	/**
	 * Sets .
	 * @param hideinreporttraders
	 */
	public void setHideinreporttraders(String hideinreporttraders) {
		this.hideinreporttraders = hideinreporttraders;
	}
}

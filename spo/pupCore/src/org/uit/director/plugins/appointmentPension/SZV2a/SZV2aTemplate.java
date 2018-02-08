/*
 * Created on 26.03.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.uit.director.plugins.appointmentPension.SZV2a;

import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class SZV2aTemplate {


	public SZV2aTemplate() {
		
	}
	
	//	Запись об органе, осуществляющем пенсионное обеспечение
	String sourceRegNumberPFR;

	long sourceINN = 0;

	long sourceKPP = 0;

	String sourceName;

	//	Запись о пачке
	String packetNumber;

	Date dateCreateList;

	int packetTypeNumber;

	int period = 0;

	String codeCategory = "";

	String codeComplementary = "";

	String territoryCondition = "";

	String typeData = "";

	String typeCorrictions = "";

	int reportPeriod = 0;

	int year = 0;

	float allAdd = 0;

	float dole = 0;

	float addStrah = 0;

	float addHoarder = 0;

	float addComplementary = 0;

	int externalCode = 0;

	int externalNumber = 0;

	//	Запись о типе документа
	int countDocs;

	//	Запись запроса выписки из лицевого счета
	int numberDocInPacket;

	int type_request;

	String insNumber;

	String name;

	String family;

	String patronymic;

	String numberMagazine;

	Date dateCreateRequest;

	float RPK;

	int numberRecords;

	//	Запись о пенсионных действиях
	int typePensAction;

	Date datePensAction;

	int typePension;

	float summStrah = 0;

	int indicateCancel = 0;

	String typeCancel = "";

	float numbCancel = 0;

	float summ = 0;

	/**
	 * @return Returns the addComplementary.
	 */
	public float getAddComplementary() {
		return addComplementary;
	}

	/**
	 * @param addComplementary
	 *            The addComplementary to set.
	 */
	public void setAddComplementary(float addComplementary) {
		this.addComplementary = addComplementary;
	}

	/**
	 * @return Returns the addHoarder.
	 */
	public float getAddHoarder() {
		return addHoarder;
	}

	/**
	 * @param addHoarder
	 *            The addHoarder to set.
	 */
	public void setAddHoarder(float addHoarder) {
		this.addHoarder = addHoarder;
	}

	/**
	 * @return Returns the addStrah.
	 */
	public float getAddStrah() {
		return addStrah;
	}

	/**
	 * @param addStrah
	 *            The addStrah to set.
	 */
	public void setAddStrah(float addStrah) {
		this.addStrah = addStrah;
	}

	/**
	 * @return Returns the allAdd.
	 */
	public float getAllAdd() {
		return allAdd;
	}

	/**
	 * @param allAdd
	 *            The allAdd to set.
	 */
	public void setAllAdd(float allAdd) {
		this.allAdd = allAdd;
	}

	/**
	 * @return Returns the codeCategory.
	 */
	public String getCodeCategory() {
		return codeCategory;
	}

	/**
	 * @param codeCategory
	 *            The codeCategory to set.
	 */
	public void setCodeCategory(String codeCategory) {
		this.codeCategory = codeCategory;
	}

	/**
	 * @return Returns the codeComplementary.
	 */
	public String getCodeComplementary() {
		return codeComplementary;
	}

	/**
	 * @param codeComplementary
	 *            The codeComplementary to set.
	 */
	public void setCodeComplementary(String codeComplementary) {
		this.codeComplementary = codeComplementary;
	}

	/**
	 * @return Returns the countDocs.
	 */
	public int getCountDocs() {
		return countDocs;
	}

	/**
	 * @param countDocs
	 *            The countDocs to set.
	 */
	public void setCountDocs(int countDocs) {
		this.countDocs = countDocs;
	}

	/**
	 * @return Returns the dateCreateList.
	 */
	public Date getDateCreateList() {
		return dateCreateList;
	}

	/**
	 * @param dateCreateList
	 *            The dateCreateList to set.
	 */
	public void setDateCreateList(Date dateCreateList) {
		this.dateCreateList = dateCreateList;
	}

	/**
	 * @return Returns the dateCreateRequest.
	 */
	public Date getDateCreateRequest() {
		return dateCreateRequest;
	}

	/**
	 * @param dateCreateRequest
	 *            The dateCreateRequest to set.
	 */
	public void setDateCreateRequest(Date dateCreateRequest) {
		this.dateCreateRequest = dateCreateRequest;
	}

	/**
	 * @return Returns the dole.
	 */
	public float getDole() {
		return dole;
	}

	/**
	 * @param dole
	 *            The dole to set.
	 */
	public void setDole(float dole) {
		this.dole = dole;
	}

	/**
	 * @return Returns the externalCode.
	 */
	public int getExternalCode() {
		return externalCode;
	}

	/**
	 * @param externalCode
	 *            The externalCode to set.
	 */
	public void setExternalCode(int externalCode) {
		this.externalCode = externalCode;
	}

	/**
	 * @return Returns the externalNumber.
	 */
	public int getExternalNumber() {
		return externalNumber;
	}

	/**
	 * @param externalNumber
	 *            The externalNumber to set.
	 */
	public void setExternalNumber(int externalNumber) {
		this.externalNumber = externalNumber;
	}

	/**
	 * @return Returns the family.
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @param family
	 *            The family to set.
	 */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * @return Returns the insNumber.
	 */
	public String getInsNumber() {
		return insNumber;
	}

	/**
	 * @param insNumber
	 *            The insNumber to set.
	 */
	public void setInsNumber(String insNumber) {
		this.insNumber = insNumber;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the numberDocInPacket.
	 */
	public int getNumberDocInPacket() {
		return numberDocInPacket;
	}

	/**
	 * @param numberDocInPacket
	 *            The numberDocInPacket to set.
	 */
	public void setNumberDocInPacket(int numberDocInPacket) {
		this.numberDocInPacket = numberDocInPacket;
	}

	/**
	 * @return Returns the numberMagazine.
	 */
	public String getNumberMagazine() {
		return numberMagazine;
	}

	/**
	 * @param numberMagazine
	 *            The numberMagazine to set.
	 */
	public void setNumberMagazine(long numberMagazine) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		
		this.numberMagazine = sdf.format(new Date()) + String.valueOf(numberMagazine);
	}

	/**
	 * @return Returns the numberRecords.
	 */
	public int getNumberRecords() {
		return numberRecords;
	}

	/**
	 * @param numberRecords
	 *            The numberRecords to set.
	 */
	public void setNumberRecords(int numberRecords) {
		this.numberRecords = numberRecords;
	}

	/**
	 * @return Returns the packetNumber.
	 */
	public String getPacketNumber() {
		return packetNumber;
	}

	/**
	 * @param packetNumber
	 *            The packetNumber to set.
	 */
	public void setPacketNumber(String packetNumber) {
		this.packetNumber = packetNumber;
	}

	/**
	 * @return Returns the packetTypeNumber.
	 */
	public int getPacketTypeNumber() {
		return packetTypeNumber;
	}

	/**
	 * @param packetTypeNumber
	 *            The packetTypeNumber to set.
	 */
	public void setPacketTypeNumber(int packetTypeNumber) {
		this.packetTypeNumber = packetTypeNumber;
	}

	/**
	 * @return Returns the patronymic.
	 */
	public String getPatronymic() {
		return patronymic;
	}

	/**
	 * @param patronymic
	 *            The patronymic to set.
	 */
	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	/**
	 * @return Returns the period.
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * @param period
	 *            The period to set.
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

	/**
	 * @return Returns the reportPeriod.
	 */
	public int getReportPeriod() {
		return reportPeriod;
	}

	/**
	 * @param reportPeriod
	 *            The reportPeriod to set.
	 */
	public void setReportPeriod(int reportPeriod) {
		this.reportPeriod = reportPeriod;
	}

	/**
	 * @return Returns the rPK.
	 */
	public float getRPK() {
		return RPK;
	}

	/**
	 * @param rpk
	 *            The rPK to set.
	 */
	public void setRPK(float rpk) {
		RPK = rpk;
	}

	/**
	 * @return Returns the sourceINN.
	 */
	public long getSourceINN() {
		return sourceINN;
	}

	/**
	 * @param sourceINN
	 *            The sourceINN to set.
	 */
	public void setSourceINN(long sourceINN) {
		this.sourceINN = sourceINN;
	}

	/**
	 * @return Returns the sourceKPP.
	 */
	public long getSourceKPP() {
		return sourceKPP;
	}

	/**
	 * @param sourceKPP
	 *            The sourceKPP to set.
	 */
	public void setSourceKPP(long sourceKPP) {
		this.sourceKPP = sourceKPP;
	}

	/**
	 * @return Returns the sourceName.
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @param sourceName
	 *            The sourceName to set.
	 */
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	/**
	 * @return Returns the sourceRegNumberPFR.
	 */
	public String getSourceRegNumberPFR() {
		return sourceRegNumberPFR;
	}

	/**
	 * @param sourceRegNumberPFR
	 *            The sourceRegNumberPFR to set.
	 */
	public void setSourceRegNumberPFR(String sourceRegNumberPFR) {
		this.sourceRegNumberPFR = sourceRegNumberPFR;
	}

	/**
	 * @return Returns the territoryCondition.
	 */
	public String getTerritoryCondition() {
		return territoryCondition;
	}

	/**
	 * @param territoryCondition
	 *            The territoryCondition to set.
	 */
	public void setTerritoryCondition(String territoryCondition) {
		this.territoryCondition = territoryCondition;
	}

	/**
	 * @return Returns the typeCorrictions.
	 */
	public String getTypeCorrictions() {
		return typeCorrictions;
	}

	/**
	 * @param typeCorrictions
	 *            The typeCorrictions to set.
	 */
	public void setTypeCorrictions(String typeCorrictions) {
		this.typeCorrictions = typeCorrictions;
	}

	/**
	 * @return Returns the typeData.
	 */
	public String getTypeData() {
		return typeData;
	}

	/**
	 * @param typeData
	 *            The typeData to set.
	 */
	public void setTypeData(String typeData) {
		this.typeData = typeData;
	}

	/**
	 * @return Returns the year.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year
	 *            The year to set.
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return Returns the datePensAction.
	 */
	public Date getDatePensAction() {
		return datePensAction;
	}

	/**
	 * @param datePensAction
	 *            The datePensAction to set.
	 */
	public void setDatePensAction(Date datePensAction) {
		this.datePensAction = datePensAction;
	}

	/**
	 * @return Returns the indicateCancel.
	 */
	public int getIndicateCancel() {
		return indicateCancel;
	}

	/**
	 * @param indicateCancel
	 *            The indicateCancel to set.
	 */
	public void setIndicateCancel(int indicateCancel) {
		this.indicateCancel = indicateCancel;
	}

	/**
	 * @return Returns the numbCancel.
	 */
	public float getNumbCancel() {
		return numbCancel;
	}

	/**
	 * @param numbCancel
	 *            The numbCancel to set.
	 */
	public void setNumbCancel(float numbCancel) {
		this.numbCancel = numbCancel;
	}

	/**
	 * @return Returns the summ.
	 */
	public float getSumm() {
		return summ;
	}

	/**
	 * @param summ
	 *            The summ to set.
	 */
	public void setSumm(float summ) {
		this.summ = summ;
	}

	/**
	 * @return Returns the summStrah.
	 */
	public float getSummStrah() {
		return summStrah;
	}

	/**
	 * @param summStrah
	 *            The summStrah to set.
	 */
	public void setSummStrah(float summStrah) {
		this.summStrah = summStrah;
	}

	/**
	 * @return Returns the typeCancel.
	 */
	public String getTypeCancel() {
		return typeCancel;
	}

	/**
	 * @param typeCancel
	 *            The typeCancel to set.
	 */
	public void setTypeCancel(String typeCancel) {
		this.typeCancel = typeCancel;
	}

	/**
	 * @return Returns the typePensAction.
	 */
	public int getTypePensAction() {
		return typePensAction;
	}

	/**
	 * @param typePensAction
	 *            The typePensAction to set.
	 */
	public void setTypePensAction(int typePensAction) {
		this.typePensAction = typePensAction;
	}

	/**
	 * @return Returns the typePension.
	 */
	public int getTypePension() {
		return typePension;
	}

	/**
	 * @param typePension
	 *            The typePension to set.
	 */
	public void setTypePension(int typePension) {
		this.typePension = typePension;
	}

	public abstract int init();

	public abstract String convert();

}

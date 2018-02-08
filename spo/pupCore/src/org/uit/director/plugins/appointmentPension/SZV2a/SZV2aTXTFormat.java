/*
 * Created on 27.03.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.uit.director.plugins.appointmentPension.SZV2a;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;


public class SZV2aTXTFormat extends SZV2aTemplate {
	//	Запись-заголовок
	private final String TYPE_REC_HEAD = "ЗГЛВ";

	private final String FORMAT_VERSION = "П4.10";

	private final String PROGRAM_NAME = "ПТК КС";

	private final String PROGRAM_VERSION = "1.0";

	//	Запись об органе, осуществляющем пенсионное обеспечение
	private final String TYPE_REC_SOURCE = "СОБС";

	private final String SOURCE_DATA = "СОБС";

	//	Запись о пачке
	private final String TYPE_REC_PACKET = "ПАЧК";

	private final String TYPE_PACKET = "ОПИСЬ";

	private final String TYPE_LIST = "ОП61";

	//	Запись о типе документа
	private final String TYPE_REC_DOC = "ТИПД";

	private final String TYPE_DOCUMENT = "ЗВПС";

	//	Запись запроса выписки из лицевого счета
	private final String TYPE_REC_REQUEST = "ЗВПС";

	private final String[] TYPE_REQUEST = { "ПЕНС", "ПРСЧ", "КОРР" };

	//	Запись о пенсионных действиях
	private final String TYPE_REC_ACTION = "ПЕНД";

	private final String[] TYPE_PENS_ACTION = { "ПЕНС", "ПРСЧ", "СТОП" };

	private final String[] TYPE_PENSION = { "СТАР", "ИНВЛ", "СПК" };

	private final String[] INDICATE_CANCSEL = { "НЕТ", "ДА" };

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private final char c = '\"';

	private final char z = ',';

	private final char p1 = 13;
	private final char p2 = 10;
	private final String p = String.valueOf(p1) + String.valueOf(p2);

	/**
	 *  
	 */
	public SZV2aTXTFormat() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pfr.packet.SZV2a.SZV2a#init()
	 */
	@Override
	public int init() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pfr.packet.SZV2a.SZV2a#convert()
	 */
	@Override
	public String convert() {
		StringBuffer sb = new StringBuffer();
		sb.append(getHead()).append(getSource()).append(getRecordPacket())
				.append(getTypeDoc()).append(getRequest()).append(
						getPensAction());

		return sb.toString();
	}

	/**
	 * @return
	 */
	private String getPensAction() {
		StringBuffer sb = new StringBuffer();
		sb.append(c).append(TYPE_REC_ACTION).append(c).append(z).append(c)
				.append(TYPE_PENS_ACTION[typePensAction]).append(c).append(z)
				.append(c).append(
						datePensAction == null ? "" : dateFormat
								.format(datePensAction)).append(c).append(z)
				.append(c).append(TYPE_PENSION[typePension]).append(c)
				.append(z).append(parsDouble(summStrah)).append(z).append(c).append(
						INDICATE_CANCSEL[indicateCancel]).append(c).append(z)
				.append(c).append(typeCancel).append(c).append(z).append(
						parsDouble(numbCancel)).append(z).append(parsDouble(summ)).append(p);
		return sb.toString();
	}

	/**
	 * @return
	 */
	private String getRequest() {
		StringBuffer sb = new StringBuffer();
		sb.append(c).append(TYPE_REC_REQUEST).append(c).append(z).append(
				numberDocInPacket).append(z).append(c).append(TYPE_REQUEST[type_request]).append(c).append(z).
				append(c).append(insNumber)
				.append(c).append(z).append(c).append(family).append(c).append(
						z).append(c).append(name).append(c).append(z).append(c)
				.append(patronymic).append(c).append(z).append(c).append(
						numberMagazine).append(c).append(z).append(c).append(
						dateCreateRequest == null ? "" : dateFormat
								.format(dateCreateRequest)).append(c).append(z)
				.append(parsDouble(RPK)).append(z).append(numberRecords).append(p);
		return sb.toString();

	}

	/**
	 * @return
	 */
	private String getTypeDoc() {
		StringBuffer sb = new StringBuffer();
		sb.append(c).append(TYPE_REC_DOC).append(c).append(z).append(c).append(
				TYPE_DOCUMENT).append(c).append(z).append(countDocs).append(p);
		return sb.toString();

	}

	/**
	 * @return
	 */
	private String getRecordPacket() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(c).append(TYPE_REC_PACKET).append(c).append(z).append(
				packetNumber).append(z).append(c).append(TYPE_PACKET).append(c)
				.append(z).append(c).append(TYPE_LIST).append(c).append(z)
				.append(c).append(
						dateCreateList == null ? "" : dateFormat
								.format(dateCreateList)).append(c).append(z)
				.append(packetTypeNumber).append(z).append(period)
				.append(z).append(c).append(codeCategory).append(c)
				.append(z).append(c).append(codeComplementary).append(c)
				.append(z).append(c).append(territoryCondition).append(c)
				.append(z).append(c).append(typeData).append(c).append(z)
				.append(c).append(typeCorrictions).append(c).append(z).append(
						reportPeriod).append(z).append(year).append(z).append(
								parsDouble(allAdd)).append(z).append(parsDouble(dole)).append(z).append(
										parsDouble(addStrah)).append(z).append(parsDouble(addHoarder)).
								append(z)
				.append(parsDouble(addComplementary)).append(z).append(externalCode)
				.append(z).append(externalNumber).append(p);
		return sb.toString();

	}
	
	private String parsDouble(double d) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMinimumFractionDigits(2);
		
		String res = nf.format(d);
		res = res.replaceAll(",", ".");
		return res;
		
	}

	/**
	 * @return
	 */
	private String getSource() {
		StringBuffer sb = new StringBuffer();
		sb.append(c).append(TYPE_REC_SOURCE).append(c).append(z).append(c)
				.append(SOURCE_DATA).append(c).append(z).append(c).append(
						sourceRegNumberPFR).append(c).append(z).append(
						sourceINN).append(z).append(sourceKPP).append(z)
				.append(c).append(sourceName).append(c).append(p);
		return sb.toString();

	}

	/**
	 * @return
	 */
	private String getHead() {
		StringBuffer sb = new StringBuffer();
		sb.append(c).append(TYPE_REC_HEAD).append(c).append(z).append(c)
				.append(FORMAT_VERSION).append(c).append(z).append(c).append(
						PROGRAM_NAME).append(c).append(z).append(c).append(
						PROGRAM_VERSION).append(c).append(p);
		return sb.toString();

	}

	public static void main(String[] args) {
		/*SZV2aTXTFormat test = new SZV2aTXTFormat();
		test.setSourceRegNumberPFR("019-004-007829");
		test.setSourceINN(1831078833);
		test.setSourceName("ГУ-УПРАВЛЕНИЕ ПФ РФ В Г,ИЖЕВСКЕ УР");
		test.setPacketNumber(1);
		test.setPacketTypeNumber(1);
		test.setDateCreateList(new Date());*/
		//		test.set
		//		test.set
		//		test.set
		//		test.set
		//		test.set
		//		test.set
		//		test.set
		//		test.set
		//		test.set

//		System.out.println(test.convert());

	}
}

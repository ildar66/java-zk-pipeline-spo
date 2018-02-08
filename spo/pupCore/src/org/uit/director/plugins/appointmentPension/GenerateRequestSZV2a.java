/*
 * Created on 02.04.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.uit.director.plugins.appointmentPension;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.plugins.PluginInterface;


public class GenerateRequestSZV2a implements PluginInterface {

	HttpServletResponse response;

	private WorkflowSessionContext wsc;

	/**
	 * 
	 */
	public GenerateRequestSZV2a() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uit.director.plugins.PluginInterface#init(org.uit.director.contexts.WorkflowSessionContext,
	 *      java.util.List)
	 */
	public void init(WorkflowSessionContext wsc, List params) {
		this.wsc = wsc;
		response = (HttpServletResponse) params.get(3);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.uit.director.plugins.PluginInterface#execute()
	 */
	public String execute() {
		/*SZV2aTXTFormat czv2a = new SZV2aTXTFormat();

		czv2a.setDateCreateList(new Date());
		czv2a.setPacketTypeNumber(1);
		czv2a.setDatePensAction(new Date());
		czv2a.setCountDocs(1);
		czv2a.setNumberDocInPacket(1);
		czv2a.setDateCreateRequest(new Date());

		TaskInfo taskInfo = new TaskCachInfo();
		taskInfo.init(wsc, new Long(wsc.getIdCurrTask()), true);
		taskInfo.execute();
		
		String idReg = taskInfo.getAttributes().findAttributeByName(
		"Номер по журналу").getrValueAttributeString(); 
		if (idReg.equals("")) idReg = "0";
		
		String numbPacketMagazine = "";
		numbPacketMagazine = "00000".substring(0, 5 - idReg.length()) + idReg; 
		
		czv2a.setPacketNumber(numbPacketMagazine);

		String sourceRegNumb = taskInfo.getAttributes().findAttributeByName(
				"Регистрационный номер ПФР").getrValueAttributeString();
		// sourceRegNumb = "123-456-789123";
		czv2a.setSourceRegNumberPFR(sourceRegNumb);

		String inn = (String) taskInfo.getAttributes().findAttributeByName(
				"ИНН").getValueAttributeString();
		czv2a.setSourceINN(inn.equals("") ? 0 : Long.parseLong(inn));
		
		try {
		String kpp = (String) taskInfo.getAttributes().findAttributeByName(
		"КПП").getValueAttributeString();
		czv2a.setSourceKPP(kpp.equals("") ? 0 : Long.parseLong(kpp));
		} catch(Exception e){
			
		}
		czv2a.setSourceName(taskInfo.getAttributes().findAttributeByName(
				"Наименование органа, осуществляющего пенсионное действие")
				.getrValueAttributeString().toUpperCase());*/

		

		/*czv2a.setNumberDocInPacket(1);

		String typPA = taskInfo.getAttributes().findAttributeByName(
				"Тип пенсионного действия").getrValueAttributeString();
		int typePAi = 0;
		if (typPA.equalsIgnoreCase("Перерасчет"))
			typePAi = 1;
		else if (typPA.equalsIgnoreCase("Остановка выплаты"))
			typePAi = 2;

		czv2a.setTypePensAction(typePAi);

		try {
			czv2a.setDatePensAction(WPC.getInstance().dateFormat.parse(taskInfo
					.getAttributes().findAttributeByName(
							"Дата совершения пенсионного действия")
					.getrValueAttributeString()));
		} catch (ParseException e) {
		}

		czv2a
				.setIndicateCancel(taskInfo.getAttributes()
						.findAttributeByName(
								"Признак отказа от страховой части")
						.getrValueAttributeString().equalsIgnoreCase("false") ? 0 : 1);

		czv2a.setName(taskInfo.getAttributes().findAttributeByName("Имя")
				.getrValueAttributeString());
		czv2a.setPatronymic(taskInfo.getAttributes().findAttributeByName(
				"Отчество").getrValueAttributeString());
		czv2a.setFamily(taskInfo.getAttributes().findAttributeByName("Фамилия")
				.getrValueAttributeString());
		czv2a.setInsNumber(taskInfo.getAttributes().findAttributeByName(
				"Страховой номер").getrValueAttributeString());

		czv2a.setNumberMagazine(Long.valueOf(idReg).longValue());
		czv2a.setNumberRecords(1);

		String reqSZV2a = czv2a.convert();

		String fileName = "";
		StringTokenizer tok = new StringTokenizer(sourceRegNumb, "-");
		if (tok.countTokens() == 3) {
			tok.nextToken();
			tok.nextToken();
			fileName = tok.nextToken();
		}*/

	
/*
		fileName += numbPacketMagazine.substring(0, 2) + "."
				+ numbPacketMagazine.substring(2, 5);

		response.setContentType("txt");
		response.setCharacterEncoding("Cp866");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ fileName);

		try {
			PrintWriter writer = response.getWriter();
			writer.write(reqSZV2a);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/

		return null;
	}

}

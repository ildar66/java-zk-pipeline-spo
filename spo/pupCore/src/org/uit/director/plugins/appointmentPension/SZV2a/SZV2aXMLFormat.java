/*
 * Created on 28.03.2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.uit.director.plugins.appointmentPension.SZV2a;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;

import org.apache.crimson.tree.XmlDocument;
import org.w3c.dom.Element;


public class SZV2aXMLFormat extends SZV2aTemplate {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	/**
	 *  
	 */
	public SZV2aXMLFormat() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pfr.packet.SZV2a.SZV2aTemplate#init()
	 */
	@Override
	public int init() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.pfr.packet.SZV2a.SZV2aTemplate#convert()
	 */
	@Override
	public String convert() {
		XmlDocument xml = new XmlDocument();

		Element mainEl = xml.createElement("ФайлПФР");
		xml.appendChild(mainEl);

		Element el = xml.createElement("ИмяФайла");
		el.appendChild(xml.createTextNode("имя файла.xml"));
		mainEl.appendChild(el);

		el = xml.createElement("ЗаголовокФайла");
		Element el2 = xml.createElement("ВерсияФормата");
		el2.appendChild(xml.createTextNode("07.00"));
		el.appendChild(el2);
		el2 = xml.createElement("ТипФайла");
		el2.appendChild(xml.createTextNode("ВНУТРЕННИЙ"));
		el.appendChild(el2);
		el2 = xml.createElement("ПрограммаПодготовкиДанных");
		Element el3 = xml.createElement("НазваниеПрограммы");
		el3.appendChild(xml.createTextNode("ПТК КС"));
		el2.appendChild(el3);
		el3 = xml.createElement("Версия");
		el3.appendChild(xml.createTextNode("1.0"));
		el2.appendChild(el3);
		el.appendChild(el2);
		el2 = xml.createElement("ИсточникДанных");
		el2.appendChild(xml.createTextNode("База ПТК КС"));
		el.appendChild(el2);
		mainEl.appendChild(el);
		el = xml.createElement("ПачкаВходящихДокументов");
		el.setAttribute("Окружение", "В составе выписки");
		el.setAttribute("Стадия", "До обработки");
		el2 = xml.createElement("ВходящаяОпись");
		el3 = xml.createElement("ТипВходящейОписи");
		el3.appendChild(xml.createTextNode("ОПИСЬ ПАЧКИ"));
		el2.appendChild(el3);
		el3 = xml.createElement("СоставительПачки");
		Element el4 = xml.createElement("НалоговыйНомер");
		Element el5 = xml.createElement("ИНН");
		el5.appendChild(xml.createTextNode(sourceINN == 0 ? "" : String
				.valueOf(sourceINN)));
		el4.appendChild(el5);
		el5 = xml.createElement("КПП");
		el5.appendChild(xml.createTextNode(sourceKPP == 0 ? "" : String
				.valueOf(sourceKPP)));
		el4.appendChild(el5);
		el3.appendChild(el4);
		el3.appendChild(xml.createElement("Форма"));
		el4 = xml.createElement("НаименованиеОрганизации");
		el4.appendChild(xml.createTextNode(sourceName == null ? "" : sourceName));
		el3.appendChild(el4);
		el3.appendChild(xml.createElement("НаименованиеКраткое"));
		el4 = xml.createElement("РегистрационныйНомер");
		el4.appendChild(xml.createTextNode(sourceRegNumberPFR == null ? "" : sourceRegNumberPFR));
		el3.appendChild(el4);
		el3.appendChild(xml.createElement("ПодразделениеОрганизации"));
		el2.appendChild(el3);
		
		el3 = xml.createElement("НомерПачки");
		el3.appendChild(xml.createElement("Основной"));
		el3.appendChild(xml.createElement("ПоПодразделению"));		
		el2.appendChild(el3);
		el3 = xml.createElement("СоставДокументов");
		el4 = xml.createElement("Количество");
		el4.appendChild(xml.createTextNode("1"));
		el3.appendChild(el4);
		el4 = xml.createElement("НаличиеДокументов");
		el5 = xml.createElement("ТипДокумента");
		el5.appendChild(xml.createTextNode("Запрос выписки от органа назначения пенсии"));
		el4.appendChild(el5);
		el5 = xml.createElement("Количество");
		el5.appendChild(xml.createTextNode("0"));
		el4.appendChild(el5);
		el3.appendChild(el4);		
		el2.appendChild(el3);
		el3 = xml.createElement("ДатаСоставления");
		el3.appendChild(xml.createTextNode(dateCreateList == null ? "" : dateFormat.format(dateCreateList)));
		el2.appendChild(el3);

		el.appendChild(el2);
		el2 = xml.createElement("ВходящийДокумент");
		el3 = xml.createElement("НомерВпачке");
		el3.appendChild(xml.createTextNode("1"));		
		el2.appendChild(el3);
		el3 = xml.createElement("ЗАПРОС_ВЫПИСКИ_СОБЕСА");
		el4 = xml.createElement("ТипЗапросаВыписки");
		el4.appendChild(xml.createTextNode(String.valueOf(type_request)));
		el3.appendChild(el4);
		el4 = xml.createElement("СтраховойНомер");
		el4.appendChild(xml.createTextNode(insNumber == null ? "" : insNumber));
		el3.appendChild(el4);
		el4 = xml.createElement("ФИО");
		el5 = xml.createElement("Фамилия");
		el5.appendChild(xml.createTextNode(family == null ? "" : family)); 
		el4.appendChild(el5);
		el5 = xml.createElement("Имя");
		el5.appendChild(xml.createTextNode(name == null ? "" : name)); 
		el4.appendChild(el5);
		el5 = xml.createElement("Отчество");
		el5.appendChild(xml.createTextNode(patronymic == null ? "" : patronymic)); 
		el4.appendChild(el5);
		el3.appendChild(el4);
		el4 = xml.createElement("РПКна01.01.2002");
		el4.appendChild(xml.createTextNode(RPK == 0 ? "" : String.valueOf(RPK))); 
		el3.appendChild(el4);
		el4 = xml.createElement("НомерПоЖурналу");
		el4.appendChild(xml.createTextNode(numberMagazine == null ? "" : numberMagazine)); 
		el3.appendChild(el4);
		el4 = xml.createElement("ДатаЗапроса");
		el4.appendChild(xml.createTextNode(dateCreateRequest == null ? "" : dateFormat.format(dateCreateRequest))); 
		el3.appendChild(el4);
		el4 = xml.createElement("ВсеПенсионныеДействия");
		el5 = xml.createElement("Количество");
		el5.appendChild(xml.createTextNode("1")); 
		el4.appendChild(el5);
		el5 = xml.createElement("ПенсионноеДействие");
		el4.appendChild(el5);
		el3.appendChild(el4);
		
		
		el2.appendChild(el3);
		el.appendChild(el2);

		mainEl.appendChild(el);

		return xmlDocumentToString(xml);
	}

	private String xmlDocumentToString(XmlDocument xmlDocument) {
		String res = "";
		try {
			Writer write = new StringWriter();
			xmlDocument.write(write);
			res = write.toString();
			write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;

	}

	public static void main(String[] args) {
		SZV2aXMLFormat xmlF = new SZV2aXMLFormat();
		System.out.print(xmlF.convert());
	}

}

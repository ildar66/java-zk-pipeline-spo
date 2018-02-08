package ru.md.jsp.tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.uit.director.db.dbobjects.AttributeStruct;
import org.uit.director.tasks.AttributesStructList;

public class PUP_EXT {
	
	public static int getValuesCountByAttributeName(AttributesStructList attrs, String name) {
		int size = 0;
		AttributeStruct attr = (AttributeStruct) attrs.findAttributeByName(name);
		if (attr != null) {
			size = 1;
			List list = attr.getAttribute().getValueAttributeList();
			if (list != null)
				size = list.size();
		}
		
		return size;
	}
	
	public static void init_DS(ServletRequest request, ServletResponse response) {
		init_currency_DS(request, response);
		init_duration_type_DS(request, response);
		init_loan_type_DS(request, response);
	}
	
	public static void init_currency_DS(ServletRequest request, ServletResponse response) {		
		List list = new ArrayList();
		list.add("РУБ");
		list.add("USD");
		list.add("EUR");
		request.setAttribute(IConst_PUP.LIST_DURATION_TYPE, list);
	}
	
	public static void init_duration_type_DS(ServletRequest request, ServletResponse response) {
		List list = new ArrayList();
		list.add("дней");
		list.add("недель");
		list.add("месяцев");
		list.add("лет");
		request.setAttribute(IConst_PUP.LIST_DURATION_TYPE, list);
	}
	
	public static void init_loan_type_DS(ServletRequest request, ServletResponse response) {
		List list = new ArrayList();
		list.add("I");
		list.add("II");
		list.add("III");
		list.add("IV");
		list.add("V");
		request.setAttribute(IConst_PUP.LIST_LOAN_TYPE, list);
	}
	
	
}

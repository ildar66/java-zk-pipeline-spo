package com.vtb.domain;

import ru.masterdm.spo.utils.Formatter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrgSearchParam implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrgSearchParam.class.getName());
	private String number;
	private String name;
	private String inn;
	private String group;

	public Map<String,Object> getHashMap() {
		Map<String,Object> filter =  new HashMap<String,Object>();
		if(!getInn().isEmpty())
			filter.put("inn", getInn());
		if(!getGroup().isEmpty())
		    filter.put("group", getGroup().toLowerCase());
		if(!getNumber().isEmpty())
			filter.put("number", getNumber());
		if(!getName().isEmpty())
			filter.put("name", getName().toLowerCase());
		return filter;
	}

	public String getUrlParam(){
		StringBuilder sb = new StringBuilder();
		sb.append("&innfilter=" + getInn());
		sb.append("&filtergroup=" + getGroup());
		sb.append("&numberfilter=" + getNumber());
		sb.append("&filter=" + getName());
		return sb.toString();
	}
	/**
	 * конструктор из реквеста
	 * @param req
	 * @return
	 */
	public OrgSearchParam(HttpServletRequest req){
	    try{
			if(req.getParameter("first")!=null && req.getParameter("first").equals("first")){//в этом случае читаем из печенек.
				//это первый запуск формы поиска
				Cookie[] cookies = req.getCookies();
				if (cookies != null)
					for(Cookie cookie: cookies){
						String name = cookie.getName();
						String value = URLDecoder.decode(cookie.getValue(),"UTF-8");
						if(name.equals("org_search_param_inn")) {setInn(value.trim());}
						if(name.equals("org_search_param_group")) {setGroup(value.trim());}
						if(name.equals("org_search_param_name")) {setName(value.trim());}
						if(name.equals("org_search_param_number")) {setNumber(value.trim());}
					}
			}

			if(req.getParameter("innfilter")!=null && req.getParameter("innfilter").trim().length()>0)
				setInn(req.getParameter("innfilter").trim());
			if(req.getParameter("filtergroup")!=null && req.getParameter("filtergroup").trim().length()>0)
				setGroup(req.getParameter("filtergroup").trim());
			if(req.getParameter("numberfilter")!=null && req.getParameter("numberfilter").trim().length()>0)
				setNumber(req.getParameter("numberfilter").trim());
			if(req.getParameter("filter")!=null && req.getParameter("filter").trim().length()>0)
				setName(req.getParameter("filter").trim());
		} catch (UnsupportedEncodingException e) {
	        LOGGER.log(Level.SEVERE, e.getMessage(), e);
	        e.printStackTrace();
        }
	}
	public void clearCookies(HttpServletResponse resp){
		resp.addCookie(prepareCookie("org_search_param_inn", null));
		resp.addCookie(prepareCookie("org_search_param_group", null));
		resp.addCookie(prepareCookie("org_search_param_name", null));
		resp.addCookie(prepareCookie("org_search_param_number", null));
	}
	/** сохранить куки */
	public void saveCookies(HttpServletResponse resp){
	    resp.addCookie(prepareCookie("org_search_param_inn", getInn()));
	    resp.addCookie(prepareCookie("org_search_param_group", getGroup()));
	    resp.addCookie(prepareCookie("org_search_param_name", getName()));
	    resp.addCookie(prepareCookie("org_search_param_number", getNumber()));
	}

	private Cookie prepareCookie(String name, String value){
        try {
            Cookie c = new Cookie(name, value == null ? "" : URLEncoder.encode(value,"UTF-8"));
            if (value == null) c.setMaxAge(0);
            return c;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Cookie("URLEncoder.encodeerror",e.getMessage());
        }
	}

	public String getInn() {
		return Formatter.str(inn);
	}

	public void setInn(String inn) {
		this.inn = inn;
	}

	public String getGroup() {
		return Formatter.str(group).replaceAll("\"","").replaceAll("\'","");
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getNumber() {
		return Formatter.str(number);
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return Formatter.str(name).replaceAll("\"", "");
	}

	public void setName(String name) {
		this.name = name;
	}
}

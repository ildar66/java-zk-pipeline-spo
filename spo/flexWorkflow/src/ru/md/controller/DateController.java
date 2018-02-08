package ru.md.controller;


import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vtb.util.Formatter;

 
@Controller
public class DateController {
	@RequestMapping(value = "/ajax/isCrossDate.html")
	public String isCrossDates(@ModelAttribute("model") ModelMap model,
			@RequestParam("from1") String from1,@RequestParam("from2") String from2,
			@RequestParam("to1") String to1,@RequestParam("to2") String to2,
			HttpServletRequest request) throws Exception {
		if(Formatter.parseDate(from1)==null || Formatter.parseDate(from2)==null || Formatter.parseDate(to1)==null || Formatter.parseDate(to2)==null){
			model.addAttribute("msg","OK");
			return "utf8";
		}
		if(after(Formatter.parseDate(from2), Formatter.parseDate(to1)) || after(Formatter.parseDate(from1), Formatter.parseDate(to2))){
			model.addAttribute("msg","OK");
			return "utf8";
		}
		model.addAttribute("msg","Периоды предоставления не могут пересекаться: c "+from1+" по "+to1+" и с "+from2+" по "+to2);
		return "utf8";
	}
	public boolean after(Date d1, Date d2){
		if(d1==null)
			return false;
		if(d2==null)
			return false;
		if(d1.equals(d2))
			return true;
		return d1.after(d2);
	}
	@RequestMapping(value = "/ajax/deltaDate.html") @ResponseBody
    public String deltaDate(@ModelAttribute("model") ModelMap model,
    		@RequestParam("from") String fromString,@RequestParam("delta") Long delta,
    		@RequestParam("deltaDimension") String deltaDimension,
    		HttpServletRequest request) throws Exception {
		if(deltaDimension==null || deltaDimension.isEmpty())
			return "";
		Date from = Formatter.parseDate(fromString);
		Calendar c = Calendar.getInstance();
		c.setTime(from);
		int dem = deltaDimension.equals("дн.")?Calendar.DATE:(deltaDimension.equals("мес.")?Calendar.MONTH:Calendar.YEAR);
		c.add(dem, delta.intValue());
        return Formatter.format(c.getTime());
    }
	
	public static Date add3years(Date d){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.YEAR, 3);
		return c.getTime();
	}
}

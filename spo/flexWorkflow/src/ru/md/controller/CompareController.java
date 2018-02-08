package ru.md.controller;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.md.compare.Result;
import ru.md.helper.CompareHelper;

/**
 * @author rislamov
 */
@Controller
public class CompareController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompareController.class.getName());

	/**
	 * Ajax-метод для сравнения
	 * @param model model
	 * @param strIds идентификаторы
	 * @param objType типы объектов
	 * @param blockName имя блока
	 * @param current текущий объект
	 * @param request request
	 * @return xml-структура результата сравнения
	 * @throws Exception ошибка
	 */
	@RequestMapping(value = "/ajax/compare.html")
	public String compare(@ModelAttribute("model") ModelMap model,
			@RequestParam("ids") String strIds, @RequestParam("objectType") String objType,
			@RequestParam("name") String blockName, @RequestParam("current") String current,
			HttpServletRequest request) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(Result.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		Result compareResult = CompareHelper.compare(strIds, objType, blockName, current, request);
		jaxbMarshaller.marshal(compareResult, sw);
		String xmlString = sw.toString();
		LOGGER.info(xmlString);
		model.addAttribute("msg", xmlString);
		return "utf8";
	}
}
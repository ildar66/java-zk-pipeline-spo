package ru.md.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.md.persistence.MdTaskMapper;
 
/**
 * Контролер работы в заявкой
 */
@Controller
public class MdTaskController {
    
    @Autowired
    MdTaskMapper mdTaskMapper;
    
	/**
	 * Добавление заявки в избранные если ее там нет, и наоборот.
	 * @param mdTaskId идентификатор заявки
	 * @param userId идентификатор пользователя
	 */
	@RequestMapping(value = "/ajax/favoriteSwitcher.html") 
	@ResponseBody
	public String favoriteSwitcher(@RequestParam("mdTaskId") Long mdTaskId, @RequestParam("userId") Long userId) {
	    mdTaskMapper.favoriteSwitcher(mdTaskId, userId);
		return mdTaskId.toString();
	}
}

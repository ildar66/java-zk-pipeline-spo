package ru.md.controller;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

import com.vtb.util.CollectionUtils;
import ru.md.domain.ClientInfo;
import ru.md.domain.ClientInfoLimit;
import ru.md.domain.Decision;
import ru.md.helper.TaskHelper;
import ru.md.persistence.ClientMapper;
import ru.md.persistence.CompendiumMapper;
import ru.md.persistence.UserMapper;
import ru.md.spo.dbobjects.OrgJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;


@Controller
public class EkController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EkController.class.getName());
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private CompendiumMapper compendiumMapper;
	@Autowired
	private ClientMapper clientMapper;

	private String clientInfoSave(String id, HttpServletRequest request) throws Exception {
		ClientInfo info = new ClientInfo();
		info.setPub(request.getParameter("pub"));
		info.setStatus(request.getParameter("status"));
		info.setSecurityLast(Formatter.parseDate(request.getParameter("securityLast")));
		info.setSecurityText(Formatter.cut(request.getParameter("securityText"),3000));
		info.setCorpBlock(Formatter.cut(request.getParameter("corpBlock"),3000));
		info.setSecurityValidto(Formatter.parseDate(request.getParameter("securityValidto")));
		info.setValidtoDate(Formatter.parseDate(request.getParameter("validtoDate")));
		info.setSublimit(Formatter.cut(request.getParameter("sublimit"),100));
		//рейтинги
		info.setGroupRating(Formatter.cut(request.getParameter("groupRating"),30));
		info.setRatingMethod(Formatter.cut(request.getParameter("ratingMethod"),300));
		info.setRatingScale(Formatter.cut(request.getParameter("ratingScale"),300));
		info.setRatingScaleGroup(Formatter.cut(request.getParameter("ratingScaleGroup"),300));
		info.setRating(Formatter.cut(request.getParameter("rating"),300));
		info.setRatingReview(Formatter.cut(request.getParameter("ratingReview"),30));
		info.setGroupRatingReview(Formatter.cut(request.getParameter("groupRatingReview"),30));
		//приостановка
		info.setSuspendLimitInvestDate(Formatter.parseDate(request.getParameter("suspendLimitInvestDate")));
		info.setSuspendLimitLoanDate(Formatter.parseDate(request.getParameter("suspendLimitLoanDate")));
		info.setSuspendLimitLoan(Formatter.cut(request.getParameter("suspendLimitLoan"),30));
		info.setSuspendLimitInvest(Formatter.cut(request.getParameter("suspendLimitInvest"),30));
		clientMapper.saveClientInfo(id, info);
		info = clientMapper.getClientInfo(id);
		clientMapper.updateDecision(parseDecision(request, "group", info.getGroupDecision()));
		clientMapper.updateDecision(parseDecision(request, "groupReview", info.getGroupDecisionReview()));
		clientMapper.updateDecision(parseDecision(request, "clientReview", info.getClientDecisionReview()));
		clientMapper.updateDecision(parseDecision(request, "client", info.getClientDecision()));
		clientMapper.updateDecision(parseDecision(request, "limit", info.getLimitDecision()));
		clientMapper.updateDecision(parseDecision(request, "suspendLimitInvest", info.getSuspendLimitInvestDecision()));
		clientMapper.updateDecision(parseDecision(request, "suspendLimitLoan", info.getSuspendLimitLoanDecision()));
		/*try {
			return updateDocuments(request, id);
		} catch (Exception e) {
			return e.getMessage();
		}*/
		return "Карточка клиента сохранена";
	}
	@RequestMapping(value = "/downloadFile.html")
	public void downloadFile(@RequestParam("id") Long id, HttpServletResponse response) {
		try {
			Decision file = clientMapper.getDocData(id);
			String fileName = new String(file.getFilename().getBytes("CP1251"), "CP1252");
			if(fileName!=null)
				fileName = fileName.replaceAll("\"", "").replaceAll("“", "").replaceAll("”", "");
			ByteArrayInputStream is = new ByteArrayInputStream(file.getFiledata());
			response.setContentType(file.getContenttype()==null?"application/octet-stream":file.getContenttype());
			response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException ex) {
			LOGGER.info("Error writing file to output stream. Fileid was '{}'", id, ex);
			throw new RuntimeException("IOError writing file to output stream");
		}
	}

	private Decision parseDecision(HttpServletRequest request, String prefix, Long id){
		Decision res = new Decision();
		res.setProtocolNo(Formatter.cut(request.getParameter(prefix+"ProtocolNo"),30));
		res.setDecisionDate(Formatter.parseDate(request.getParameter(prefix+"DecisionDate")));
		res.setIdDecision(id);
		res.setDecisionBody(parseDecisionBody(request, prefix+"DecisionBody"));
		return res;
	}
	private ArrayList<String> parseDecisionBody(HttpServletRequest request, String name){
		ArrayList<String> res = new ArrayList<String>();
		if (request.getParameter(name)!=null)
			for (String txt : request.getParameterValues(name))
				res.add(txt);
		return res;
	}

	@RequestMapping(value = "/clientInfo.html")
	public String clientInfo(@ModelAttribute("model") ModelMap model,
    		 @RequestParam(value="mdtask",required=false) Long mdtask,
			@RequestParam("id") String id, HttpServletRequest request) throws Exception {
		if (request.getParameter("clientInfo") != null)
			model.addAttribute("msg", clientInfoSave(id, request));
		model.addAttribute("id", id);
		model.addAttribute("mdtask", mdtask);
		model.addAttribute("editMode", clientInfoEditMode());
		model.addAttribute("org", compendiumMapper.getEkById(id));
		model.addAttribute("dealStatusList", clientMapper.getDealStatusList());
		model.addAttribute("decisionMakerList", clientMapper.getDecisionMakerList());
		model.addAttribute("bodydecisionList", clientMapper.getBodydecisionList());
		ClientInfo info = clientMapper.getClientInfo(id);
		if (info == null)
			info = new ClientInfo();
		model.addAttribute("clientInfo", info);

		model.addAttribute("groupDecision", getDocDecision(info.getGroupDecision()));
		model.addAttribute("groupDecisionReview", getDocDecision(info.getGroupDecisionReview()));
		model.addAttribute("clientDecision", getDocDecision(info.getClientDecision()));
		model.addAttribute("clientDecisionReview", getDocDecision(info.getClientDecisionReview()));
		model.addAttribute("limitDecision", getDocDecision(info.getLimitDecision()));
		model.addAttribute("suspendLimitLoanDecision", getDocDecision(info.getSuspendLimitLoanDecision()));
		model.addAttribute("suspendLimitInvestDecision", getDocDecision(info.getSuspendLimitInvestDecision()));

		return "clientInfo";
	}

	@RequestMapping(value="/upload.html")
	public @ResponseBody String handleFileUpload(HttpServletRequest request) throws Exception {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024*1024);// Максимальный размер буфера данных в байтах
		ServletFileUpload upload = new ServletFileUpload(factory);
		List<FileItem> items = upload.parseRequest(request);
		Decision res = new Decision();
		for (FileItem item : items) {
			if (item.isFormField()) {
				LOGGER.info("handleFileUpload "+item.getFieldName()+" = "+item.getString("UTF-8"));
				if(item.getFieldName().equals("decid"))
					res.setIdDecision(Long.valueOf(item.getString("UTF-8")));
			} else {
				LOGGER.info("handleFileUpload filename "+item.getName());
				LOGGER.info("handleFileUpload ContentType "+item.getContentType());
				res.setFilename(item.getName());
				res.setContenttype(item.getContentType());
				res.setFiledata(item.get());
			}
		}
		//залить в базу
		clientMapper.updateDecisionData(res);
		return "OK";
	}

	private Decision getDocDecision(Long idDecision) {
		if (idDecision == null)
			return new Decision();
		Decision res = clientMapper.getDocDecision(idDecision);
		res.setDecisionBody(clientMapper.getDecisionBody(idDecision));
		return res;
	}

	private static final Set<String> cpsRolesEditClientInfo = CollectionUtils.set("Работник мидл-офиса (мониторинг)", "Работник мидл-офиса (мониторинг ковенант)",
								  "Работник подразделения по мониторингу залогов", "Руководитель мидл-офиса (мониторинг)", "Руководитель мидл-офиса (мониторинг ковенант)",
								  "Руководитель подразделения по мониторингу залогов", "Сотрудник мидл-офиса ОФКР", "Руководитель мидл-офиса ОФКР");
	private boolean clientInfoEditMode() throws Exception {
		PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
		List<String> userCpsRoles = userMapper.userCpsRoles(pupFacade.getCurrentUser().getIdUser());
		for (String roles : userCpsRoles)
			if (cpsRolesEditClientInfo.contains(roles))
				return true;
		return false;
	}

	@RequestMapping(value = "/changeMainOrganisation.html")
	public String changeMainOrganisationStage1(@ModelAttribute("model") ModelMap model,
			@RequestParam("mdtaskid") Long mdtaskid, HttpServletRequest request) throws Exception {
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskid);
		if(taskJPA.getOrgList().isEmpty()){
			model.addAttribute("msg", "В заявке нет основного заёмщика");
			return "utf8";
		}
		String createGroup = taskJPA.getMainOrgGroup();
		String kzId = taskJPA.getOrgList().get(0).getId();
		String ekId = kzId;
		OrgJPA kz = TaskHelper.dict().getOrg(kzId);
		if(kz != null && kz.getIdUnitedClient() != null && !kz.getIdUnitedClient().isEmpty())
		    ekId = kz.getIdUnitedClient();
		String currentGroup = SBeanLocator.singleton().compendium().getEkGroupId(ekId);//какая сейчас группа у основного заёмщика
		//На ЭФ Заявка при нажатии на кнопку "Изменить заемщика" проверять, ГК у ЕК пустая / непустая.
		if(Formatter.str(currentGroup).isEmpty() || Formatter.str(createGroup).isEmpty()){
			LOGGER.info("currentGroup="+Formatter.str(currentGroup));
			LOGGER.info("createGroup="+Formatter.str(createGroup));
			if(Formatter.str(currentGroup).isEmpty() && Formatter.str(createGroup).isEmpty())
				return "changeMainOrganisationEmptyGroup";
			//Если пустая
			Map<String,Object> filter =  new HashMap<String,Object>();
			String group = Formatter.str(currentGroup);
			if (Formatter.str(currentGroup).isEmpty())
				group = Formatter.str(createGroup);
			filter.put("group", SBeanLocator.singleton().compendium().getEkGroupName(group).toLowerCase());
			if(SBeanLocator.singleton().compendium().getEkPageTotalCount(filter)>0)
			    return "redirect:popup_org.jsp?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID" +
						"&filtergroup=" + URLEncoder.encode(SBeanLocator.singleton().compendium().getEkGroupName(group).toLowerCase(), "UTF-8") +
						"&onMySelect=changeMainOrganisationStage2()&first=first";//вывести список ЕК по ГК, выбранному ранее при создании заявки.
			else
				return "changeMainOrganisationEmptyGroup";
		} else {
			//проверять, совпадают ли slxid выбранной ранее ГК при создании заявки и slxid текущей ГК
			if(currentGroup.equals(createGroup)) {
				return "redirect:popup_org.jsp?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&mainorg="+
						ekId+
						"&onMySelect=changeMainOrganisationStage2()&first=first";//При совпадении выводим список ЕК с текущим ГК.
			}
			else {
				//В противном случае выводим всплывающее окно, в котором:
				//сообщение: «Выбранная ранее ГК отличается от текущей ГК основного заемщика. Список возможных клиентов по ним может отличаться. Выберите ГК, по которой нужно сформировать список" и
				//список для выбора пользователю: <Выбранная ранее ГК>, <Текущая ГК>, «Учитывать обе ГК»
				model.addAttribute("group1", SBeanLocator.singleton().compendium().getEkGroupName(createGroup));
				model.addAttribute("group2", SBeanLocator.singleton().compendium().getEkGroupName(currentGroup));
				return "changeMainOrganisationNotEmptyGroup";
			}
		}
	}

}

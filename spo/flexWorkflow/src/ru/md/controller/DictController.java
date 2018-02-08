package ru.md.controller;



import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.Department;
import ru.md.domain.Org;
import ru.md.domain.Product;
import ru.md.domain.Withdraw;
import ru.md.helper.TaskHelper;
import ru.md.persistence.CurrencyMapper;
import ru.md.persistence.DepartmentMapper;
import ru.md.persistence.ProductMapper;
import ru.md.persistence.WithdrawMapper;
import ru.md.pup.dbobjects.DepartmentJPA;
import ru.md.spo.dbobjects.OrgJPA;
import ru.md.spo.ejb.StandardPeriodBeanLocal;
import ru.md.spo.ejb.TaskFacadeLocal;
 
/**
 * контролер возвращает значения справочников для ajax.
 */
@Controller
public class DictController {
	private static final Logger LOGGER = LoggerFactory.getLogger(DictController.class.getName());
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private CurrencyMapper currencyMapper;
	@Autowired
	private DepartmentMapper departmentMapper;
	@Autowired
	private WithdrawMapper withdrawMapper;

	
	@RequestMapping(value = "/ajax/withdraw.html") 
	public String getWithdraw(@ModelAttribute("model") ModelMap model, 
			@RequestParam("tranceid") Long tranceid,@RequestParam("ro") String ro,@RequestParam("mdtask") Long mdtask,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Cache-control", "no-cache");
		TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
		ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(mdtask);
		List<Withdraw> list = null;
		if(tranceid.equals(0L))
			list = withdrawMapper.findByMdtask(mdtask);
		else
			list = withdrawMapper.findByTrance(tranceid);
		if(list!=null)
			for(Withdraw w : list)
				w.generateFormatetDates(taskJPA.getTrance_period_format());
		model.addAttribute("list",list);
		model.addAttribute("currency_list",currencyMapper.getCurrencyList());
		return ro.equalsIgnoreCase("true")?"withdrawRO":"withdraw";
	}
	
	@RequestMapping(value = "/ajax/standardPeriodValue.html") 
	public String getStandardPeriodValue4group(@ModelAttribute("model") ModelMap model, 
			@RequestParam("grid") String grid,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		LOGGER.info("getStandardPeriodValue4group. grid="+grid);
		StandardPeriodBeanLocal spLocal = com.vtb.util.EjbLocator.getInstance().getReference(StandardPeriodBeanLocal.class);
		model.addAttribute("res", spLocal.getStandardPeriodGroup(Long.valueOf(grid)).getValues());
		return "standardPeriodValue";
	}
	
	@RequestMapping(value = "/ajax/actualid.html")
	public String getACTUALID(@ModelAttribute("model") ModelMap model, 
			@RequestParam("productid") String productid,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Product product =  productMapper.getById(productid);
		String actualid = product==null?"":product.getActualid();
		if(actualid==null)
			actualid = "";
		if(actualid.toUpperCase().startsWith("CLINGV")){
			model.addAttribute("msg",("{'actualid':'"+actualid+"','showDebt':false,'showLimit':true,'editDebt':true,'editLimit':false}").replaceAll("'", "\""));
			return "utf8";
		}
		if(actualid.toUpperCase().startsWith("CLINGZ")){
			model.addAttribute("msg",("{'actualid':'"+actualid+"','showDebt':true,'showLimit':false,'editDebt':false,'editLimit':true}").replaceAll("'", "\""));
			return "utf8";
		}
		if(actualid.toUpperCase().startsWith("CLINGM")){
			model.addAttribute("msg",("{'actualid':'"+actualid+"','showDebt':true,'showLimit':true,'editDebt':false,'editLimit':false}").replaceAll("'", "\""));
			return "utf8";
		}
		if(actualid.toUpperCase().startsWith("CLINL") || actualid.toUpperCase().startsWith("CLINK") || actualid.toUpperCase().startsWith("CLINP")){
			model.addAttribute("msg",("{'actualid':'"+actualid+"','showDebt':true,'showLimit':true,'editDebt':true,'editLimit':true}").replaceAll("'", "\""));
			return "utf8";
		}
		model.addAttribute("msg",("{'actualid':'"+actualid+"','showDebt':false,'showLimit':false,'editDebt':true,'editLimit':true}").replaceAll("'", "\""));
		return "utf8";
	}
	@RequestMapping(value = "/ajax/departmentList.html") 
    public String getDepartmentList(@ModelAttribute("model") ModelMap model, 
    		@RequestParam("onlyInitialDep") String onlyInitialDep,@RequestParam("routeid") String routeid,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		//список подразделений 
		List<Department> list = departmentMapper.getAll();
		ArrayList<Department> departments = new ArrayList<Department>();
		for(Department department: list) {
			if(onlyInitialDep!=null&& onlyInitialDep.equals("true")&&!department.isInitial())continue;
			departments.add(department);
		}
		model.addAttribute("list", departments);
		model.addAttribute("routeid", routeid);
		model.addAttribute("onlyInitialDep", onlyInitialDep);
        return "departmentList";
    }
	
	/**
	 * возвращает список КЗ для ЕК с учётом места проведения сделки. VTBSPO-126
	 */
	@RequestMapping(value = "/ajax/kzList.html") 
	public String getKzList(@ModelAttribute("model") ModelMap model, 
			@RequestParam("ek_id") String ek_id,@RequestParam("depid") Long depid,@RequestParam(value="place2",required=false) String place2,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dep_name = departmentMapper.getById(depid).getCrmName();
		String place2_name = null;
		if(place2!=null && !place2.isEmpty()){
			if (place2.equals("0"))
				place2_name = "Не определено";
			else
				try {
					place2_name = departmentMapper.getById(Long.valueOf(place2)).getCrmName();
				} catch (Exception e) {
					LOGGER.warn("can't find place2_name by id "+place2,e);
				}
		}
		if(dep_name == null)
			dep_name = "";
		if(place2 == null)
			place2 = "";
		String msg="{'dep_name':'"+dep_name+"','place2':'"+place2+"', 'org' : [";
		String orgs = "";
		Pair<List<OrgJPA>,List<DepartmentJPA>> pair = TaskHelper.dict().findOrganization4EK(dep_name, ek_id,place2_name);
		for(OrgJPA org : pair.getLeft()){
			if(!orgs.isEmpty()) orgs +=", ";
			String desc = "";
			if(!org.getInn().isEmpty())
				desc += "ИНН: " + org.getInn() + ", ";
			if(!org.getClientcategory().isEmpty())
				desc += "Категория: " + org.getClientcategory() + ", ";
			if(!org.getKpp().isEmpty())
				desc += "КПП: " + org.getKpp();
			orgs += "{"+jsonField("name",org.getOrganizationName())+jsonField("id",org.getId())+jsonField("desc", desc)+"'f':'f'}";
		}
		msg += orgs+"], 'deps' : [";
		String deps = "";
		for(DepartmentJPA dep : pair.getRight()){
			if(!deps.isEmpty()) deps +=", ";
			deps += "{"+jsonField("name",dep.getShortName())+jsonField("id",dep.getIdDepartment().toString())+"'f':'f'}";
		}
		msg += deps+"]}";
		model.addAttribute("msg", msg.replaceAll("'", "\""));
		return "utf8";
	}
	private String jsonField(String name, String val){
		return "'"+name+"':'"+ Formatter.strWeb(val.replaceAll("\"", "").replaceAll("'",""))+"', ";
	}
	@RequestMapping(value = "/ajax/orgname.html") 
	public String getOrgName(@ModelAttribute("model") ModelMap model, 
			@RequestParam("id") String id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		OrgJPA org = TaskHelper.dict().getOrg(id);
		if (org == null){
			model.addAttribute("msg", "");
			return "utf8";
		}
		if(org.getIdUnitedClient()!=null)
			org = TaskHelper.dict().getOrg(org.getIdUnitedClient());
		if (org == null){
			model.addAttribute("msg", "");
			return "utf8";
		}
		model.addAttribute("msg", org.getOrganizationName());
		return "utf8";
	}
	@RequestMapping(value = "/ajax/org_group_name.html") 
	public String getOrgGroupNameByOrgId(@ModelAttribute("model") ModelMap model, 
			@RequestParam("idorg") String id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Org org = SBeanLocator.singleton().compendium().getEkById(id);
		if (org == null)
			model.addAttribute("msg", "");
		else
			model.addAttribute("msg", org.getGroupname());
		return "utf8";
	}
}

package ru.md.spo.ejb;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import com.vtb.domain.ApprovedRating;
import com.vtb.domain.Task;
import com.vtb.domain.TaskCurrency;
import com.vtb.exception.MappingException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import ru.md.domain.MdTask;
import ru.md.domain.TaskKz;
import ru.md.persistence.MdTaskMapper;
import ru.md.spo.dbobjects.OrgJPA;
import ru.md.spo.dbobjects.ProductGroupJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.report.Contractor;
import ru.md.spo.report.TaskReport;
import ru.md.spo.report.User;

import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

/**
 * ААА 21. ДО. Сервис начитки объектов.
 * @author Andrey Pavlenko
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ReportBean implements ReportBeanLocal {
    @Autowired
    private MdTaskMapper mdTaskMapper;
    @EJB
    private PupFacadeLocal pupFacade;
    @EJB
    private DictionaryFacadeLocal dictionaryFacade;
    @EJB
    private TaskFacadeLocal taskFacade;

    @Override
    public TaskReport getTaskReport(Long idMdtask) {
        TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
        TaskReport tr = new TaskReport();
        MdTask task = mdTaskMapper.getById(idMdtask);
        TaskJPA taskJPA = taskFacade.getTask(idMdtask);
        tr.number = task.getMdtaskNumber();
        tr.version = task.getVersion();
        tr.mdtaskSum = task.getMdtaskSum();
        tr.currency = task.getCurrency();
        tr.place = taskJPA.getPlace()==null?"":taskJPA.getPlace().getFullName();
        tr.initDepartment = taskJPA.getInitDepartment().getFullName();
        for(TaskKz kz : SBeanLocator.singleton().compendium().getTaskKzByMdtask(idMdtask)){
            OrgJPA org =dictionaryFacade.getOrg(kz.getKzid());
            String rating = "";
            String ratingDate = "";
            ApprovedRating ar = processor.getApprovedRating(new Date(), kz.getKzid());
            if (ar!=null){
                rating = ar.getRating();
                ratingDate = Formatter.format(ar.getDate());
            }
            tr.contractors.add(new Contractor(SBeanLocator.singleton().getDictService().getEkNameByOrgId(kz.getKzid()),
                  org.getIndustry(), org.getClientcategory(),
                  org.getIdUnitedClient()==null?"":SBeanLocator.singleton().compendium().getEkById(org.getIdUnitedClient()).getGroupname(),
                  org.getInn(), rating, ratingDate, kz.getRatingpkr(),org.getOrganizationName(), kz.isMainOrg() ));
        }
        for (ru.md.spo.dbobjects.ProjectTeamJPA team : taskJPA.getProjectTeam("p")) {
            if (pupFacade.userAssignedAs(team.getUser().getIdUser(),"Структуратор", taskJPA.getIdProcess()))
                tr.structurator = new User(team.getUser().getFullName(), team.getUser().getDepartment().getFullName());
            if (pupFacade.userAssignedAs(team.getUser().getIdUser(),"Кредитный аналитик", taskJPA.getIdProcess()))
                tr.analist = new User(team.getUser().getFullName(), team.getUser().getDepartment().getFullName());
            if (pupFacade.userAssignedAs(team.getUser().getIdUser(),"Клиентский менеджер", taskJPA.getIdProcess()))
                tr.clientManager = new User(team.getUser().getFullName(), team.getUser().getDepartment().getFullName());
        }
        for (ProductGroupJPA pg : taskJPA.getProductGroupList())
            tr.productGroupList.add(pg.getName());
        try {
            Task taskJDBC = processor.getTask(new Task(new Long(idMdtask)));
            for (TaskCurrency taskCurrency : taskJDBC.getCurrencyList())
                tr.currencyList.add(taskCurrency.getCurrency().getCode());
        } catch (MappingException e) {
            e.printStackTrace();
        }
        return tr;
    }
}

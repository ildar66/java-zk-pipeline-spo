package ru.md.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.AttachmentActionProcessor;
import ru.md.domain.MdTask;
import ru.md.domain.dashboard.CCQuestion;

import ru.masterdm.compendium.domain.Department;
import ru.masterdm.compendium.domain.cc.QuestionType;
import ru.masterdm.compendium.model.CompendiumActionProcessor;
import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

/**
 * Логика секции
 */
@Controller
public class ConclusionController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConclusionController.class);
    public static String question(Long idMdtask)  throws Exception {
        return new Gson().toJson(getCCQuestion(idMdtask));
    }
    public static boolean isFORCC(Long idMdtask, String unid) throws Exception {
        return SBeanLocator.singleton().mdTaskMapper().isFORCC(idMdtask, unid);
    }
    public static List<CCQuestion> getCCQuestion(Long idMdtask) throws Exception {
        List<CCQuestion> res = new ArrayList<CCQuestion>();
        if (idMdtask == null || idMdtask.equals(0L))
            return res;
        MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(idMdtask);
        if (task.getQuestionGroup() == null)
            res.add(loadCCQuestion(idMdtask));
        else
            for(Long id : SBeanLocator.singleton().mdTaskMapper().getIdMdtaskByQuestionGroup(task.getQuestionGroup()))
                res.add(loadCCQuestion(id));
        return res;
    }
    private static CCQuestion loadCCQuestion(Long idMdtask){
        CCQuestion q = SBeanLocator.singleton().mdTaskMapper().getCCQuestion(idMdtask);
        if(Formatter.str(q.status).isEmpty()) q.status = "статус не присвоен";
        if(Formatter.str(q.protocol).isEmpty()) q.protocol = "не присвоен";
        q.resolution = getResolution(idMdtask,q.idReport);
        return q;
    }
    private static String getResolution(Long id_mdtask, Long id_template) {
        return id_template != null && id_template.longValue() != 0L?"<a href=downloadResolution.do?id_template=" + String.valueOf(id_template) + "&id_mdtask=" + id_mdtask + ">выписка из протокола</a>":"Выписка не готова";
    }
    public static String pkrList(Long id_pup_process) throws Exception {
        ArrayList<Map<String, String>> res = new ArrayList<Map<String, String>>();
        AttachmentActionProcessor attachmentProcessor = (AttachmentActionProcessor) ActionProcessorFactory.getActionProcessor("Attachment");
        Map<String, String> map = attachmentProcessor.findByOwnerAndKeyType(id_pup_process, "credit_decision_project");
        LOGGER.info("pkrList map size " + map.size());
        for (String id : map.keySet()){
            Map<String, String> doc = new HashMap<String, String>();
            doc.put("id", id);
            doc.put("name", map.get(id));
            res.add(doc);
        }
        return new Gson().toJson(res);
    }
    public static String allowedCommittees(Integer idStartDepartment) {
        Gson gson = new Gson();
        return gson.toJson(getAllowedCommitteess(idStartDepartment));
    }
    public static List<ru.md.domain.Department> getAllowedCommitteess(Integer idStartDepartment) {
        List<ru.md.domain.Department> res = new ArrayList<ru.md.domain.Department>();
        for(Long id : SBeanLocator.singleton().getDepartmentMapper().getAllowedCommittees(idStartDepartment)) {
            ru.md.domain.Department d = SBeanLocator.singleton().getDepartmentMapper().getById(id);
            if (d.getIsActive() != null && d.getIsActive().equals(1L))
                res.add(d);
        }
        return res;
    }
    public static String getQuestionTypeListJson(){
        return new Gson().toJson(getQuestionTypeList());
    }
    public static List<QuestionType> getQuestionTypeList(){
        List<ru.masterdm.compendium.domain.cc.QuestionType> res = new ArrayList<QuestionType>();
        for (ru.masterdm.compendium.domain.cc.QuestionType qt : compenduim().findQuestionTypePage(null,1,3000,"c.questionType").getList())
            if (qt.getIsUsableInSpo())
                res.add(qt);
        return res;
    }

    public static CompendiumActionProcessor compenduim() {
        CompendiumActionProcessor compenduim = (CompendiumActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("Compendium");
        return compenduim;
    }
}

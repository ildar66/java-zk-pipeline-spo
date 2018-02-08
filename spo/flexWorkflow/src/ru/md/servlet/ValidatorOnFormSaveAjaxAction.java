package ru.md.servlet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;

import com.vtb.domain.Task;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;
import com.vtb.util.Formatter;
/**
 * Вычисляет данные, которые необходимо проверить на момент сохранения заявки
 * (проверка многопоточности: данные в уже заявке могли устареть, либо данные в сублимитах и сделках,
 * связанных с данной заявкой, изменились, пока редактировались данные в этой заявке). 
 */
public class ValidatorOnFormSaveAjaxAction extends Action {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Compute data onSave by mdTaskId
     * use param "idTask" from request
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // находим требуемые данные в базе  
        BigDecimal upperLimitSumRoubles = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        // get sum of all children without this child (it's been changed)
        BigDecimal childrenSumsRoubles = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP); 
        // get old sum of this task (saved in the database)
        BigDecimal oldThisSumsRoubles = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP); 
        BigDecimal thisSumRoubles = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        boolean isOpportunity = true;
        StringBuilder opportunityDateErrorMsg = new StringBuilder();
        try{
            String idTask = request.getParameter("idTask");
            String limit_sum = request.getParameter("limit_sum");
            String currency = request.getParameter("currency");
            String validToStr = request.getParameter("valid_to_param");
            String mdtaskPeriod = request.getParameter("mdtask_period");
            String usePeriod = request.getParameter("use_period");
            String useDateStr = request.getParameter("use_date");
            
            Date validFrom = null;
            logger.info("AJAX validateOnSave.do idTask:" + idTask);
            logger.info("AJAX validateOnSave.do limit_sum:" + limit_sum);
            logger.info("AJAX validateOnSave.do currency:" + currency);
            logger.info("AJAX validateOnSave.do valid_from:" + validToStr);
            logger.info("AJAX validateOnSave.do mdtaskPeriod:" + mdtaskPeriod);
            logger.info("AJAX validateOnSave.do usePeriod:" + usePeriod);
            logger.info("AJAX validateOnSave.do useDate:" + useDateStr);

            // validFrom = validToDate - mdtask_period
            if ((idTask == null) || (idTask.equals(""))) {
                logger.info("AJAX validateOnSave.do idTask request parameter is not present");
            } else {
                Long taskId = Long.parseLong(idTask); 
                TaskActionProcessor p = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
                CompendiumCrmActionProcessor c = 
                    (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
                Task task = p.getTask(new Task(taskId));
                Task parent = findParent(task, p);
                // changed: now only for Opportunities.
                if ((parent != null) && (task.isOpportunity())) {
                    // check for dates
                    Date validTo = Formatter.parseDate(validToStr);
                    Date useDateTo = Formatter.parseDate(useDateStr);
                    validFrom = subtractDaysFromDate(validTo, Formatter.parseInt(mdtaskPeriod));
                    opportunityDateErrorMsg = checkForDates(parent, task, parent.getMain().getValidto(), validFrom, validTo, useDateTo); 
                    

                    // check for sums
                    upperLimitSumRoubles =  
                        c.computeRate(
                             parent.getMain().getSum(),
                             subtractDaysFromDate(parent.getMain().getValidto(), parent.getMain().getPeriod()), 
                             parent.getMain().getCurrency2().getCode());
                    
                    List<Task> siblings = findSiblings(parent, p); 
                    childrenSumsRoubles = calculateSumOfChildren(siblings, c);
                    
                    oldThisSumsRoubles = 
                        c.computeRate(
                             task.getMain().getSum(),
                             subtractDaysFromDate(task.getMain().getValidto(), task.getMain().getPeriod()),                  
                             task.getMain().getCurrency2().getCode());
                    
                    thisSumRoubles = 
                        c.computeRate(
                             Formatter.parseBigDecimal(limit_sum),
                             validFrom, 
                             currency);
                
                } else if (!task.isOpportunity()) {
                    Date validTo = Formatter.parseDate(validToStr);
                    validFrom = subtractDaysFromDate(validTo, Formatter.parseInt(mdtaskPeriod));

                    // check for sums
                    // for limits and sublimits finds all of the opportunities of the level below 
                    // upperLimitSumRoubles is 0.0 and is not used here 
                    // oldThisSumsRoubles is 0.0 and is not used here
                    isOpportunity = false;
                    List<Task> siblings = findSiblings(task, p);
                    childrenSumsRoubles = calculateSumOfChildren(siblings, c);
                    
                    thisSumRoubles = 
                        c.computeRate(
                                Formatter.parseBigDecimal(limit_sum),
                                validFrom,
                                currency);
                    
                    // check for dates
                    opportunityDateErrorMsg = checkSiblingsDates(siblings, task, validTo);
                }
            } 
            
        } catch(Exception e){
            logger.info("AJAX validateOnSave.do ERROR: " + e.getMessage());
        }

        // кладем результаты в XML файл
        String format = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                +       "<data><upperLimitSumRoubles>{0}</upperLimitSumRoubles>"
                +             "<childrenSumsRoubles>{1}</childrenSumsRoubles>"
                +             "<thisSumRoubles>{2}</thisSumRoubles>"
                +             "<oldThisSumsRoubles>{3}</oldThisSumsRoubles>"
                +             "<opportunity>{4}</opportunity><opportunityDateErrorMsg>{5}</opportunityDateErrorMsg></data>";
        String result = 
            MessageFormat.format(format, 
                Formatter.toMoneyFormat(upperLimitSumRoubles), 
                Formatter.toMoneyFormat(childrenSumsRoubles),
                Formatter.toMoneyFormat(thisSumRoubles),
                Formatter.toMoneyFormat(oldThisSumsRoubles),
                isOpportunity,
                opportunityDateErrorMsg.toString()
        );

        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result);
        logger.info("AJAX validateOnSave.do answer: " + result);
        return null;
    }

    /**
     * subtract days (mdtaskPeriod) from date (validToDate) 
     * @param validToDate
     * @param mdtaskPeriod
     * @return
     */
    private Date subtractDaysFromDate(Date validToDate, Integer mdtaskPeriod) {
        int period = (mdtaskPeriod != null) ? mdtaskPeriod.intValue() : 0; 
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(validToDate);
            cal.add(Calendar.DATE, - period);
            return cal.getTime();
        } catch (Exception e) {
            return null;
        }        
    }
    
    /**
     * Finds sum in roubles of all childredn of the task (if present).
     * @param siblings list of siblings for those we need to find the sum.   
     */
    private BigDecimal calculateSumOfChildren(List<Task> siblings, CompendiumCrmActionProcessor c) {
        BigDecimal sum = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        try {
            if (siblings != null)
                for (Task son : siblings) {
                   BigDecimal augment = 
                       c.computeRate(son.getMain().getSum(), 
                                     subtractDaysFromDate(
                                         son.getMain().getValidto(), 
                                         son.getMain().getPeriod()
                                      ),                  
                                     son.getMain().getCurrency2().getCode());
                   sum = sum.add(augment);
                }
        } catch (Exception e) {
            logger.severe("calculateSumOfChildren error: " + e.getMessage());
        }
        return sum;
    }
    
    /**
     * Checks for dates for all children of the task (if present).
     * @param siblings list of siblings.
     * @param parent parent of the opportunities.   
     */
    private StringBuilder checkSiblingsDates(List<Task> siblings, Task parent, Date parentDateTo) {
        StringBuilder sb = new StringBuilder();
        if (siblings != null)
            try {
                for (Task son : siblings) {
                    Date validTo = son.getMain().getValidto();
                    Date validFrom = subtractDaysFromDate(validTo, son.getMain().getPeriod());
                    Date useDateTo = son.getMain().getUsedate();
                    sb.append(
                         checkForDates(parent, son, parentDateTo, validFrom, validTo, useDateTo).toString());
                }
            } catch (Exception e) {
                logger.severe("checkSiblingsDates error: " + e.getMessage());
            }
        return sb;
    }
    
    private StringBuilder checkForDates(Task parent, Task son, Date parentValidTo, Date validFrom, Date validTo, Date useDateTo) {
        StringBuilder sb = new StringBuilder(); 
        if ((parentValidTo == null) || (validFrom == null) || (!validFrom.before(parentValidTo))) {
            sb.append(
                 MessageFormat.format(
                     "Дата начала действия {0} сделки №{1} должна быть меньше или равна дате окончания действия {2} (суб)лимита №{3}.END_OF_LINE", 
                     Formatter.format(validFrom), 
                     son.getHeader().getCombinedNumber(),
                     Formatter.format(parentValidTo),
                     parent.getHeader().getCombinedNumber()
             ));
        }
        
        if ((parentValidTo == null) || (validTo == null) || (!validTo.before(parentValidTo))) {
            sb.append(
                 MessageFormat.format(
                     "Дата окончания действия {0} сделки №{1} должна быть меньше или равна дате окончания действия {2} (суб)лимита №{3}.END_OF_LINE", 
                     Formatter.format(validTo), 
                     son.getHeader().getCombinedNumber(),
                     Formatter.format(parentValidTo),
                     parent.getHeader().getCombinedNumber()
            ));
        }
        // проверим только для тех useDateTo, которые были заполнены
        if ((parentValidTo == null) || 
              ((useDateTo != null) && (!useDateTo.before(parentValidTo)))) {
            sb.append(
                MessageFormat.format(
                     "Дата окончания использования {0} сделки №{1} должна быть меньше или равна дате окончания действия {2} (суб)лимита №{3}.END_OF_LINE", 
                     Formatter.format(useDateTo), 
                     son.getHeader().getCombinedNumber(),
                     Formatter.format(parentValidTo),
                     parent.getHeader().getCombinedNumber()
            ));
        }
        return sb;
    }
    
    /**
     * Looks for the parent of the task. 2 different cases : for limit(sublimit) and for opportunity. 
     * @throws MappingException 
     * @throws ModelException 
     */
    private Task findParent(Task t, TaskActionProcessor p) throws MappingException, ModelException {
        if (t.isOpportunity()) {
            // get parent for opportunity, if has parent and parent was loaded from CRM
            if ((t.getInLimit() == null) || t.getInLimit().equals("")) return null;
            else {
                if (p.isCRMLimitLoaded(t.getInLimit())) return p.findByCRMid(t.getInLimit());
                // wasn't loaded
                else return null; 
            }       
        } else {
            // get parent for limit or sublimit, if present
            if((t.getParent() == null) || (t.getParent().longValue() == 0))  return null;
            else return p.getTask(new Task(t.getParent())); 
        }
    }

    /**
     * Generates list of siblings
     * @param parent parent of the opportunities to find.
     */
    private List<Task> findSiblings(Task parent, TaskActionProcessor p) { 
        List<Task> siblings = new ArrayList<Task>();
        if (parent != null) {
            try {
                // adds all children of type limit or sublimit
                // in this version we don't include the sublimits here!!!
                //siblings.addAll(p.findTaskByParent(parent.getId_task(), false, false));
                // add all opportunities linked to the limit (sublimit), if parent was loaded 
                siblings.addAll(p.findChildrenOfCRMid(parent.getHeader().getCrmid(), false));
            } catch (ModelException e) {
                logger.severe("findSiblings error: " + e.getMessage());
            }
        }
        return siblings;
    }
}

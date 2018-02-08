package org.uit.director.action;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.ProcessControlType;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;
import org.uit.director.decider.BusinessProcessDecider;
import org.uit.director.tasks.AttributesStructList;
import org.uit.director.tasks.ProcessInfo;
import org.uit.director.tasks.TaskInfo;

import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.util.Config;

public class UpdateAttributesAction extends Action {
    private static final Logger LOGGER = Logger.getLogger(UpdateAttributesAction.class.getName());
            
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String target = "acceptedTasks";
        String idTask = request.getParameter("idTask");
        String classAction = request.getParameter("class");
        String comment = request.getParameter("comment");
        String isWithComplete = request.getParameter("isWithComplete");
        String isOnlyUpdate = request.getParameter("isOnlyUpdate");
        String isEditMode = request.getParameter("isEditMode");
        boolean isEditModeB = isEditMode.equalsIgnoreCase("true");
        String isAssign = request.getParameter("isAssign");

        WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);

        if (wsc.isNewContext())
            return (mapping.findForward("start"));
        DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager().getDbFlexDirector();
        String sign = request.getParameter("sign0");
        if (sign != null)
            wsc.setSignum(sign);

        try {

            long idTaskLong;
            try {
                idTaskLong = Long.parseLong(idTask);
            } catch (Exception e) {
                /* Если переходим на страницу после инициализации процесса */
                idTaskLong = wsc.getIdCurrTask();
                idTask = String.valueOf(idTaskLong);
            }

            ArrayList<Object[]> param = new ArrayList<Object[]>();
            // List<Object[]> paramsForDelete = new ArrayList<Object[]>();

            ProcessInfo taskInfo = null;
            if (isEditModeB) {
                taskInfo = wsc.getCurrEditProcessInfo();
                wsc.setCurrEditProcessInfo(null);
            } else {
                taskInfo = wsc.getTaskList().findTaskInfo(idTaskLong);
            }
            AttributesStructList attribList = taskInfo.getAttributes();
            Iterator<BasicAttribute> it = attribList.getIterator();

            while (it.hasNext()) {
                BasicAttribute attributStruct = it.next();
                Attribute attribut = attributStruct.getAttribute();
                String attributName = attribut.getNameVariable();
                List<String> attributValue = attribut.getValueAttributeList();
                String[] attrFromPage = (String[]) request.getParameterMap().get(attributName);
                if (attributName.equals("Сумма лимита")) {
                    // для суммы нужно убрать форматирование
                    attrFromPage = reformatSum(attrFromPage);
                }
                // String attributType = attribut.getTypeVar();
                if (attrFromPage != null && !compareAttributes(attrFromPage, attributValue)) {
                // если на странице есть такой атрибут и новые значения не равны
                // старым

                    for (int j = 0; j < attrFromPage.length; j++) {
                        String attrFromPageStr = attrFromPage[j];
                        attrFromPageStr = attrFromPageStr.trim();

                        if (BusinessProcessDecider.checkValue(attribut, attrFromPageStr)) {
                            Object[] par = new Object[3];
                            par[0] = taskInfo.getIdProcess();
                            par[1] = attribut.getIdVariable();
                            par[2] = attrFromPageStr;
                            // par[3] = new Long(0); // резервное поле для
                            // идентификатора
                            // транзакции
                            param.add(par);
                        } else {
                            wsc.setErrorMessage("Неверный формат введенных данных в поле '" + attributName + "'");
                            target = "errorPage";
                            // dbFlexDirector.rollback();
                            return (mapping.findForward(target));
                        }
                    }
                }

            }

            ProcessControlType controlType = WPC.getInstance().getControlType(taskInfo.getIdTypeProcess());

            if (isEditModeB) {

                String resUpd = dbFlexDirector.updateAttributesControl(param, wsc.getIdUser(), request.getRemoteAddr(), "UpdateAttributes",
                        controlType);
                if (resUpd.startsWith("Error")) {
                    wsc.setErrorMessage(resUpd);
                    return mapping.findForward("errorPage");
                } else {
                    wsc.setWarningMessage(resUpd);
                }
            } else {

                if (param != null && param.size() > 0) {
                    String resUpd = dbFlexDirector.updateAttributes(param, controlType);
                    if (!resUpd.equalsIgnoreCase("ok")) {
                        if (resUpd.startsWith("Error")) {
                            wsc.setErrorMessage(resUpd);
                            return mapping.findForward("errorPage");
                        } else {
                            wsc.setWarningMessage(resUpd);
                        }

                    }
                }
                setComment(wsc, taskInfo, comment);
            }

            if (idTaskLong == -1)
                wsc.getTaskList().deleteTaskInfo(-1);
            else
                wsc.getCacheManager().deleteCacheElement(idTaskLong);

            if (classAction != null && isOnlyUpdate != null && isOnlyUpdate.equals("false")) {
                request.setAttribute("class", classAction);
                target = "execPlugin";
            }

            if (isWithComplete.equals("true")) {
                if (sign == null || sign.equals("")) {
                    if (Config.getProperty("VALIDATE_SIGNUM").equalsIgnoreCase("true")) {
                        wsc.setErrorMessage("Действие не подписано, операция не может быть выполнена");
                        return (mapping.findForward("errorPage"));
                    } else {

                        sign = "NULL_String";
                    }
                }

                String url = "task.complete.do?id0=" + idTask+ "&export2cc="+request.getParameter("export2cc")+
                    "&refuseMode="+request.getParameter("refuseMode");
                LOGGER.warning("sendRedirect " + url);
                response.sendRedirect(url);
                return null;
            }

            if (isAssign != null && isAssign.equals("true")) {
                if (sign == null || sign.equals("")) {
                    if (Config.getProperty("VALIDATE_SIGNUM").equalsIgnoreCase("true")) {
                        wsc.setErrorMessage("Действие не подписано, операция не может быть выполнена");
                        return (mapping.findForward("errorPage"));
                    } else {
                        sign = "NULL_String";
                    }
                }

                if (request.getParameter("mayReAssign") == null)
                    response.sendRedirect("assign.user.do?idTask=" + idTask + "&idUser=" + request.getParameter("idUser") + "&idRole="
                            + request.getParameter("idRole"));
                else
                    response.sendRedirect("assign.user.do?idTask=" + idTask + "&idUser=" + request.getParameter("idUser") + "&idRole="
                            + request.getParameter("idRole") + "&mayReAssign=" + request.getParameter("mayReAssign"));
                return null;
            }

            if (isEditModeB) {
                response.sendRedirect("report.do?classReport=org.uit.director.report.mainreports.SearchProcessReport&par1=edit");
                return null;

            }
            // dbFlexDirector.commit();

        } catch (Exception e) {
            e.printStackTrace();
            wsc.setErrorMessage("Ошибка сохранения атрибутов: " + e.getMessage());
            target = "errorPage";
            // dbFlexDirector.rollback();

        }

      //Если это сохранение лимита или саблимита, то переходим к лимиту
        ActionForward forward = new ActionForward();
        String redirecturl = request.getParameter("redirecturl");
        forward.setPath(redirecturl);//переадресация на сам лимит на случай Exception
        // Если это добавление сублимита
        /*String sublimitparam = request.getParameter("sublimit"); 
        if (sublimitparam != null && !sublimitparam.equalsIgnoreCase("0")) {
            LOGGER.info("param sublimit = " + sublimitparam);
            //форвард
            try {
                PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
                Long maxnumber=pupFacadeLocal.getLastSublimitId(new Long(sublimitparam));
                forward.setPath("/task.context.do?newsublimit=true&id="+idTask+"&mdtaskid="+maxnumber);
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }
            return forward;
        }*/
        if(redirecturl!=null && !redirecturl.isEmpty() && !redirecturl.startsWith("/"))
        	response.sendRedirect(redirecturl);
        
        return forward;
    }

    private String[] reformatSum(String[] values) throws ParseException {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("ru", "RU", ""));
        DecimalFormat d = new DecimalFormat("###,###,###,###.##", symbols);
        String[] res = new String[values.length];
        int i = 0;
        for (String value : values) {
            if (value != null && value.length() > 0)
                res[i]= d.parse(value).toString();
            else
                res[i]= value;
            i++;
        }
        return res;
    }

    // сравнить новые значения атрибута со старыми
    private boolean compareAttributes(String[] newValues, List<String> oldValues) {

        if (newValues.length > 0 && oldValues != null && newValues.length == oldValues.size()) {
            for (int i = 0; i < newValues.length; i++) {

                if (!newValues[i].trim().equals(oldValues.get(i))) {
                    return false;
                }

            }

            return true;

        }
        return false;
    }

    private void setComment(WorkflowSessionContext wsc, ProcessInfo taskInfo, String comment) throws RemoteException {

        if (comment != null) {
            if (!comment.trim().equals("")) {
                //				String userName = wsc.getFullUserName();

                wsc.getDbManager().getDbFlexDirector().setComment(taskInfo.getIdProcess(), ((TaskInfo) taskInfo).getIdStageTo(), wsc.getIdUser(),
                        comment);

            }
        }
    }

}
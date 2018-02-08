package org.uit.director.tasks;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.uit.director.contexts.WPC;
import org.uit.director.contexts.WorkflowSessionContext;
import org.uit.director.db.dbobjects.Attribute;
import org.uit.director.db.dbobjects.BasicAttribute;
import org.uit.director.db.dbobjects.Cnst;
import org.uit.director.db.dbobjects.StatusProcess;
import org.uit.director.db.dbobjects.VariablesType;
import org.uit.director.db.dbobjects.VarsPermissions;
import org.uit.director.db.dbobjects.VarsPermissions.Permission;
import org.uit.director.db.dbobjects.WorkflowProcessInfo;
import org.uit.director.db.dbobjects.WorkflowVariables;
import org.uit.director.db.ejb.DBFlexWorkflowCommon;

import ru.md.spo.util.Config;

import com.vtb.util.Formatter;

/**
 * Created by IntelliJ IDEA. User: pd190390 Date: 12.12.2005 Time: 10:37:31 To
 * change this template use File | Settings | File Templates.
 */
public class ProcessInfo implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    protected final SimpleDateFormat dateFormater = new SimpleDateFormat(Config.getProperty("DATE_TIME_FORMAT"));

    protected final SimpleDateFormat dateFormaterDb = new SimpleDateFormat(Config.getProperty("DATE_TIME_DB_FORMAT"));

    protected AttributesStructList attributes;

    protected WorkflowSessionContext wsc;

    protected Long timeToLife; // время жизни кешируемого объекта ( в милисекундах)
    //protected List comments;

    protected boolean isAll;

    protected Long idUser;

    protected WorkflowProcessInfo wfpInfo;

    protected List<Attribute> reqAttrs;

    protected ArrayList<Object[]> varParams;

    public boolean isExpired=false; // является ли просроченным

    /**
     * 
     */
    public ProcessInfo() {
        super();
    }

    public void init(WorkflowSessionContext wsc, Long idProcess, Long idUser,
            boolean isAll) {

        this.wsc = wsc;
        attributes = new AttributesStructList();
        this.idUser = idUser;
        this.isAll = isAll;
        wfpInfo = new WorkflowProcessInfo(idProcess);
        reqAttrs = new ArrayList<Attribute>();
        varParams = new ArrayList<Object[]>();

    }

    public void init(WorkflowSessionContext wsc2, boolean isAll2) {
        wsc = wsc2;
        attributes = new AttributesStructList();
        isAll = isAll2;
        wfpInfo = new WorkflowProcessInfo(null);
        reqAttrs = new ArrayList<Attribute>();
        varParams = new ArrayList<Object[]>();

    }

    public String execute() {
        String res = "error";

        try {

            DBFlexWorkflowCommon dbFlexDirector = wsc.getDbManager()
                    .getDbFlexDirector();

            wfpInfo = dbFlexDirector.getProcessInfo(wfpInfo.getIdProcess());

            setIsExpired();

            getRequeredAttributesAndParams();

            // создадим структуру атрибутов
            List<BasicAttribute> attrStruct = new ArrayList<BasicAttribute>();
            addSystemVariables(attrStruct);

            if (varParams.size() > 0) {
                // получим значения атрибутов
                HashMap<Long, ArrayList<String>> attrib = dbFlexDirector
                        .getAttributes(varParams);

                // проставим значения атрибутов
                for (Attribute attr : reqAttrs) {
                    ArrayList<String> value = attrib.get(attr.getId());
                    if (value != null) {
                        if (value.size() == 1) {
                            attr.setValueAttributeStr(value.get(0));
                        } else {
                            attr.setValueAttribute(value);
                        }

                    }
                }

                // создадим структуру атрибутов
                attrStruct.addAll(WPC.getInstance().createAttributesStructList(
                        reqAttrs));

            }

            attributes.setAttrStructures(attrStruct);
            res = "ok";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private void addSystemVariables(List<BasicAttribute> attrStruct) {

        for (int i = 0; i < WPC.getInstance().getDirectVars().size(); i++) {
            String directVar = WPC.getInstance().getDirectVars().get(i);
            Attribute atr = new Attribute();
            atr.setName(directVar);
            atr.setTypeVar(new VariablesType(VariablesType.STRING));
            switch(i) {
            case 0: {
                atr.setValueAttributeStr(getDateInitProcess());
                break;
            }
            case 1: {
                atr.setValueAttributeStr(getDateCompleteProcess());
                break;
            }
            case 2: {
                atr.setValueAttributeStr(String.valueOf(getStatusProcess().value));
                break;
            }
            case 3: {
                atr.setValueAttributeStr(String.valueOf(getIdTypeProcess()));
                break;
            }
            case 4: {
                atr.setValueAttributeStr("");
                break;
            }
            case 5: {
                atr.setValueAttributeStr(String.valueOf(getCountActiveStages()));
                break;
            }
            case 6: {
                String stages = "";
                List<Long> listActiveStages = getActiveStages();
                if (listActiveStages != null) {
                    for (Long st: listActiveStages) {
                        stages += String.valueOf(st) + ",";
                    }
                }
                atr.setValueAttributeStr(stages);
                break;
            }
            }
            
            BasicAttribute as = new BasicAttribute(atr);
            attrStruct.add(as);
        }

    }

    protected void getRequeredAttributesAndParams() {
        if (isAll) {
            getAllAttributesAndParams();
            return;
        }
        List<Long> roles = WPC.getInstance().getIDRolesForUser(idUser);
        List<Long> varSet = new ArrayList<Long>();
        // нет прав
        if (roles == null) {
            return;
        }
        // определим допустимые атрибуты для заданного пользователя
        for (Long idRole : roles) {
            // исключим роли, не относящиеся к текущему типу процесса
            if (!WPC.getInstance().getRolesInTypeProcess().get(
                    wfpInfo.getIdTypeProcess()).contains(idRole)) {
                continue;
            }
            VarsPermissions vars = WPC.getInstance().getRolesPermissions(idRole);
            if (vars != null) {
                HashMap<Long, HashSet<Permission>> varPermissions = vars.getVarPermissions();
                Iterator<Long> it = varPermissions.keySet().iterator();
                while (it.hasNext()) {
                    Long idVar = it.next();
                    HashSet<Permission> setVp = varPermissions.get(idVar);
                    if (!varSet.contains(idVar)) {
                        Attribute attr = new Attribute(null, setVp, Config.getProperty("DATE_TIME_DB_FORMAT"), 
                                Config.getProperty("DATE_TIME_FORMAT"), Config.getProperty("DATE_FORMAT"));
                        attr.setWorkflowVariable(WPC.getInstance().findVariableById(idVar));
                        reqAttrs.add(attr);
                        varSet.add(idVar);
                        Object[] o = new Object[2];
                        o[0] = getIdProcess();
                        o[1] = idVar;
                        varParams.add(o);
                    }
                }
            } else
                System.out.println("no vars permissions for role " + idRole.longValue());
        }
    }

    public void getAllAttributesAndParams() {

        List<WorkflowVariables> vars = WPC.getInstance().getVariables();
        int idTypeProcess = getIdTypeProcess().intValue();

        for (WorkflowVariables v : vars) {

            if (v.getIdTypeProcess().intValue() == idTypeProcess) {

                HashSet<Permission> setVp = getFullPermissions();

                Attribute attr = new Attribute(null, setVp, Config
                        .getProperty("DATE_TIME_DB_FORMAT"), Config
                        .getProperty("DATE_TIME_FORMAT"), Config
                        .getProperty("DATE_FORMAT"));

                attr.setWorkflowVariable(v);
                reqAttrs.add(attr);
                Object[] o = new Object[2];
                o[0] = getIdProcess();
                o[1] = v.getIdVariable();
                varParams.add(o);

            }
        }

    }

    private HashSet<Permission> getFullPermissions() {
        HashSet<Permission> res = new HashSet<Permission>();
        res.add(new Permission(Permission.VIEW_MAIN));
        res.add(new Permission(Permission.VIEW_ADDITION));
        res.add(new Permission(Permission.EDIT));
        return res;
    }

    public AttributesStructList getAttributes() {
        return attributes;
    }

    public String getNameTypeProcess() {
        return (String) WPC.getInstance().getData(Cnst.TBLS.typeProcesses,
                wfpInfo.getIdTypeProcess().longValue(), Cnst.TTypeProc.name);
    }

    public Integer getIdTypeProcess() {

        return wfpInfo.getIdTypeProcess();
    }

    public Long getTimeToLife() {
        return timeToLife;
    }

    public void setTimeToLife(long timeToLife) {
        this.timeToLife = timeToLife;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        return super.clone();

    }

    public List<Long> getActiveStages() {
        return wfpInfo.getActiveStages();
    }

    public int getCountActiveStages() {
        return wfpInfo.getCountActiveStages();
    }

    public int getCountExecute() {
        return wfpInfo.getCountExecute();
    }

    public String getDateCompleteProcess() {
        return wfpInfo.getDateCompleteProcess();
    }

    public String getDateInitProcess() {
        return wfpInfo.getDateInitProcess();
    }

    public Long getIdProcess() {
        return wfpInfo.getIdProcess();
    }

    public Long getIdUser() {
        return idUser;
    }

    public boolean isAll() {
        return isAll;
    }

    public StatusProcess getStatusProcess() {
        return wfpInfo.getStatusProcess();
    }

    public void setActiveStages(List<Long> activeStages) {
        wfpInfo.setActiveStages(activeStages);
    }

    public void setAttributes(AttributesStructList attributes) {
        this.attributes = attributes;
    }

    public void setCountActiveStages(int countActiveStages) {
        wfpInfo.setCountActiveStages(countActiveStages);
    }

    public void setCountExecute(int countExecute) {
        wfpInfo.setCountExecute(countExecute);
    }

    public void setDateCompleteProcess(String dateCompleteProcess) {
        wfpInfo.setDateCompleteProcess(dateCompleteProcess);
    }

    public void setDateInitProcess(String dateInitProcess) {
        wfpInfo.setDateInitProcess(dateInitProcess);
    }

    public void setIdProcess(Long idProcess) {
        wfpInfo.setIdProcess(idProcess);
    }

    public void setIdTypeProcess(Integer idTypeProcess) {
        wfpInfo.setIdTypeProcess(idTypeProcess);
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public void setAll(boolean isAll) {
        this.isAll = isAll;
    }

    public void setStatusProcess(StatusProcess statusProcess) {
        wfpInfo.setStatusProcess(statusProcess);
    }

    public Long getIdParentProcess() {
        return wfpInfo.getIdParentProcess();
    }

    private void setIsExpired() throws ParseException {
        /* определим срок выполнения процесса */
        try{
            int limitDayProcess = getCountExecute();
            String strDateOfStartProcess = getDateInitProcess();
            Date dateOfStartProcess = Formatter.parseDateRobust(strDateOfStartProcess);
            Calendar calMustEndProc = Calendar.getInstance();
            calMustEndProc.setTime(dateOfStartProcess);
            calMustEndProc.add(Calendar.DAY_OF_MONTH, limitDayProcess);
            isExpired = false;
            if (Calendar.getInstance().after(calMustEndProc))
                isExpired = true;
        }catch(Exception e){}
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    public String getColorProcess() {
        if (!this.isExpired)
            return Config.getProperty("COLOR_NOT_EXPIRED");
        else
            return Config.getProperty("COLOR_EXPIRED_PROCESS");
    }

}

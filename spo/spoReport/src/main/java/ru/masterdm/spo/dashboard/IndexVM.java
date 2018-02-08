package ru.masterdm.spo.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uit.director.action.AbstractAction;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;

import com.google.gson.Gson;
import ru.md.domain.ReportSetting;
import ru.md.domain.User;

import ru.masterdm.spo.utils.Formatter;
import ru.masterdm.spo.utils.SBeanLocator;

//@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class IndexVM {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexVM.class);

    private String reportName;
    private Long reportId;
    private Long pub;
    private String reportName2;
    private String reportNameNew;
    private String reportNameErrorMsg = "";
    NavigationPage currentPage;
    private Map<String, Map<String, NavigationPage>> pageMap;
    private User currentUser;

    public String getCurrentHeader() {
        if (currentPage==null)
            return "";
        return currentPage.getFullTitle();
    }
    public String getCurrentUser() {
        if (this.currentUser == null)
            return "";
        //ГО //Иванов Иван Иванович (Администратор системы) -- образец
        return "//" + this.currentUser.getDepname() + " //" + this.currentUser.getFullName();
    }

    @Init
    public void init(@ContextParam(ContextType.EXECUTION) Execution execution) throws Exception {
        initPageMap();
        HttpServletRequest request = (HttpServletRequest) execution.getNativeRequest();
        this.currentUser = AbstractAction.getUser(request);
        if (this.currentUser == null)
            throw new Exception("Пользователь с логином " + request.getRemoteUser() + " не найден в системе");
        String settingJson = SBeanLocator.getDashboardMapper().getPipelineSettings(
                PipelineSettings.SETTING_USER==null?this.currentUser.getId():PipelineSettings.SETTING_USER);
        if (!Formatter.str(settingJson).isEmpty()) {
            Gson gson = new Gson();
            HashMap<String, String> map = new HashMap<String, String>();
            map = gson.fromJson(settingJson, map.getClass());
            String cd = Formatter.str(map.get("mainMenuSelection-creditDocumentary"));
            String title = (cd==null||cd.isEmpty()||cd.equals("0"))?PipelineConstants.CreditDocumentary.ALL:
                           (cd.equals("1")?PipelineConstants.CreditDocumentary.CREDIT:PipelineConstants.CreditDocumentary.DOCUMENTARY);
            String tt = Formatter.str(map.get("mainMenuSelection-taskType"));
            String stitle = tt.equalsIgnoreCase("limit")?"Лимиты":(tt.isEmpty()||tt.equalsIgnoreCase("product"))?"Сделки":
                          tt.equalsIgnoreCase("waiver")?"Изменённые и вейверы":"Кросс-селл";
                    currentPage = pageMap.get(title).get(stitle);
            if (currentPage==null)
                currentPage = pageMap.get("Все").get("Сделки");
        } else {
            currentPage = pageMap.get("Все").get("Сделки");
        }
    }

    @Command
    @NotifyChange({"taskType", "creditDocumentary", "includeUri", "currentHeader"})
    public void navigatePage(@BindingParam("target") NavigationPage targetPage, @BindingParam("c") Component c) {
        currentPage = targetPage;

        Component inc = c.query("#centerHBox");
        inc.invalidate();
    }

    public NavigationPage getCurrentPage() {
        return currentPage;
    }

    public String getIncludeUri() {
        return currentPage.getIncludeUri();
    }

    public String getTaskType() {
        return currentPage.getTaskType();
    }

    public Integer getCreditDocumentary() {
        return currentPage.getCreditDocumentary();
    }

    public List<ReportSetting> getPersonalReports() {
        if (currentUser == null)
            return new ArrayList<ReportSetting>();
        return SBeanLocator.getDashboardMapper().getNamedPipelineSettings(currentUser.getId());
    }
    public List<ReportSetting> getPubReports() {
        return SBeanLocator.getDashboardMapper().getNamedPubPipelineSettings();
    }
    @Command
    @NotifyChange({"personalReports","pubReports","reportName","reportNameErrorMsg","reportNameNew"})
    public void reportNameSetter(@BindingParam("rn") Long rn, @BindingParam("pub") Long pub) {
        reportId = rn;
        this.pub = pub;
        reportName2 = "";
        reportNameErrorMsg = "";
        reportNameNew = "";
        if (reportId != null && reportId > 0)
            reportNameNew = SBeanLocator.getDashboardMapper().getPipelineSetting(reportId).getName();
        reportName = reportNameNew;
        LOGGER.info("reportNameSetter reportId "+reportId);
        LOGGER.info("reportNameSetter reportNameNew "+reportNameNew);
    }
    /**валидатор название не пустое и не повторяется*/
    private boolean isNameValid(String name, boolean pub){
        reportNameErrorMsg = "";
        if (Formatter.str(name).isEmpty()){
            reportNameErrorMsg = "Название конфигурации должно быть не пустое";
            return false;
        }
        if (Formatter.str(name).length()>25){
            reportNameErrorMsg = "Название конфигурации должно быть не больше 25 символов";
            return false;
        }
        if (pub)
            return true;
        for (ReportSetting rs : SBeanLocator.getDashboardMapper().getNamedPipelineSettings(currentUser.getId()))
            if (rs.getName().equals(name)) {
                reportNameErrorMsg = "Личная конфигурация с таким именем уже существует";
                return false;
            }
        return true;
    }
    @Command
    @NotifyChange({"personalReports","pubReports","reportName","reportNameErrorMsg"})
    public void reportSave() {
        LOGGER.info("reportSave "+reportName);
        reportName = Formatter.str(reportName).trim();
        if (isNameValid(reportName, false)) {
            SBeanLocator.getDashboardMapper().savePipelineSettingsByName(currentUser.getId(), reportName);
            reportName = "";
        }
    }
    @Command
    @NotifyChange({"pubReports","personalReports"})
    public void reportDel() {
        LOGGER.info("reportDel "+reportId);
        SBeanLocator.getDashboardMapper().delPipelineSettings(reportId);
    }
    @Command
    @NotifyChange({"personalReports","pubReports","reportName","reportName2","reportNameErrorMsg","reportNameNew"})
    public void reportCopy() {
        LOGGER.info("reportCopy "+reportName);
        LOGGER.info("reportCopy "+reportName2);
        reportName2 = Formatter.str(reportName2).trim();
        if (isNameValid(reportName2, false)) {
            SBeanLocator.getDashboardMapper().copyPipelineSettingsByName(currentUser.getId(), reportName, reportName2, 0);
            reportName = "";
            reportName2 = "";
        }
    }
    @Command
    @NotifyChange({"personalReports","pubReports","reportName","reportName2","reportNameErrorMsg","reportNameNew"})
    public void reportRename() {
        LOGGER.info("reportRename "+reportName);
        LOGGER.info("reportRename "+reportNameNew);
        reportNameNew = Formatter.str(reportNameNew).trim();
        if (isNameValid(reportNameNew, pub != null && pub.equals(1L))) {
            if(pub == null || pub.equals(0L))
                SBeanLocator.getDashboardMapper().renamePipelineSettingsByName(currentUser.getId(), reportName, reportNameNew);
            else
                SBeanLocator.getDashboardMapper().renamePipelinePubSettingsByName(reportName, reportNameNew);
            reportName = "";
            reportNameNew = "";
        }
    }
    @Command
    @NotifyChange({"personalReports","pubReports","reportName","reportName2","reportNameErrorMsg","reportNameNew"})
    public void reportShare() {
        LOGGER.info("reportShare "+reportName);
        LOGGER.info("reportShare "+reportName2);
        reportName2 = Formatter.str(reportName2).trim();
        if (isNameValid(reportName2, true)) {
            SBeanLocator.getDashboardMapper().copyPipelineSettingsByName(currentUser.getId(), reportName, reportName2, 1);
            reportName = "";
            reportName2 = "";
        }
    }
    public Map<String, Map<String, NavigationPage>> getPageMap() {
        return pageMap;
    }

    private void initPageMap() {
        pageMap = new LinkedHashMap<String, Map<String, NavigationPage>>();

        addPage(PipelineConstants.CreditDocumentary.ALL, "Сделки", "product");
        addPage(PipelineConstants.CreditDocumentary.ALL, "Лимиты", "limit");
        addPage(PipelineConstants.CreditDocumentary.ALL, "Измененные сделки и вейверы", "waiver");
        addPage(PipelineConstants.CreditDocumentary.ALL, "Кросс-селл", "cross-sell");

        // 1 - кредитная, 2 - документарная,
        addPage(PipelineConstants.CreditDocumentary.CREDIT, "Сделки", "product", 1);
        addPage(PipelineConstants.CreditDocumentary.CREDIT, "Лимиты", "limit", 1);
        addPage(PipelineConstants.CreditDocumentary.CREDIT, "Измененные сделки и вейверы", "waiver", 1);

        addPage(PipelineConstants.CreditDocumentary.DOCUMENTARY, "Сделки", "product", 2);
        addPage(PipelineConstants.CreditDocumentary.DOCUMENTARY, "Лимиты", "limit", 2);
        addPage(PipelineConstants.CreditDocumentary.DOCUMENTARY, "Измененные сделки и вейверы", "waiver", 2);
    }

    private void addPage(String title, String subTitle) {
        addPage(title, subTitle, null);
    }

    private void addPage(String title, String subTitle, String taskType) {
        addPage(title, subTitle, taskType, null);
    }

    private void addPage(String title, String subTitle, String taskType, Integer creditDocumentary) {
        Map<String, NavigationPage> subPageMap = pageMap.get(title);
        if (subPageMap == null) {
            subPageMap = new LinkedHashMap<String, NavigationPage>();
            pageMap.put(title, subPageMap);
        }
        NavigationPage navigationPage = new NavigationPage(title, subTitle,
                                                           "/widgets/pipeline.zul?random=" + Math.random(), taskType, creditDocumentary) {

            @Override
            //@NotifyChange(".")
            public boolean isSelected() {
                return currentPage == this;
            }
        };
        subPageMap.put(subTitle, navigationPage);
    }

    /**
     * Returns .
     * @return
     */
    public String getReportName() {
        return reportName;
    }

    /**
     * Sets .
     * @param reportName
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    /**
     * Returns .
     * @return
     */
    public String getReportName2() {
        return reportName2;
    }

    /**
     * Sets .
     * @param reportName2
     */
    public void setReportName2(String reportName2) {
        this.reportName2 = reportName2;
    }

    public String getReportNameNew() {
        return reportNameNew;
    }
    public void setReportNameNew(String reportNameNew) {
        this.reportNameNew = reportNameNew;
    }

    /**
     * Returns .
     * @return
     */
    public String getReportNameErrorMsg() {
        return reportNameErrorMsg;
    }

    /**
     * Sets .
     * @param reportNameErrorMsg
     */
    public void setReportNameErrorMsg(String reportNameErrorMsg) {
        this.reportNameErrorMsg = reportNameErrorMsg;
    }

    /**
     * Returns .
     * @return
     */
    public Long getPub() {
        return pub;
    }

    /**
     * Sets .
     * @param pub
     */
    public void setPub(Long pub) {
        this.pub = pub;
    }
}

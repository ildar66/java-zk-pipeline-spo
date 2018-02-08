package ru.masterdm.spo.pipeline;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Default;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.QueryParam;
import org.zkoss.bind.annotation.SmartNotifyChange;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Include;
import org.zkoss.zul.Textbox;

import ru.masterdm.spo.service.IIndustryPipelineService;

import ru.md.domain.ReportSetting;
import ru.md.domain.User;

// import org.uit.director.action.AbstractAction;

// import ru.masterdm.spo.utils.SBeanLocator;

/**
 * Layout navigation View Model.
 * Created by Ildar Shafigullin on 16.09.2017.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LayoutVM {

    private static final Logger LOGGER = LoggerFactory.getLogger(LayoutVM.class);
    private static final String SEPARATOR_ID = "/";

    @WireVariable("industryPipelineService")
    private IIndustryPipelineService industryService;

    private boolean homePage = true;
    private String page;
    private String currentTab;
    private String navigationData;
    private String currentBookmark;
    private String searchString;
    private User currentUser;

    /**
     * ViewModel initialization method.
     * @param execution Execution
     * @param page page
     */
    @Init
    public void init(@ContextParam(ContextType.EXECUTION) Execution execution, @QueryParam("page") String page) {
        HttpServletRequest request = (HttpServletRequest) execution.getNativeRequest();
        /*this.currentUser = AbstractAction.getUser(request);
        if (this.currentUser == null)
            throw new Exception("Пользователь с логином " + request.getRemoteUser() + " не найден в системе");*/
        if (page != null && !page.trim().equals("")) {
            this.page = page;
        }
    }

    /**
     * Focus pointer to the context textbox.
     * @param textbox context textbox
     */
    @Command
    public void focus(@ContextParam(ContextType.COMPONENT) Textbox textbox) {
        textbox.focus();
    }

    /**
     * Searches.
     * @param currentPage current page.
     */
    @Command
    @SmartNotifyChange({"page", "homePage", "currentTab"})
    public void search(@ContextParam(ContextType.PAGE) Page currentPage) {
        /*Map<String, Object> map = new HashMap<>();
        map.put("targetPage", "search_result");
        BindUtils.postGlobalCommand(null, null, "navigateTabGlobal", map);*/
        String targetPage;
        if (industryService.isPresentDealCounts(searchString)) {
            homePage = false;
            currentTab = "branchesAndGCo";
            targetPage = "pipelineTabs";
        } else {
            homePage = true;
            targetPage = "welcome";
            Clients.showNotification("Совпадений не найдено");
        }
        includePage(currentPage, targetPage);
    }

    /**
     * Performs global navigation.
     * @param currentPage current page
     * @param targetPage target page logical name
     * @param targetTab target Tab logical name
     * @param data navigation Data
     * @param key id Data
     */
    @GlobalCommand
    @SmartNotifyChange({"page", "homePage"})
    public void navigateTabGlobal(@ContextParam(ContextType.PAGE) Page currentPage,
                                  @BindingParam(value = "targetPage") String targetPage,
                                  @BindingParam(value = "targetTab") String targetTab,
                                  @BindingParam("data") @Default("") String data,
                                  @BindingParam("key") @Default("") String key) {
        homePage = targetPage.equals("welcome");
        currentTab = targetTab;
        navigationData = data;
        includePage(currentPage, targetPage);
    }

    /**
     * Performs navigation.
     * @param currentPage current page
     * @param targetPage target page logical name
     */
    @Command
    @SmartNotifyChange({"page", "homePage"})
    public void navigate(@ContextParam(ContextType.PAGE) Page currentPage, @BindingParam(value = "targetPage") String targetPage) {
        homePage = targetPage.equals("welcome");
        includePage(currentPage, targetPage);
    }

    /**
     * Performs navigation.
     * @param currentPage current page
     * @param targetPage target page logical name
     * @param targetTab target Tab logical name
     * @param data navigation Data
     */
    @Command
    @SmartNotifyChange({"page", "homePage", "currentTab", "navigationData"})
    public void navigateTab(@ContextParam(ContextType.PAGE) Page currentPage, @BindingParam(value = "targetPage") String targetPage,
                            @BindingParam(value = "targetTab") String targetTab, @BindingParam("data") @Default("") String data) {
        homePage = targetPage.equals("welcome");
        currentTab = targetTab;
        navigationData = data;
        if ("branchesAndGCo".equals(currentTab)) {
            resetSearch();
        }
        includePage(currentPage, targetPage);
    }

    /** reset searchString for right navigation. */
    private void resetSearch() {
        searchString = "";
    }

    /**
     * Includes page.
     * @param currentPage current page.
     * @param targetPage target page
     */
    private void includePage(Page currentPage, String targetPage) {
        page = targetPage;
        Include include = (Include) Selectors.iterable(currentPage, "#pageInclude").iterator().next();
        // Resets include to refresh inclusion with the same source
        include.setSrc(null);
        include.setSrc(getPageName());
        /*include.getPage().setTitle(getPageTitle());*/
        String key = (String) Executions.getCurrent().getAttribute("key");
        key = SEPARATOR_ID.concat((key != null) ? key : "");
        currentBookmark = targetPage.concat(key);
        Executions.getCurrent().getDesktop().setBookmark(currentBookmark);
    }

    /**
     * @return list reports
     */
    public List<ReportSetting> getReports() {
        /*if (currentUser == null){
            return new ArrayList<>();
        return SBeanLocator.getDashboardMapper().getNamedPipelineSettings(currentUser.getId());*/
        List<ReportSetting> presentationList = new ArrayList<ReportSetting>();
        ReportSetting vo = new ReportSetting();
        vo.setId(1L);
        vo.setName("Отчет 1");
        presentationList.add(vo);
        vo = new ReportSetting();
        vo.setId(2L);
        vo.setName("Отчет 2");
        presentationList.add(vo);
        vo = new ReportSetting();
        vo.setId(3L);
        vo.setName("Отчет 3");
        presentationList.add(vo);
        return presentationList;
    }

    /**
     * @param rn
     */
    @Command
    @SmartNotifyChange({"reports", "reportName", "reportNameErrorMsg", "reportNameNew"})
    public void reportNameSetter(@BindingParam("rn") Long rn) {
        /*reportId = rn;
        reportName2 = "";
        reportNameErrorMsg = "";
        reportNameNew = "";
        if (reportId != null && reportId > 0)
            reportNameNew = SBeanLocator.getDashboardMapper().getPipelineSetting(reportId).getName();
        reportName = reportNameNew;*/
        LOGGER.info("reportNameSetter reportId " + rn);
        // LOGGER.info("reportNameSetter reportNameNew "+reportNameNew);
    }

    /**
     * Returns is home page.
     * @return is home page
     */
    public boolean isHomePage() {
        return homePage;
    }

    /**
     * Returns page name.
     * @return page name
     */
    public String getPageName() {
        // return page != null ? pages.getProperty(page) : pages.getProperty("welcome");
        return page != null ? "/pages/" + page + ".zul" : "/pages/welcome.zul";
    }

    /**
     * Returns search string.
     * @return search string
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Sets search string.
     * @param searchString search string
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * Return current panel Tab .
     * @return current Tab
     */
    public String getCurrentTab() {
        return currentTab;
    }

    /**
     * Returns navigation info.
     * @return
     */
    public String getNavigationData() {
        return navigationData;
    }

    /**
     * @return current User
     */
    public String getCurrentUser() {
        if (this.currentUser == null)
            return "";
        //ГО //Иванов Иван Иванович (Администратор системы) -- образец
        return "//" + this.currentUser.getDepname() + " //" + this.currentUser.getFullName();
    }
}

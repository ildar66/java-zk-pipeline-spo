package ru.masterdm.spo.pipeline;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;

import ru.masterdm.spo.pipeline.util.NavigationPage;

import ru.md.domain.User;

/**
 * Index View Model.
 * Created by Ildar Shafigullin on 12.09.2017.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class IndexVM {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexVM.class);

    private NavigationPage currentPage;
    private User currentUser;
    private Map<String, Map<String, NavigationPage>> pageMap;

    /** init model. */
    @Init
    public void init() {
        initPageMap();
        currentPage = pageMap.get("Pages").get("Customers");
    }

    /**
     * navigator left menu command.
     * @param targetPage
     */
    @Command
    public void navigatePage(@BindingParam("target") NavigationPage targetPage) {
        BindUtils.postNotifyChange(null, null, currentPage, "selected");
        currentPage = targetPage;
        BindUtils.postNotifyChange(null, null, this, "currentPage");
    }

    /**
     * navigator tab menu command.
     * @param targetLabelTab
     */
    @Command
    public void navigateTab(@BindingParam("target") String targetLabelTab) {
        // System.out.println("++++++++++++ selected label tag = " + targetLabelTab); // TODO temp out
        BindUtils.postNotifyChange(null, null, currentPage, "selected");
        currentPage = pageMap.get("Pages").get(targetLabelTab);
        BindUtils.postNotifyChange(null, null, currentPage, "selected");
        // System.out.println("++++++++++++ currentPage = " + currentPage); // TODO temp out
        // BindUtils.postNotifyChange(null, null, this, "currentPage");
    }

    /**
     * @return current NavigationPage.
     */
    public NavigationPage getCurrentPage() {
        return currentPage;
    }

    /** @return navigation map. */
    public Map<String, Map<String, NavigationPage>> getPageMap() {
        return pageMap;
    }

    /** init navigation map. */
    private void initPageMap() {
        pageMap = new LinkedHashMap<String, Map<String, NavigationPage>>();

        /*addPage("ZK's Pizza", "About Us", "/home/about_us.zul");
        addPage("ZK's Pizza", "Menu", "/home/menu.zul");
        addPage("ZK's Pizza", "FAQ", "/home/faq.zul");
        addPage("ZK's Pizza", "Press", "/home/press.zul");

        addPage("Customers", "Active Members", "/customers/customers.zul", "active");
        addPage("Customers", "Inactive Members", "/customers/customers.zul", "inactive");

        addPage("Orders", "In Preparation", "/orders/orders.zul", "in-preparation");
        addPage("Orders", "Ready for Shipping", "/orders/orders.zul", "ready");
        addPage("Orders", "Shipping", "/orders/orders.zul", "shipping");
        addPage("Orders", "Specified for Later", "/orders/orders.zul", "later");
*/
        addPage("Pages", "Customers", "/index_0.zul");
        addPage("Pages", "Change table columns", "/temp.zul");
        addPage("Pages", "Customers in Tabs", "/tabs.zul", "some data");
        addPage("Pages", "Change table columns in Tabs", "/tabs.zul", "some other data");
    }

    private void addPage(String title, String subTitle, String includeUri) {
        addPage(title, subTitle, includeUri, null);
    }

    private void addPage(String title, String subTitle, String includeUri, String data) {
        String folder = "/pages";
        Map<String, NavigationPage> subPageMap = pageMap.get(title);
        if (subPageMap == null) {
            subPageMap = new LinkedHashMap<String, NavigationPage>();
            pageMap.put(title, subPageMap);
        }
        NavigationPage navigationPage = new NavigationPage(title, subTitle,
                                                           folder + includeUri + "?random=" + Math.random(), data) {

            @Override
            public boolean isSelected() {
                return currentPage == this;
            }
        };
        subPageMap.put(subTitle, navigationPage);
    }

    /**
     * @return current page TITLE
     */
    public String getCurrentHeader() {
        if (currentPage == null)
            return "PIPELINE";
        return currentPage.getTitle();
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

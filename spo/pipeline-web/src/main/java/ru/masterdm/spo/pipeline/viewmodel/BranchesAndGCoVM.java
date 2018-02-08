package ru.masterdm.spo.pipeline.viewmodel;

import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.SmartNotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Textbox;

import ru.masterdm.spo.service.IIndustryPipelineService;

import ru.md.domain.pipeline.DealCount;

/**
 * Количество и суммы сделок по отраслям и компаниям View Model.
 * Created by Ildar Shafigullin on 23.09.2017.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class BranchesAndGCoVM {

    private String searchString;

    @WireVariable
    private IIndustryPipelineService industryPipelineService;

    /**
     * ViewModel initialization method.
     * @param searchString search string
     */
    @Init
    public void init(@BindingParam("searchString") String searchString) {
        this.searchString = searchString;
    }

    /**
     * Searches.
     */
    @Command
    @SmartNotifyChange({"*"})
    public void search() {
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
     * @return лист кол-во сделок и сумм сгрупированным по отраслям и компаниям.
     */
    public List<DealCount> getDealCounts() {
        return null; // industryPipelineService.fetchDealCounts(searchString); TODO for company group
    }

    /**
     * @return лист кол-во сделок и сумм сгрупированным только по отраслям.
     */
    public List<DealCount> getIndustriesDealCounts() {
        return industryPipelineService.fetchIndustriesDealCounts(searchString);
    }

    /**
     * @return лист кол-во сделок и сумм сгрупированным только по компаниям.
     */
    public List<DealCount> getCompaniesDealCounts() {
        return industryPipelineService.fetchCompaniesDealCounts(searchString);
    }

    /**
     * @return boolean.
     */
    public boolean isShowDealCountsCoGroup() {
        return false; // getDealCounts().size() > 0; TODO for company group
    }

    /**
     * @return boolean.
     */
    public boolean isShowCompaniesDealCounts() {
        return getCompaniesDealCounts().size() > 0;
    }

    /**
     * @return boolean.
     */
    public boolean isShowIndustriesDealCounts() {
        return getIndustriesDealCounts().size() > 0;
    }

    /**
     * @return лист объемов сделок и сумм сгрупированным по отраслям.
     */
    public List<DealCount> getDealCountsSummary() {
        return industryPipelineService.fetchDealCountsSummary();
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

}

package ru.masterdm.spo.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.AfterSizeEvent;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Window;

/**
 * @author pmasalov
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class IndexController extends BindComposer<Window> {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    @Wire
    private Window mainWindow;
    @Wire
    private Popup reportSavePopup;
    @Wire
    private Popup reportDelPopup;
    @Wire
    private Popup reportRenamePopup;
    @Wire
    private Popup reportCopyPopup;
    @Wire
    private Popup reportPopup;
    @Wire
    private Popup reportPopupPub;
    @Wire
    private Popup reportSharePopup;

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) throws Exception {
        super.doBeforeCompose(page, parent, compInfo);
        Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), BindComposer.class));
        return compInfo;
    }

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        Selectors.wireComponents(comp, this, false);
        Selectors.wireEventListeners(comp, this);
    }

    /*@Listen("onAfterSize = #mainWindow")
    public void onAfterSizeMainWindow(AfterSizeEvent event) {
        System.out.println("onAfterSizeMainWindow - " + event + " w=" + event.getWidth() + " h=" + event.getHeight());
    }*/
    @Listen("onClick = #saveBtn, #renameBtn, #reportCopyBtn, #shareBtn, #delBtn")
    public void onPopupClick() {
        reportPopup.close();
        reportPopupPub.close();
        reportSharePopup.close();
        reportDelPopup.close();
        IndexVM vm = (IndexVM)getViewModel();
        if (vm.getReportNameErrorMsg().isEmpty()){
            reportSavePopup.close();
            reportRenamePopup.close();
            reportCopyPopup.close();
        }
    }

    @Listen("onCancel = #mainWindow, #reportPopup ,#reportPopupPub")
    public void escPressed() {
        LOGGER.info("escPressed");
        //git test
        // close popup if any is opened
        for (Component c : mainWindow.queryAll("popup")) {
            Popup p = (Popup) c;
            p.close();
        }
    }
}

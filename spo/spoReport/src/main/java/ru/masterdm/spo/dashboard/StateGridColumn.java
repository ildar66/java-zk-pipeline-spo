package ru.masterdm.spo.dashboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * @author pmasalov
 */
public class StateGridColumn extends DataGridColumn {

    public StateGridColumn() {
        super();

        EventListener<Event> invalidatePanelEventListener = new EventListener<Event>() {

            @Override
            public void onEvent(Event event) throws Exception {
                //System.out.println("onEvent - " + event);
                invalidateToPanel(event.getTarget());
            }
        };

        addEventListener(ON_SHOW_EVENT, invalidatePanelEventListener);
        addEventListener(ON_HIDE_EVENT, invalidatePanelEventListener);
    }

    private void invalidateToPanel(Component c) {
        do {
            //System.out.println(c);
            c.invalidate();
            c = c.getParent();
        } while (!(c instanceof org.zkoss.zul.Panel));
        //System.out.println(c);
        c.invalidate();
    }

}

package ru.masterdm.spo.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.Window;

/**
 * Index Controller ZK layout.
 * Created by Ildar Shafigullin on 12.09.2017.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class IndexController extends BindComposer<Window> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

}

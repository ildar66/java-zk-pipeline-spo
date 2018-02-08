package ru.masterdm.spo.pipeline.viewmodel;

import java.util.List;

import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Column;

import ru.masterdm.spo.service.IIndustryPipelineService;

import ru.md.domain.pipeline.DealVolume;

/**
 * Объем сделок по отраслям View Model.
 * Created by Ildar Shafigullin on 19.09.2017.
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class VolumeDealOnBranchVM {

    // private final static Logger logger = LoggerFactory.getLogger(VolumeDealOnBranchVM.class);

    @WireVariable
    private IIndustryPipelineService industryPipelineService;

    /**
     * @return лист объемов сделок по отраслям
     */
    public List<DealVolume> getDealVolumes() {
        return industryPipelineService.fetchDealVolumes();
    }

    /**
     * @return общий объем всех сделок по отраслям
     */
    public long getSumDealVolumes() {
        return industryPipelineService.getSumDealVolumes();
    }

    /**
     * change columne and row.
     * @param event
     */
    public void move(org.zkoss.zk.ui.event.DropEvent event) {
        Column dragged = (Column) event.getDragged();
        Column self = (Column) event.getTarget();
        if (dragged.getClass().getName().endsWith("Column")) {
            int maxRows = dragged.getGrid().getRows().getChildren().size();
            int i = dragged.getParent().getChildren().indexOf(dragged);
            int j = self.getParent().getChildren().indexOf(self);

            //move celles for each row
            for (int k = 0; k < maxRows; k++)
                self.getGrid().getCell(k, j).getParent().insertBefore(self.getGrid()
                                                                          .getCell(k, i), self.getGrid().getCell(k, j));
        }

        self.getParent().insertBefore(dragged, self);
    }
}
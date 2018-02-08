package ru.masterdm.spo.dashboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.set.ListOrderedSet;
import org.zkoss.zul.TreeNode;

import com.google.gson.Gson;
import com.vtb.util.CollectionUtils;
import ru.md.domain.DepartmentExt;
import ru.md.domain.User;
import ru.md.domain.dashboard.PipelineTradingDesk;

import ru.masterdm.spo.dashboard.model.SummaryFigure;
import ru.masterdm.spo.dashboard.model.metadata.GridColumnItem;
import ru.masterdm.spo.dashboard.model.metadata.GridColumnMetadata;
import ru.masterdm.spo.utils.SBeanLocator;

public class PipelineSettingsSerializer {

    private static class GridColumnBag {
        String code;
        boolean visible;

        public GridColumnBag(String code, boolean visible) {
            this.code = code;
            this.visible = visible;
        }
    }

    public static String jsonfyDepartments(Set<TreeNode<DepartmentExt>> set){
        ArrayList<String> departmentsIds = new ArrayList<String>();
        for (TreeNode<DepartmentExt> n : set)
            departmentsIds.add(n.getData().getId().toString());
        return CollectionUtils.listJoin(departmentsIds);
    }

    public static String jsonfyGridColumnItem(Set<GridColumnMetadata> set) {
        Gson gson = new Gson();
        GridColumnBag[] bags = new GridColumnBag[set.size()];
        int i = 0;

        for (GridColumnMetadata td : set)
            bags[i++] = new GridColumnBag(td.getCode(), td.isVisible());

        return gson.toJson(bags);
    }

    public static String jsonfySummaryFigure(Set<SummaryFigure> set) {
        ArrayList<String> list = new ArrayList<String>();
        for (SummaryFigure td : set)
            list.add(String.valueOf(td.getCode()));
        return CollectionUtils.listJoin(list);
    }

    public static String jsonfyTradingDesk(Set<PipelineTradingDesk> set) {
        ArrayList<String> list = new ArrayList<String>();
        for (PipelineTradingDesk td : set)
            list.add(String.valueOf(td.getId()));
        return CollectionUtils.listJoin(list);
    }

    public static Set<TreeNode<DepartmentExt>> parseDepartmentExt(String s, List<DepartmentExt> departmentExtList){
        Set<TreeNode<DepartmentExt>> set = new HashSet<TreeNode<DepartmentExt>>();
        Gson gson = new Gson();
        Long[] arr = gson.fromJson("[" + s + "]", Long[].class);
        for (Long id : arr)
            for (DepartmentExt d : departmentExtList)
                if (d.getId().equals(id))
                    set.add(new DepartmentTreeNode(null,d));
        return set;
    }

    public static Set<PipelineTradingDesk> parsePipelineTradingDesk(String s){
        Set<PipelineTradingDesk> set = new HashSet<PipelineTradingDesk>();
        List<PipelineTradingDesk> tradingDesks = SBeanLocator.getDashboardService().getPipelineTradingDesk();
        tradingDesks.add(PipelineTradingDesk.OTHERS);
        Gson gson = new Gson();
        Integer[] arr = gson.fromJson("[" + s + "]", Integer[].class);
        for (PipelineTradingDesk td : tradingDesks)
            for (Integer id : arr)
                if(id.equals(Integer.valueOf(td.getId())))
                    set.add(td);
        return set;
    }

    private static GridColumnItem find(GridColumnItem[] values, String code) {
        for (GridColumnItem i : values) {
            if (i.getCode().equals(code))
                return i;
        }
        return null;
    }

    public static Set<GridColumnMetadata> parseGridColumnItem(String s, GridColumnItem[] values){
        Gson gson = new Gson();
        GridColumnBag[] bags = gson.fromJson(s, GridColumnBag[].class);
        ListOrderedSet arr = new ListOrderedSet();

        for (GridColumnBag b : bags) {
            GridColumnItem i = find(values, b.code);
            if (i != null) {
                PipelineSettings.GridColumnSetting cs = new PipelineSettings.GridColumnSetting(i);
                cs.setVisible(b.visible);
                arr.add(cs);
            }
        }

        // если разработка добавила новые колонки и они не были сохранены в настройках ещё ниразу
        for (GridColumnItem i : values) {
            if (!arr.contains(i)) {
                arr.add(new PipelineSettings.GridColumnSetting(i));
            }
        }

        return arr;
    }

    public static HashSet<String> parseHashSet(String s){
        Gson gson = new Gson();
        String[] arr = gson.fromJson("[" + s + "]", String[].class);
        HashSet<String> set = new HashSet<String>();
        for (String code : arr)
            set.add(code);
        return set;
    }
    public static Set<SummaryFigure> parseSummaryFigure(String s, SummaryFigure[] values){
        Gson gson = new Gson();
        String[] arr = gson.fromJson("[" + s + "]", String[].class);
        Set<SummaryFigure> set = new HashSet<SummaryFigure>();
        for (SummaryFigure item : values)
            for (String code : arr)
                if (item.getCode().equals(code))
                    set.add(item);
        return set;
    }
}

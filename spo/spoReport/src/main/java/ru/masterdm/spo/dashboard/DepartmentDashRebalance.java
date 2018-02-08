package ru.masterdm.spo.dashboard;

import ru.md.domain.DepartmentExt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Перевешивает листья дерева в новые места с учётом настроек справочника
 * Логика перенесенна в {@link ru.masterdm.spo.dashboard.model.FilteredDepartmentModelFactory}
 * Created by Andrey Pavlenko on 14.10.2016.
 */
@Deprecated
public class DepartmentDashRebalance {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDashRebalance.class.getName());
    private Map<Long, DepartmentExt> map;

    public DepartmentDashRebalance(List<DepartmentExt> departmentExtList) {
        if (departmentExtList != null) {
            map = new HashMap<Long, DepartmentExt>(departmentExtList.size());
            for (DepartmentExt i : departmentExtList) map.put(i.getId(), i);
        } else
            map = new HashMap<Long, DepartmentExt>(1);
        LOGGER.info("DepartmentDashRebalance map size " + map.size());
    }

    public List<DepartmentExt> getFilteredList() {
        List<DepartmentExt> res = new ArrayList<DepartmentExt>();
        for (DepartmentExt d : map.values())
            if (d.isDashboardDep()) {
                d.setIdDepartmentParent(findValidParent(d.getIdDepartmentParent(), map));
                res.add(d);
            }
        LOGGER.info("getFilteredList size " + res.size());
        return res;
    }

    private Long findValidParent(Long id, Map<Long, DepartmentExt> deps) {
        if (id == null || !deps.containsKey(id))
            return null;
        DepartmentExt d = deps.get(id);
        return d.isDashboardDep() ? id : findValidParent(d.getIdDepartmentParent(), deps);
    }
}

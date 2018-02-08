package ru.md.spo.dashboard;

import org.junit.Assert;
import org.junit.Test;
import ru.masterdm.spo.dashboard.DepartmentDashRebalance;
import ru.md.domain.DepartmentExt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey Pavlenko on 14.10.2016.
 */
public class DepartmentDashRebalanceTest  extends Assert {

    @Test(timeout=7000)
    public  void testNull() {
        assertNotNull(new DepartmentDashRebalance(null).getFilteredList());
    }
    @Test(timeout=7000)
    public  void testEmpty() {//пустое дерево
        assertEquals(0L, new DepartmentDashRebalance(null).getFilteredList().size());
    }
    private DepartmentExt newDep(Long id, String name, boolean isDashboardDep, Long idParent) {
        DepartmentExt d = new DepartmentExt();
        d.setId(id);
        d.setName(name);
        d.setIsDashboardDep(isDashboardDep ? 1L : 0L);
        d.setIdDepartmentParent(idParent);
        return d;
    }

    @Test(timeout=7000)
    public  void testFull() {//дерево с галочками возвращается всё
        List<DepartmentExt> res = new ArrayList<DepartmentExt>();
        res.add(newDep(1L, "1", true, null));
        res.add(newDep(2L, "2", true, null));
        res.add(newDep(3L, "3", true, null));
        res.add(newDep(4L, "1-1", true, 1L));
        res.add(newDep(5L, "1-2", true, 1L));
        res.add(newDep(6L, "1-1-1", true, 4L));
        res.add(newDep(7L, "1-1-2", true, 4L));
        res.add(newDep(8L, "2-1", true, 2L));
        assertEquals(8L, new DepartmentDashRebalance(res).getFilteredList().size());
    }
    @Test(timeout=7000)
    public  void testInterruptedTree() {
        List<DepartmentExt> param = new ArrayList<DepartmentExt>();
        param.add(newDep(1L, "1", true, null));
        param.add(newDep(2L, "2", false, null));
        param.add(newDep(3L, "3", false, null));
        param.add(newDep(4L, "1-1", false, 1L));
        param.add(newDep(5L, "1-2", true, 1L));
        param.add(newDep(6L, "1-1-1", true, 4L));
        param.add(newDep(7L, "1-1-2", false, 4L));
        param.add(newDep(8L, "2-1", true, 2L));//должен в корень попасть
        List<DepartmentExt> res = new DepartmentDashRebalance(param).getFilteredList();
        assertEquals(4L, res.size());
        assertNull(findParentFor(1L, res));
        assertEquals(1L, findParentFor(6L, res).longValue());
        assertEquals(1L, findParentFor(5L, res).longValue());
        assertNull(findParentFor(8L, res));
    }
    private Long findParentFor(Long id, List<DepartmentExt> list) {
        for (DepartmentExt d : list)
            if(d.getId().equals(id))
                return d.getIdDepartmentParent();
        return null;
    }
}

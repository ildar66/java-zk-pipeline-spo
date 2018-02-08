package ru.masterdm.spo.dashboard.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

import ru.md.domain.DepartmentExt;
import ru.md.domain.User;

import ru.masterdm.spo.dashboard.DepartmentTreeNode;
import ru.masterdm.spo.dashboard.PipelineVM;

/**
 * @author pmasalov
 */
public class FullDepartmentModelFactory extends ModelFactoryAbstract<DefaultTreeModel<DepartmentExt>> {

    public FullDepartmentModelFactory(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    private DefaultTreeModel<DepartmentExt> createDepartmentTreeModel() {
        DefaultTreeModel<DepartmentExt> model = new DefaultTreeModel<DepartmentExt>(
                new DepartmentTreeNode(this, null,
                                                                         new DepartmentTreeNode[] {
                                                                                 new DepartmentTreeNode(this, DepartmentExt.ALL_DEPARTMENT,
                                                                                                        createDepartmentTree())}), true);
        model.setMultiple(true);
        model.addOpenPath(new int[] {0});
        model.setSelection(getPipelineVM().getSettings().getDepartmentsSelectedNodes());
        for (TreeNode<DepartmentExt> n : getPipelineVM().getSettings().getDepartmentsSelectedNodes())
            model.addOpenObject(n);

        return model;
    }

    private TreeNode<DepartmentExt>[] createDepartmentTree() {
        User user = getPipelineVM().getSettings().getUser();
        List<DepartmentExt> departmentExtList =
                getPipelineVM().getDashboardService().getDepartmentsExtForTree(user);
        if (departmentExtList.size() == 0)
            return null;

        Map<Long, TreeNode<DepartmentExt>> treeNodeMap = new HashMap<Long, TreeNode<DepartmentExt>>();
        List<TreeNode<DepartmentExt>> rootLevel = new ArrayList<TreeNode<DepartmentExt>>();

        for (DepartmentExt department : departmentExtList) {

            /*DefaultTreeNode<DepartmentExt>*/
            DepartmentTreeNode node = null;
            if (!treeNodeMap.containsKey(department.getId())) {
                node = new DepartmentTreeNode(this, department,
                                                                                (Collection<? extends TreeNode<DepartmentExt>>) null);
                treeNodeMap.put(department.getId(), node);
            }

            if (department.isDashboardDep()) {
                // если это именно корневой отдел в исходном дереве (дерево в БД)
                if (department.getIdDepartmentParent() == null)
                    rootLevel.add(node);
                else {
                    // подвязка к родительскому элементу
                    for (TreeNode<DepartmentExt> parent = treeNodeMap.get(department.getIdDepartmentParent()); parent != null;
                         parent = treeNodeMap.get(parent.getData().getIdDepartmentParent())) {
                        if (parent.getData().isDashboardDep()) {
                            parent.add(node);
                            break;
                        }
                    }
                    if (node.getParent() == null) { // подразделение не было добавленно ни к одному из родительских
                        rootLevel.add(node);
                    }
                }

                // департамент текущего пользователя
                if (user.getIdDepartment() != null && user.getIdDepartment().equals(department.getId())) {
                    getPipelineVM().getSettings().setUserDepartmentTreeNode(node);
                }
            }
        }

        return (TreeNode<DepartmentExt>[]) rootLevel.toArray(new TreeNode[0]);
    }

    @Override
    public DefaultTreeModel<DepartmentExt> createModel() {
        return createDepartmentTreeModel();
    }
}

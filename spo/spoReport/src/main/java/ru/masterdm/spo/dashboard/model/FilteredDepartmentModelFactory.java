package ru.masterdm.spo.dashboard.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

import ru.md.domain.DepartmentExt;

import ru.masterdm.spo.dashboard.DepartmentTreeNode;
import ru.masterdm.spo.dashboard.PipelineVM;

/**
 * @author pmasalov
 */
public class FilteredDepartmentModelFactory extends ModelFactoryAbstract<DefaultTreeModel<DepartmentExt>> {

    public FilteredDepartmentModelFactory(PipelineVM pipelineVM) {
        super(pipelineVM);
    }

    private List<TreeNode<DepartmentExt>> searchInChildren(List<TreeNode<DepartmentExt>> srcNodes) {
        boolean yesContains;
        List<TreeNode<DepartmentExt>> newChildren = new ArrayList<TreeNode<DepartmentExt>>();
        for (TreeNode<DepartmentExt> srcN : srcNodes) {
            DepartmentExt data = srcN.getData();
            yesContains = data == null ? false : StringUtils.containsIgnoreCase(data.getName(), getPipelineVM().getDepartmentSearch());
            //System.out.println(yesContains + " = " + data.getName());

            if (!srcN.isLeaf()) {
                List<TreeNode<DepartmentExt>> newMyChildren = searchInChildren(srcN.getChildren());
                if (newMyChildren != null && newMyChildren.size() > 0) {
                    newChildren.add(new DepartmentTreeNode(this, srcN.getData(), newMyChildren));
                } else if (yesContains) {
                    newChildren.add(new DepartmentTreeNode(this, srcN.getData()));
                }
            } else if (yesContains) {
                newChildren.add(new DepartmentTreeNode(this, srcN.getData()));
            }
        }

        return newChildren.size() > 0 ? newChildren : null;
    }

    private TreeNode<DepartmentExt> createFilteredDepartmentTree() {
        TreeNode<DepartmentExt> srcRoot = getPipelineVM().getFullDepartmentsModel().getRoot();
        TreeNode<DepartmentExt> newRoot = null;

        DepartmentExt data = srcRoot.getData();
        boolean yesContains = data == null ? false : StringUtils.containsIgnoreCase(data.getName(), getPipelineVM().getDepartmentSearch());
        if (!srcRoot.isLeaf()) {
            List<TreeNode<DepartmentExt>> newRootChildren = searchInChildren(srcRoot.getChildren());
            if (newRootChildren != null && newRootChildren.size() > 0) {
                newRoot = new DepartmentTreeNode(this, srcRoot.getData(), newRootChildren);
            } else if (yesContains) {
                newRoot = new DepartmentTreeNode(this, srcRoot.getData());
            }
        } else if (yesContains) {
            newRoot = new DepartmentTreeNode(this, srcRoot.getData());
        }

        return newRoot;
    }

    private DefaultTreeModel<DepartmentExt> createFilteredDepartmentModel() {
        DefaultTreeModel<DepartmentExt> model = null;
        TreeNode<DepartmentExt> newTree = createFilteredDepartmentTree();
        if (newTree != null) {
            model = new DefaultTreeModel<DepartmentExt>(newTree);
            model.setMultiple(true);
            model.addOpenPath(new int[] {0});
            model.setSelection(getPipelineVM().getSettings().getDepartmentsSelectedNodes());
            for (TreeNode<DepartmentExt> n : getPipelineVM().getSettings().getDepartmentsSelectedNodes()) {
                model.addOpenObject(n);
            }
        }
        return model;
    }

    @Override
    public DefaultTreeModel<DepartmentExt> createModel() {
        return createFilteredDepartmentModel();
    }
}

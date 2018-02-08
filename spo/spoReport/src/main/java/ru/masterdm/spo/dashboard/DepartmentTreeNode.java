package ru.masterdm.spo.dashboard;

import java.util.Collection;

import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treecell;

import ru.md.domain.DepartmentExt;

import ru.masterdm.spo.dashboard.model.ModelFactoryAbstract;
import ru.masterdm.spo.dashboard.tree.TreeUtils;

/**
 * расширение функциональности стандартного нода ля наших нужд
 *
 * @author pmasalov
 */
public class DepartmentTreeNode extends DefaultTreeNode<DepartmentExt> {

    private ModelFactoryAbstract modelFactory;

    public DepartmentTreeNode(ModelFactoryAbstract modelFactory, DepartmentExt data, Collection<? extends TreeNode<DepartmentExt>> children) {
        super(data, children);
        this.modelFactory = modelFactory;
    }

    public DepartmentTreeNode(ModelFactoryAbstract modelFactory, DepartmentExt data) {
        super(data);
        this.modelFactory = modelFactory;
    }

    public DepartmentTreeNode(ModelFactoryAbstract modelFactory, DepartmentExt data, Collection<? extends TreeNode<DepartmentExt>> children, boolean nullAsMax) {
        super(data, children, nullAsMax);
        this.modelFactory = modelFactory;
    }

    public DepartmentTreeNode(ModelFactoryAbstract modelFactory, DepartmentExt data, TreeNode<DepartmentExt>[] children) {
        super(data, children);
        this.modelFactory = modelFactory;
    }

    //static int i = 0;
    public boolean isHasChildUnselected() {
        //return (i++ % 2 == 0) ? true : false;
        return TreeUtils.isMarkGray(this, modelFactory.getPipelineVM().getSettings().getUserDepartmentTreeNode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DepartmentTreeNode))
            return false;
        if (((DepartmentTreeNode) o).getData() == null || getData() == null)
            return false;

        return getData().equals(((DepartmentTreeNode) o).getData());
    }

    @Override
    public int hashCode() {
        Object o = getData();
        if (o != null)
            return o.hashCode();
        return super.hashCode();
    }
}

package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeNode;

/**
 * @author pmasalov
 */
public interface TreeVisitor<E> {

    /**
     *
     * @param node
     * @return true continue processing to next nodes
     */
    boolean visit(TreeNode<E> node);
}

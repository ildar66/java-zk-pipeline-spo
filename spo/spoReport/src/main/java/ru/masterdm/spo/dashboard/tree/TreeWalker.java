package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeNode;

/**
 * @author pmasalov
 */
public interface TreeWalker<E> {

    void walk(TreeNode<E> rootNode, TreeVisitor<E> v);

}

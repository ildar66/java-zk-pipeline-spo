package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeNode;

/**
 * Обход дерева/поддерева сверху вниз
 * @author pmasalov
 */
public class TreeDownUpWalker<E> implements TreeWalker<E> {

    private boolean skipRoot = false;

    public TreeDownUpWalker() {
    }

    public TreeDownUpWalker(boolean skipRoot) {
        this.skipRoot = skipRoot;
    }

    private void walkToChild(TreeNode<E> node, TreeVisitor<E> v) {
        for (TreeNode<E> n : node.getChildren()) {
            if (!n.isLeaf())
                walkToChild(n, v);
            if (!v.visit(n))
                return;
        }
    }

    @Override
    public void walk(TreeNode<E> rootNode, TreeVisitor<E> v) {
        if (!rootNode.isLeaf())
            walkToChild(rootNode, v);

        if (!skipRoot)
            v.visit(rootNode);
    }
}

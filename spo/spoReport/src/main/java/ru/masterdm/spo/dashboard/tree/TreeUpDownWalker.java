package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeNode;

/**
 * Обход дерева/поддерева сверху вниз
 * @author pmasalov
 */
public class TreeUpDownWalker<E> implements TreeWalker<E> {

    private boolean skipRoot = false;

    public TreeUpDownWalker() {
    }

    public TreeUpDownWalker(boolean skipRoot) {
        this.skipRoot = skipRoot;
    }

    private void walkToChild(TreeNode<E> node, TreeVisitor<E> v) {
        for (TreeNode<E> n : node.getChildren()) {
            if (!v.visit(n))
                return;

            if (!n.isLeaf())
                walkToChild(n, v);
        }
    }

    @Override
    public void walk(TreeNode<E> rootNode, TreeVisitor<E> v) {
        if (!skipRoot) {
            if (!v.visit(rootNode))
                return;
        }

        if (!rootNode.isLeaf())
            walkToChild(rootNode, v);
    }
}

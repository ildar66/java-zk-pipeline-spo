package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.ext.Selectable;

/**
 * @author pmasalov
 */
public class SelectChanger extends TreeUpDownWalker {

    protected Selectable s;
    protected TreeNode rn;

    public SelectChanger(TreeModel model, TreeNode rootNode) {
        super(true);
        s = (Selectable) model;
        rn = rootNode;
    }

    /**
     * Returns .
     * @return
     */
    public void select() {
        walk(rn, new TreeVisitor() {

            @Override
            public boolean visit(TreeNode node) {
                s.addToSelection(node);
                return true;
            }
        });
    }

    public void unselect() {
        walk(rn, new TreeVisitor() {

            @Override
            public boolean visit(TreeNode node) {
                s.removeFromSelection(node);
                return true;
            }
        });
    }
}

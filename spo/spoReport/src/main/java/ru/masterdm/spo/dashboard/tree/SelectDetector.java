package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.ext.Selectable;

/**
 * @author pmasalov
 */
public class SelectDetector extends TreeUpDownWalker {

    private boolean stateResult = false;
    private boolean includeResult = false;

    public SelectDetector(TreeModel model, final TreeNode rootNode, final boolean selectState) {
        this(model, rootNode, selectState, null);
    }

    public SelectDetector(TreeModel model, final TreeNode rootNode, final boolean selectState, final TreeNode includeNode) {
        super(true);
        if (includeNode != null) {
            includeResult = rootNode.equals(includeNode);
        } else {
            includeResult = true; // нечего сверять в плане включения
        }
        final Selectable s = (Selectable) model;
        walk(rootNode, new TreeVisitor() {

            @Override
            public boolean visit(TreeNode node) {
                if (!includeResult && node.equals(includeNode)) {
                    includeResult = true;
                }
                if (!stateResult && selectState == s.isSelected(node)) {
                    stateResult = true;
                }
                return !(stateResult && includeResult); // stop visiting if all results are
            }
        });
    }

    /**
     * Returns .
     * @return
     */
    public boolean isResult() {
        return stateResult && includeResult;
    }
}

package ru.masterdm.spo.dashboard.tree;

import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

/**
 * @author pmasalov
 */
public class TreeUtils {

    static public boolean atLeastOneChildUnselected(TreeNode rootNode) {
        TreeModel m = rootNode.getModel();
        if (m != null)
            return atLeastOneChildUnselected(m, rootNode);
        return false;
    }

    /**
     * Отмечать ли подразделение серым цветом.
     * Отмечается в случае если у потомков есть как отмеченные, так и не отмеченные подразделения.
     * @param rootNode - подразделение
     * @param includeNode - полное дерево
     * @return Отмечать ли подразделение серым цветом
     */
    static public boolean isMarkGray(TreeNode rootNode, TreeNode includeNode) {
        if (rootNode.getChildCount() == 0)
            return false;
        if (rootNode.getModel() == null)
            return false;
        if (rootNode.getModel().isSelected(rootNode))
            return false;
        for (Object child : rootNode.getChildren()){
            TreeNode c = (TreeNode) child;
            if (rootNode.getModel().isSelected(c) || isMarkGray(c, includeNode))
                return true;
        }
        return false;
    }
    static public boolean atLeastOneChildUnselected(TreeNode rootNode, TreeNode includeNode) {
        TreeModel m = rootNode.getModel();
        if (m != null)
            return atLeastOneChildUnselected(m, rootNode, includeNode);
        return false;
    }

    static public boolean atLeastOneChildUnselected(TreeModel m, TreeNode rootNode) {
        SelectDetector selectDetector = new SelectDetector(m, rootNode, false);
        return selectDetector.isResult();
    }

    // child unselected and path contain include node
    static public boolean atLeastOneChildUnselected(TreeModel m, TreeNode rootNode, TreeNode includeNode) {
        SelectDetector selectDetector = new SelectDetector(m, rootNode, false, includeNode);
        return selectDetector.isResult();
    }

    static public boolean atLeastOneChildSelected(TreeNode rootNode) {
        TreeModel m = rootNode.getModel();
        if (m != null)
            if (m != null)
                return atLeastOneChildSelected(m, rootNode);
        return false;
    }

    static public boolean atLeastOneChildSelected(TreeModel m, TreeNode rootNode) {
        SelectDetector selectDetector = new SelectDetector(m, rootNode, true);
        return selectDetector.isResult();
    }

}

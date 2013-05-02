package com.karateca.ddescriber.dialog;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * @author Andres Dominguez.
 */
public class MyCellRenderer extends ColoredTreeCellRenderer {

  private static final Color GREEN_BG_COLOR = new Color(182, 232, 172);
  private static final Color GREEN_FG_COLOR = new Color(57, 194, 70);
  private static final Color RED_BG_COLOR = new Color(232, 117, 107);
  private static final Color RED_FG_COLOR = new Color(194, 41, 39);
  private final Icon itIcon = IconLoader.findIcon("/icons/it-icon.png");
  private final Icon descIcon = IconLoader.findIcon("/icons/desc-icon.png");

  @Override
  public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

    // Ignore non test nodes.
    if (!(node.getUserObject() instanceof TestFindResult)) {
      return;
    }

    TreeNode treeNode = (TreeNode) node;
    TestFindResult findResult = treeNode.getNodeValue();

    switch (findResult.getTestState()) {
      case Excluded:
        setForeground(RED_FG_COLOR);
        setBackground(RED_BG_COLOR);
        break;
      case Included:
        setForeground(GREEN_FG_COLOR);
        setBackground(GREEN_BG_COLOR);
        break;
      case NotModified:
        break;
    }

    // Set the icon depending on the type.
    setIcon(findResult.isDescribe() ? descIcon : itIcon);

    String name = findResult.toString();
    if (treeNode.isTopNode()) {
      name = String.format("%s - [%s]", name, treeNode.getVirtualFile().getName());
    }
    append(name);
  }
}

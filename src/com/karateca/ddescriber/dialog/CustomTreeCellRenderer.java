package com.karateca.ddescriber.dialog;

import com.intellij.openapi.util.IconLoader;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author Andres Dominguez.
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

  private static final Color GREEN_BG_COLOR = new Color(182, 232, 172);
  private static final Color GREEN_FG_COLOR = new Color(57, 194, 70);
  private static final Color RED_BG_COLOR = new Color(232, 117, 107);
  private static final Color RED_FG_COLOR = new Color(194, 41, 39);
  private final Color defaultNonSelColor = getBackgroundNonSelectionColor();
  private final Color defaultBgSelColor = getBackgroundSelectionColor();
  private final Icon itIcon = IconLoader.findIcon("/icons/it-icon.png");
  private final Icon descIcon = IconLoader.findIcon("/icons/desc-icon.png");
  private final boolean showFileName;

  public CustomTreeCellRenderer(boolean showFileName) {
    this.showFileName = showFileName;
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

    // Ignore non test nodes.
    if (!(node.getUserObject() instanceof TestFindResult)) {
      return component;
    }

    TreeNode treeNode = (TreeNode) node;
    TestFindResult findResult = treeNode.getNodeValue();

    switch (findResult.getTestState()) {
      case Excluded:
        setBackgroundNonSelectionColor(RED_BG_COLOR);
        setBackgroundSelectionColor(RED_FG_COLOR);
        break;
      case Included:
        setBackgroundNonSelectionColor(GREEN_BG_COLOR);
        setBackgroundSelectionColor(GREEN_FG_COLOR);
        break;
      case NotModified:
        setBackgroundNonSelectionColor(defaultNonSelColor);
        setBackgroundSelectionColor(defaultBgSelColor);
        break;
    }

    // Set the icon depending on the type.
    setIcon(findResult.isDescribe() ? descIcon : itIcon);

    String name = findResult.toString();
    if (showFileName && treeNode.isTopNode()) {
      name = String.format("%s - [%s]", name, treeNode.getVirtualFile().getName());
    }
    setText(name);

    return component;
  }
}

package com.karateca.ddescriber.dialog;

import com.intellij.openapi.util.IconLoader;
import com.karateca.ddescriber.TestFindResult;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author Andres Dominguez.
 */
public class CustomTreeCellRenderer extends DefaultTreeCellRenderer {

  static final Color GREEN_BG_COLOR = new Color(182, 232, 172);
  private final Color defaultColor = getBackgroundNonSelectionColor();
  private Icon itIcon = IconLoader.findIcon("/icons/it-icon.png");
  private Icon descIcon = IconLoader.findIcon("/icons/desc-icon.png");

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    TestFindResult findResult = (TestFindResult) node.getUserObject();

    Color color = findResult.isMarkedForRun() ? GREEN_BG_COLOR : defaultColor;
    setBackgroundNonSelectionColor(color);

    // Set the icon depending on the type.
    setIcon(findResult.isDescribe() ? descIcon : itIcon);

    return component;
  }
}

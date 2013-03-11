package com.karateca.ddescriber.dialog;

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

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    TestFindResult findResult = (TestFindResult) node.getUserObject();

    Color color = findResult.isMarkedForRun() ? GREEN_BG_COLOR : defaultColor;
    setBackgroundNonSelectionColor(color);

    return component;
  }
}

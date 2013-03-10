package com.karateca.ddescriber.dialog;

import com.karateca.ddescriber.TestFindResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author Andres Dominguez.
 */
class CellRenderer extends DefaultListCellRenderer {

  public static final Color GREEN_BG_COLOR = new Color(182, 232, 172);

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    // Paint the cell as green if it is an iit or ddescribe.
    if (((TestFindResult) value).isMarkedForRun() && !isSelected) {
      component.setBackground(GREEN_BG_COLOR);
    }

    return component;
  }
}

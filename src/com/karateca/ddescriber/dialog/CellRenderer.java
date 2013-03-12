package com.karateca.ddescriber.dialog;

import com.karateca.ddescriber.TestFindResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author Andres Dominguez.
 */
class CellRenderer extends DefaultListCellRenderer {

  private static final Color GREEN_BG_COLOR = new Color(182, 232, 172);
  private static final Color GREEN_SELECTED_COLOR = new Color(9, 203, 0);
  private static final Color RED_BG_COLOR = new Color(255, 162, 149);
  private static final Color RED_SELECTED_COLOR = new Color(255, 69, 30);

  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    TestFindResult findResult = (TestFindResult) value;

    if (isSelected) {
      if (findResult.isExcluded()) {
        setBackground(RED_SELECTED_COLOR);
      } else if (findResult.isMarkedForRun()) {
        setBackground(GREEN_SELECTED_COLOR);
      }

      return component;
    }

    // Paint the cell as green if it is an iit or ddescribe.
    if (findResult.isMarkedForRun()) {
      component.setBackground(GREEN_BG_COLOR);
    }

    if (findResult.isExcluded()) {
      component.setBackground(RED_BG_COLOR);
    }

    return component;
  }
}

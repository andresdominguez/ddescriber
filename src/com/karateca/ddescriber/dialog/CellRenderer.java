package com.karateca.ddescriber.dialog;

import com.karateca.ddescriber.LineFindResult;

import javax.swing.*;
import java.awt.*;

/**
 * @author Andres Dominguez.
 */
class CellRenderer extends DefaultListCellRenderer {
  @Override
  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    // Paint the cell as green if it is an iit or ddescribe.
    if (((LineFindResult) value).isMarkedForRun()) {
      component.setForeground(new Color(0, 102, 0));
    }

    return component;
  }
}

package com.karateca.ddescriber.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.karateca.LineFindResult;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Dialog extends DialogWrapper {
  final List<LineFindResult> hierarchy;
  private JBList jbList;

  public Dialog(@Nullable Project project, List<LineFindResult> hierarchy) {
    super(project);
    this.hierarchy = hierarchy;
    init();
    setTitle("Select the test or suite to add / remove");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    createDialogList();
    return jbList;
  }

  private void createDialogList() {
    Collections.reverse(hierarchy);
    jbList = new JBList(hierarchy.toArray());
    jbList.setCellRenderer(new CellRenderer());
    jbList.setSelectedIndex(hierarchy.size() - 1);
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return jbList;
  }
}

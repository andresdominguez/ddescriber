package com.karateca.ddescriber.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.karateca.ddescriber.TestFindResult;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Dialog extends DialogWrapper {
  private final List<TestFindResult> hierarchy;
  private JBList jbList;

  public Dialog(@Nullable Project project, List<TestFindResult> hierarchy) {
    super(project);
    this.hierarchy = hierarchy;
    init();
    setTitle("Select the Test or Suite to Add / Remove");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    createDialogList();
    return jbList;
  }

  private void createDialogList() {
    jbList = new JBList(hierarchy.toArray());
    jbList.setCellRenderer(new CellRenderer());

    // Select the closest element found from the current position.
    jbList.setSelectedIndex(hierarchy.size() - 1);
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return jbList;
  }

  public TestFindResult getSelectedValue() {
    return (TestFindResult) jbList.getSelectedValue();
  }

//  @Override
//  protected Action[] createActions() {
//    DialogWrapperExitAction okButton = new DialogWrapperExitAction("OK", 1);
//    DialogWrapperExitAction removeAll = new DialogWrapperExitAction("A", 2);
//
//    return new Action[]{okButton, removeAll};
//  }
}

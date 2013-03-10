package com.karateca.ddescriber.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.TestFindResult;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Dialog extends DialogWrapper {
  public static final int CLEAN_CURRENT_EXIT_CODE = 100;
  public static final int REMOVE_ALL_PROJECT_EXIT_CODE = 200;
  private final Hierarchy hierarchy;
  private JBList jbList;
  public static final int VISIBLE_ROW_COUNT = 13;

  public Dialog(@Nullable Project project, Hierarchy hierarchy) {
    super(project);
    this.hierarchy = hierarchy;
    init();
    setTitle("Select the Test or Suite to Add / Remove");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    List<TestFindResult> elements = hierarchy.getUnitTestsForCurrentDescribe();

    jbList = new JBList(elements.toArray());
    jbList.setVisibleRowCount(VISIBLE_ROW_COUNT);

    // Use a custom cell renderer to paint green.
    jbList.setCellRenderer(new CellRenderer());

    // Select the closest element found from the current position.
    int closestIndex = elements.indexOf(hierarchy.getClosest());
    jbList.setSelectedIndex(closestIndex);

    // Create a scroll pane and make sure the selected row is in the middle.
    JBScrollPane scrollPane = new JBScrollPane(jbList);
    int minIndex = Math.max(0, closestIndex - VISIBLE_ROW_COUNT / 2);
    int maxIndex = Math.min(elements.size() - 1, closestIndex + VISIBLE_ROW_COUNT / 2);
    jbList.scrollRectToVisible(jbList.getCellBounds(minIndex, maxIndex));

    return scrollPane;
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return jbList;
  }

  public TestFindResult getSelectedValue() {
    return (TestFindResult) jbList.getSelectedValue();
  }

  @Override
  protected Action[] createLeftSideActions() {
    return new Action[]{
        new DialogWrapperExitAction("Remove all", CLEAN_CURRENT_EXIT_CODE)
//        , new DialogWrapperExitAction("Remove all in project", REMOVE_ALL_PROJECT_EXIT_CODE)
    };
  }
}

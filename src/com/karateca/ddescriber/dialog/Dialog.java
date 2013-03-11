package com.karateca.ddescriber.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ListSpeedSearch;
import com.intellij.ui.SpeedSearchComparator;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.karateca.ddescriber.Hierarchy;
import com.karateca.ddescriber.TestFindResult;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Dialog extends DialogWrapper {
  public static final int CLEAN_CURRENT_EXIT_CODE = 100;
  public static final int SHOW_ALL_EXIT_CODE = 101;
  public static final int SHOW_DESCRIBE_EXIT_CODE = 102;
  public static final int REMOVE_ALL_PROJECT_EXIT_CODE = 200;

  protected final Hierarchy hierarchy;
  private JBList jbList;
  private static final int VISIBLE_ROW_COUNT = 13;
  private final boolean showAll;

  private final DialogWrapper.DialogWrapperExitAction showDescribeAction =
      new DialogWrapperExitAction("Show current describe", SHOW_DESCRIBE_EXIT_CODE);
  private final DialogWrapper.DialogWrapperExitAction showFileAction =
      new DialogWrapperExitAction("Show all in file", SHOW_ALL_EXIT_CODE);

  public Dialog(@Nullable Project project, Hierarchy hierarchy, boolean showAll) {
    super(project);
    this.hierarchy = hierarchy;
    this.showAll = showAll;
    init();
    setTitle("Select the Test or Suite to Add / Remove");
  }

  @Nullable
  @Override
  protected JComponent createCenterPanel() {
    List<TestFindResult> elements = getElementsToShow();

    jbList = new JBList(elements.toArray());
    jbList.setVisibleRowCount(VISIBLE_ROW_COUNT);

    // Use a custom cell renderer to paint green.
    jbList.setCellRenderer(new CellRenderer());

    // Add case insensitive search
    new ListSpeedSearch(jbList) {
      @Override
      protected boolean compare(String text, String pattern) {
        return super.compare(text.toLowerCase(), pattern.toLowerCase());
      }
    }.setComparator(new SpeedSearchComparator(false));

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

  List<TestFindResult> getElementsToShow() {
    return showAll ? hierarchy.getAllUnitTests() : hierarchy.getUnitTestsForCurrentDescribe();
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return jbList;
  }

  public List<TestFindResult> getSelectedValues() {
    ArrayList<TestFindResult> elements = new ArrayList<TestFindResult>();
    for (Object item : jbList.getSelectedValues()) {
      elements.add((TestFindResult) item);
    }
    return elements;
  }

  @Override
  protected Action[] createLeftSideActions() {
    return new Action[]{
        new DialogWrapperExitAction("Clean file", CLEAN_CURRENT_EXIT_CODE),
        getShowInAction(),
//        , new DialogWrapperExitAction("Remove all in project", REMOVE_ALL_PROJECT_EXIT_CODE)
    };
  }

  private DialogWrapperExitAction getShowInAction() {
    return showAll ? showDescribeAction : showFileAction;
  }
}

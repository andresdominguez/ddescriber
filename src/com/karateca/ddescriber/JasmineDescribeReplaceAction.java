package com.karateca.ddescriber;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.karateca.ddescriber.dialog.TreeViewDialog;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Dominguez.
 *         TODO: Add button to remove all dd, ii in the project
 *         TODO: Add tests for js files with double quotes
 */
public class JasmineDescribeReplaceAction extends AnAction {

  private Project project;
  private DocumentImpl document;
  private JasmineFinder jasmineFinder;
  private EditorImpl editor;

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    document = (DocumentImpl) editor.getDocument();

    jasmineFinder = new JasmineFinder(project, document);

    // Async callback to get the search results for it( and describe(
    jasmineFinder.addResultsReadyListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource().equals("LinesFound")) {
          showDialog();
        }
      }
    });
    jasmineFinder.findText();
  }

  private void showDialog() {
    // Open a pop-up to select which describe() or it() you want to change.
    Hierarchy hierarchy = new Hierarchy(document, jasmineFinder.getFindResults(), editor.getCaretModel().getOffset());

    TreeViewDialog dialog = new TreeViewDialog(project, hierarchy);
    dialog.show();

    int exitCode = dialog.getExitCode();

    List<TestFindResult> elements = null;

    switch (exitCode) {
      case TreeViewDialog.CLEAN_CURRENT_EXIT_CODE:
        // Clean the current file.
        elements = hierarchy.getMarkedElements();
        break;
      case TreeViewDialog.OK_EXIT_CODE:
        // Flip the selected elements.
        elements = dialog.getSelectedValues();
        break;
      case TreeViewDialog.GO_TO_TEST_EXIT_CODE:
        goToSelectedTest(dialog.getSelectedTest());
        break;
    }

    // Reverse the order to do it from bottom to top.
    if (elements != null) {
      Collections.reverse(elements);
      changeSelectedLineRunningCommand(elements.toArray(new TestFindResult[elements.size()]));
    }
  }

  private void goToSelectedTest(TestFindResult selectedTest) {
    editor.getCaretModel().moveToOffset(selectedTest.getStartOffset());
  }

  /**
   * Change the contents of the selected line. Wrap the call into command and
   * write actions to support undo.
   *
   * @param testFindResults The lines that have to change.
   */
  private void changeSelectedLineRunningCommand(final TestFindResult... testFindResults) {
    ActionUtil.runWriteActionInsideCommand(project, new Runnable() {
      @Override
      public void run() {
        for (TestFindResult testFindResult : testFindResults) {
          changeSelectedLine(testFindResult);
        }
      }
    });
  }

  /**
   * Perform the replace for the selected line. It will add or remove a
   * "d" from describe() and an "i" form it().
   *
   * @param testFindResult The line that has to change.
   */
  private void changeSelectedLine(TestFindResult testFindResult) {
    String newText;
    boolean markedForRun = testFindResult.isMarkedForRun();

    if (testFindResult.isDescribe()) {
      newText = markedForRun ? "describe(" : "ddescribe(";
    } else {
      newText = markedForRun ? "it(" : "iit(";
    }

    int start = testFindResult.getStartOffset();
    int end = testFindResult.getEndOffset();

    document.replaceString(start, end, newText);
  }
}

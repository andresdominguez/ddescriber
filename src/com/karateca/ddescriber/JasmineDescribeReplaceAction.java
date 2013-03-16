package com.karateca.ddescriber;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.karateca.ddescriber.dialog.TreeViewDialog;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TestFindResult;

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
  private EditorImpl editor;
  private JasmineFile jasmineFile;

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    document = (DocumentImpl) editor.getDocument();

    jasmineFile = new JasmineFile(project, editor.getVirtualFile());

    // Async callback to get the search results for it( and describe(
    jasmineFile.addResultsReadyListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        showDialog();
      }
    });
    jasmineFile.buildTreeNodeAsync();
  }

  private void showDialog() {
    // Open a pop-up to select which describe() or it() you want to change.
    TreeViewDialog dialog = new TreeViewDialog(project, jasmineFile, editor.getCaretModel().getOffset());
    dialog.show();

    int exitCode = dialog.getExitCode();

    List<TestFindResult> elements = null;

    switch (exitCode) {
      case TreeViewDialog.CLEAN_CURRENT_EXIT_CODE:
        // Clean the current file.
        elements = jasmineFile.getElementsMarkedToRun();
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

      JasmineDescriberNotifier instance = JasmineDescriberNotifier.getInstance();

      if (exitCode == TreeViewDialog.CLEAN_CURRENT_EXIT_CODE || exitCode == TreeViewDialog.OK_EXIT_CODE) {
        instance.testWasChanged(jasmineFile);
      }
    }
  }

  private void goToSelectedTest(TestFindResult selectedTest) {
    // TODO: scoll.
    editor.getCaretModel().moveToOffset(selectedTest.getStartOffset());
//    editor.getScrollPane().scrollRectToVisible(new Rectangle());
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
   * @param test The line that has to change.
   */
  private void changeSelectedLine(TestFindResult test) {
    String newText;

    if (test.isDescribe()) {
      newText = test.isMarkedForRun() ? "describe(" : "ddescribe(";
    } else {
      newText = test.isMarkedForRun() ? "it(" : "iit(";
    }

    int start = test.getStartOffset();
    int end = test.getEndOffset();

    document.replaceString(start, end, newText);
  }
}

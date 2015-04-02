package com.karateca.ddescriber;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.dialog.DescriberDialog;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TestState;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

/**
 * @author Andres Dominguez.
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
    if (editor == null) {
      return;
    }
    document = (DocumentImpl) editor.getDocument();

    VirtualFile virtualFile = editor.getVirtualFile();
    if (virtualFile == null) {
      return;
    }

    jasmineFile = new JasmineFile(project, virtualFile);

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
    DescriberDialog dialog = new DescriberDialog(project, jasmineFile, editor.getCaretModel().getOffset());
    dialog.show();

    switch (dialog.getExitCode()) {
      case DescriberDialog.CLEAN_CURRENT_EXIT_CODE:
        // Clean the current file.
        jasmineFile.cleanFile();
        break;
      case DescriberDialog.OK_EXIT_CODE:
        // Change all of the pending changes.
        List<TestFindResult> pendingChanges = dialog.getPendingChanges();

        // If there are no pending changes then flip the currently selected node.
        if (pendingChanges.size() == 0) {
          TestFindResult selectedTest = dialog.getSelectedTest();
          if (selectedTest == null) {
            return;
          }

          if (selectedTest.getTestState() == TestState.NotModified) {
            selectedTest.setPendingChangeState(TestState.Included);
          } else {
            selectedTest.setPendingChangeState(TestState.RolledBack);
          }

          pendingChanges.add(selectedTest);
        }

        ActionUtil.changeTestList(project, document, pendingChanges, dialog.jasmineSyntax);
        break;
      case DescriberDialog.GO_TO_TEST_EXIT_CODE:
        goToSelectedTest(dialog.getSelectedTest());
        break;
    }
  }

  private void goToSelectedTest(TestFindResult selectedTest) {
    editor.getCaretModel().moveToOffset(selectedTest.getStartOffset());
    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }
}

package com.karateca.ddescriber;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.karateca.ddescriber.dialog.TreeViewDialog;
import com.karateca.ddescriber.model.JasmineFile;
import com.karateca.ddescriber.model.JasmineFileImpl;
import com.karateca.ddescriber.model.TestFindResult;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Andres Dominguez.
 * TODO: Add tests for js files with double quotes
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

    jasmineFile = new JasmineFileImpl(project, editor.getVirtualFile());

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

    switch (dialog.getExitCode()) {
      case TreeViewDialog.CLEAN_CURRENT_EXIT_CODE:
        // Clean the current file.
        jasmineFile.cleanFile();
        break;
      case TreeViewDialog.OK_EXIT_CODE:
        // Flip the selected elements.
        ActionUtil.changeSelectedLineRunningCommand(project, document, dialog.getSelectedValues());
        break;
      case TreeViewDialog.GO_TO_TEST_EXIT_CODE:
        goToSelectedTest(dialog.getSelectedTest());
        break;
    }
  }

  private void goToSelectedTest(TestFindResult selectedTest) {
    editor.getCaretModel().moveToOffset(selectedTest.getStartOffset());
    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
  }
}

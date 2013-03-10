package com.karateca.ddescriber;

import com.intellij.find.FindResult;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.karateca.ddescriber.dialog.Dialog;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

/**
 * @author Andres Dominguez.
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
    VirtualFile virtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
    document = (DocumentImpl) editor.getDocument();

    jasmineFinder = new JasmineFinder(project, document, virtualFile);

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
    List<FindResult> findResults = jasmineFinder.getFindResults();
    Hierarchy hierarchy = new Hierarchy(document, findResults, editor.getCaretModel().getOffset());
    Dialog dialog = new Dialog(project, hierarchy);
    dialog.show();

    if (dialog.getExitCode() != Dialog.OK_EXIT_CODE) {
      return;
    }

    changeSelectedLineRunningCommand(dialog.getSelectedValue());
  }

  /**
   * Change the contents of the selected line. Wrap the call into command and
   * write actions to support undo.
   *
   * @param selectedValue The line that has to change.
   */
  private void changeSelectedLineRunningCommand(final TestFindResult selectedValue) {
    CommandProcessor.getInstance().executeCommand(project, new Runnable() {
      @Override
      public void run() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          @Override
          public void run() {
            changeSelectedLine(selectedValue);
          }
        });
      }
    }, "Change describe", null);
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

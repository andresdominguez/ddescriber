package com.karateca;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.karateca.ddescriber.dialog.Dialog;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineDescribeReplaceAction extends AnAction {

  private Project project;
  private DocumentImpl document;
  private JasmineFinder jasmineFinder;

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    EditorImpl editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    VirtualFile virtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
    document = (DocumentImpl) editor.getDocument();

    jasmineFinder = new JasmineFinder(project, document, editor, virtualFile);

    // Async callback to get the search results for it( and describe(
    jasmineFinder.addResultsReadyListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource().equals("LinesFound")) {
          showDialog();
        }
      }
    });
    jasmineFinder.findText("iit\\(|ddescribe\\(|it\\(|describe\\(", true);
  }

  private void showDialog() {
    // Open a pop-up to select which describe() or it() you want to change.
    Dialog dialog = new Dialog(project, jasmineFinder.getHierarchy());
    dialog.show();
    int exitCode = dialog.getExitCode();

    changeSelectedLineRunningCommand(dialog.getSelectedValue());
  }

  /**
   * Change the contents of the selected line. Wrap the call into command and
   * write actions to support undo.
   * @param selectedValue The line that has to change.
   */
  private void changeSelectedLineRunningCommand(final LineFindResult selectedValue) {
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
   * @param lineFindResult The line that has to change.
   */
  private void changeSelectedLine(LineFindResult lineFindResult) {
    String newText;
    boolean markedForRun = lineFindResult.isMarkedForRun();

    if (lineFindResult.isDescribe()) {
      newText = markedForRun ? "describe(" : "ddescribe(";
    } else {
      newText = markedForRun ? "it(" : "iit(";
    }

    int start = lineFindResult.getStartOffset();
    int end = lineFindResult.getEndOffset();

    document.replaceString(start, end, newText);
  }
}

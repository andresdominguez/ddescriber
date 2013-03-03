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

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineDescribeReplaceAction extends AnAction {

  protected Project project;
  protected EditorImpl editor;
  protected VirtualFile virtualFile;
  protected DocumentImpl document;
  private JasmineFinder jasmineFinder;

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    virtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
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
    // Reverse the order to show the top parent first all the way down
    // to the current position.
    final List<LineFindResult> hierarchy = jasmineFinder.getHierarchy();
    Collections.reverse(hierarchy);

    // Select the closest element found from the current position.
    final JBList jbList = new JBList(hierarchy.toArray());
    jbList.setSelectedIndex(hierarchy.size() - 1);

    // Open a pop-up to select which describe() or it() you want to change.
    JBPopupFactory.getInstance()
            .createListPopupBuilder(jbList)
            .setTitle("Select the test or suite to add / remove")
            .setItemChoosenCallback(new Runnable() {
              public void run() {
                if (jbList.getSelectedValue() != null) {
                  changeSelectedLineRunningCommand((LineFindResult) jbList.getSelectedValue());
                }
              }
            })
            .createPopup()
            .showCenteredInCurrentWindow(project);
  }

  /**
   * Change the contents of the selected line. Wrap the call into command and
   * write actions to support undo.
   * @param selectedValue
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
    String newText = "";
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

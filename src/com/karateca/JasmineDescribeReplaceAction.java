package com.karateca;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
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
          processSearchResultsAndShowDialog();
        }
      }
    });
    jasmineFinder.findText("iit\\(|ddescribe\\(|it\\(|describe\\(", true);
  }

  private void processSearchResultsAndShowDialog() {
    // Filter the hierarchy.
    int currentIndentation = Integer.MAX_VALUE;
    List<LineFindResult> hierarchy = new ArrayList<LineFindResult>();

    // Find all the parents from the current scope.
    for (LineFindResult line : jasmineFinder.getLineFindResults()) {
      if (line.getIndentation() < currentIndentation) {
        currentIndentation = line.getIndentation();
        hierarchy.add(line);
      }
    }

    showDialog(hierarchy);
  }

  private void showDialog(final List<LineFindResult> hierarchy) {
    List<String> itemNames = new ArrayList<String>();
    for (LineFindResult lineFindResult : hierarchy) {
      itemNames.add(lineFindResult.getLineText());
    }

    final JBList theList = new JBList(itemNames.toArray());

    JBPopupFactory.getInstance()
            .createListPopupBuilder(theList)
            .setTitle("Select the test or suite to add / remove")

            .setItemChoosenCallback(new Runnable() {
              public void run() {
                processSelectedLine(theList, hierarchy);
              }
            })
            .createPopup().
            showCenteredInCurrentWindow(project);
  }

  private void processSelectedLine(final JBList theList, final List<LineFindResult> hierarchy) {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        int selectedIndex = theList.getSelectedIndex();
        LineFindResult lineFindResult = hierarchy.get(selectedIndex);
        changeSelectedLine(lineFindResult);
      }
    });
  }

  private void changeSelectedLine(LineFindResult lineFindResult) {
    int start = lineFindResult.getStartOffset();
    int end = lineFindResult.getEndOffset();
    String newText = "";
    boolean markedForRun = lineFindResult.isMarkedForRun();

    if (lineFindResult.isDescribe()) {
      newText = markedForRun ? "describe(" : "ddescribe(";
    } else {
      newText = markedForRun ? "it(" : "iit(";
    }

    document.replaceString(start, end, newText);
  }
}

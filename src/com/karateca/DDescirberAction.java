package com.karateca;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class DDescirberAction extends AnAction {

  protected Project project;
  protected EditorImpl editor;
  protected VirtualFile virtualFile;
  protected DocumentImpl document;

  private JsUnitFinder jsUnitFinder;


  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    virtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
    document = (DocumentImpl) editor.getDocument();

    jsUnitFinder = new JsUnitFinder(project, document, editor, virtualFile);

    jsUnitFinder.addResultsReadyListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource().equals("LinesFound")) {
          showResultsFound();

        }
      }
    });
    jsUnitFinder.findText("iit\\(|ddescribe\\(|it\\(|describe\\(", true);
  }

  private void showResultsFound() {
    // Filter the hierarchy.
    int currentIndentation = Integer.MAX_VALUE;
    List<LineFindResult> hierarchy = new ArrayList<LineFindResult>();

    // Find all the parents from the current scope.
    for (LineFindResult line : jsUnitFinder.getLineFindResults()) {
      if (line.getIndentation() < currentIndentation) {
        currentIndentation = line.getIndentation();
        hierarchy.add(line);
      }
    }

    System.out.println("");
  }
}

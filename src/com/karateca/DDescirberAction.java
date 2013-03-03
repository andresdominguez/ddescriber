package com.karateca;

import com.intellij.find.FindResult;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.popup.AbstractPopup;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;

/**
 * @author Andres Dominguez.
 */
public class DDescirberAction extends AnAction {

  protected Project project;
  protected EditorImpl editor;
  protected AbstractPopup popup;
  protected VirtualFile virtualFile;
  protected DocumentImpl document;

  private Font font;
  private EditorColorsScheme scheme;

  private AceFinder aceFinder;

  private HashMap<String, Integer> textAndOffsetHash = new HashMap<String, Integer>();


  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(e.getData(PlatformDataKeys.EDITOR) != null);
  }

  public void actionPerformed(AnActionEvent actionEvent) {
    project = actionEvent.getData(PlatformDataKeys.PROJECT);
    editor = (EditorImpl) actionEvent.getData(PlatformDataKeys.EDITOR);
    virtualFile = actionEvent.getData(PlatformDataKeys.VIRTUAL_FILE);
    document = (DocumentImpl) editor.getDocument();

    scheme = EditorColorsManager.getInstance().getGlobalScheme();
    font = new Font(scheme.getEditorFontName(), Font.BOLD, scheme.getEditorFontSize());

    aceFinder = new AceFinder(project, document, editor, virtualFile);

    aceFinder.addResultsReadyListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent changeEvent) {
        if (changeEvent.getSource().equals("LinesFound")) {
          showResultsFound();

        }
      }
    });
    aceFinder.findText("it\\(|describe\\(", true);
  }

  private void showResultsFound() {
    System.out.println("Done");
    CharSequence charsSequence = document.getCharsSequence();
    for (FindResult findResult : aceFinder.getFindResults()) {

      LineFindResult line = new LineFindResult(document, findResult);
      System.out.println("");
    }


  }
}

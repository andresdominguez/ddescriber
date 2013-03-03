package com.karateca;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EventDispatcher;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to find suites and unit tests in a JsUnit JavaScript file.
 */
public class JsUnitFinder {

  private final Project project;
  private final DocumentImpl document;
  private final EditorImpl editor;
  private final VirtualFile virtualFile;
  private FindManager findManager;
  private FindModel findModel;
  private List<LineFindResult> lineFindResults;

  private final EventDispatcher<ChangeListener> myEventDispatcher = EventDispatcher.create(ChangeListener.class);

  public List<LineFindResult> getLineFindResults() {
    return lineFindResults;
  }

  public JsUnitFinder(Project project, DocumentImpl document, EditorImpl editor, VirtualFile virtualFile) {
    this.project = project;
    this.document = document;
    this.editor = editor;
    this.virtualFile = virtualFile;
  }

  protected FindModel createFindModel(FindManager findManager) {
    FindModel clone = (FindModel) findManager.getFindInFileModel().clone();
    clone.setFindAll(true);
    clone.setFromCursor(true);
    clone.setForward(false);
    clone.setWholeWordsOnly(false);
    clone.setCaseSensitive(true);
    clone.setSearchHighlighters(true);
    clone.setPreserveCase(false);

    return clone;
  }

  public void findText(final String text, boolean isRegEx) {
    findManager = FindManager.getInstance(project);
    findModel = createFindModel(findManager);

    findModel.setStringToFind(text);
    findModel.setRegularExpressions(isRegEx);

    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        findAll();
        myEventDispatcher.getMulticaster().stateChanged(new ChangeEvent("LinesFound"));
      }
    });
  }

  private void findAll() {
    lineFindResults = new ArrayList<LineFindResult>();
    CharSequence text = document.getCharsSequence();
    int offset = editor.getCaretModel().getOffset();

    while (true) {
      FindResult result = findManager.findString(text, offset, findModel, virtualFile);

      if (!result.isStringFound()) {
        return;
      }

      offset = result.getStartOffset();
      lineFindResults.add(new LineFindResult(document, result));
    }
  }

  /**
   * Register for change events.
   *
   * @param changeListener
   */
  public void addResultsReadyListener(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }
}

package com.karateca.ddescriber;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.DocumentImpl;
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
class JasmineFinder {

  private static final String FIND_REGEXP = "iit\\(|ddescribe\\(|it\\(|describe\\(";
  private final Project project;
  private final DocumentImpl document;
  private final VirtualFile virtualFile;
  List<FindResult> findResults;

  private final EventDispatcher<ChangeListener> myEventDispatcher = EventDispatcher.create(ChangeListener.class);

  public JasmineFinder(Project project, DocumentImpl document, VirtualFile virtualFile) {
    this.project = project;
    this.document = document;
    this.virtualFile = virtualFile;
  }

  FindModel createFindModel(FindManager findManager) {
    FindModel clone = (FindModel) findManager.getFindInFileModel().clone();
    clone.setFindAll(true);
    clone.setFromCursor(true);
    clone.setForward(true);
    clone.setWholeWordsOnly(false);
    clone.setCaseSensitive(true);
    clone.setSearchHighlighters(true);
    clone.setPreserveCase(false);

    return clone;
  }

  public void findText() {
    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        findAll();
        if (findResults.size() > 0) {
          myEventDispatcher.getMulticaster().stateChanged(new ChangeEvent("LinesFound"));
        }
      }
    });
  }

  void findAll() {
    FindManager findManager = FindManager.getInstance(project);
    FindModel findModel = createFindModel(findManager);
    findModel.setStringToFind(FIND_REGEXP);
    findModel.setRegularExpressions(true);

    findResults = new ArrayList<FindResult>();

    CharSequence text = document.getCharsSequence();
    int offset = 0;

    while (true) {
      FindResult result = findManager.findString(text, offset, findModel, virtualFile);

      if (!result.isStringFound()) {
        return;
      }

      offset = result.getEndOffset();

      findResults.add(result);
    }
  }

  /**
   * Register for change events.
   *
   * @param changeListener The listener to be added.
   */
  public void addResultsReadyListener(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }

  public List<FindResult> getFindResults() {
    return findResults;
  }
}

package com.karateca.ddescriber;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindResult;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.EventDispatcher;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to find suites and unit tests in a JsUnit JavaScript file.
 */
public class JasmineFinder {

  private static final String FIND_REGEXP = "iit\\(|ddescribe\\(|it\\(|describe\\(";
  private final Project project;
  private final Document document;
  public List<FindResult> findResults;

  public JasmineFinder(Project project, Document document) {
    this.project = project;
    this.document = document;
  }

  FindModel createFindModel(FindManager findManager) {
    FindModel clone = (FindModel) findManager.getFindInFileModel().clone();
    clone.setFindAll(true);
    clone.setFromCursor(true);
    clone.setForward(true);
    clone.setWholeWordsOnly(false);
    clone.setCaseSensitive(true);
    clone.setRegularExpressions(true);
    clone.setWholeWordsOnly(true);
    clone.setStringToFind(FIND_REGEXP);

    return clone;
  }

  public void findAll() {
    FindManager findManager = FindManager.getInstance(project);
    FindModel findModel = createFindModel(findManager);

    findResults = new ArrayList<FindResult>();

    CharSequence text = document.getCharsSequence();
    int offset = 0;

    while (true) {
      FindResult result = findManager.findString(text, offset, findModel);

      if (!result.isStringFound()) {
        return;
      }

      offset = result.getEndOffset();

      findResults.add(result);
    }
  }

  public List<FindResult> getFindResults() {
    return findResults;
  }
}

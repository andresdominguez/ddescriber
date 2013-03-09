package com.karateca.ddescriber;

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
class JasmineFinder {

  private final Project project;
  private final DocumentImpl document;
  private final EditorImpl editor;
  private final VirtualFile virtualFile;
  private FindManager findManager;
  private FindModel findModel;
  private List<TestFindResult> testFindResults;

  private final EventDispatcher<ChangeListener> myEventDispatcher = EventDispatcher.create(ChangeListener.class);

  public JasmineFinder(Project project, DocumentImpl document, EditorImpl editor, VirtualFile virtualFile) {
    this.project = project;
    this.document = document;
    this.editor = editor;
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
    testFindResults = new ArrayList<TestFindResult>();
    CharSequence text = document.getCharsSequence();
    int offset = 0;

    while (true) {
      FindResult result = findManager.findString(text, offset, findModel, virtualFile);

      if (!result.isStringFound()) {
        return;
      }

      offset = result.getEndOffset();
      testFindResults.add(new TestFindResult(document, result));
    }
  }

  public Hierarchy getHierarchy() {
    TestFindResult closest = getClosestTestFromCaret();

    List<TestFindResult> matches = new ArrayList<TestFindResult>();

    int index = testFindResults.indexOf(closest);

    matches.add(closest);

    if (!closest.isDescribe()) {
      // Add elements after with the same indentation.
      for (int i = index + 1; i < testFindResults.size(); i++) {
        TestFindResult element = testFindResults.get(i);
        if (element.getIndentation() != closest.getIndentation()) {
          break;
        }
        matches.add(element);
      }

      // Add elements before with the same indentation.
      for (int i = index - 1; i >= 0; i--) {
        TestFindResult element = testFindResults.get(i);
        if (element.getIndentation() != closest.getIndentation()) {
          break;
        }
        matches.add(0, element);
      }
    }

    // Add the parents.
    int currentIndentation = closest.getIndentation();
    for (int i = index - 1; i >= 0; i--) {
      TestFindResult element = testFindResults.get(i);
      if (element.getIndentation() < currentIndentation) {
        matches.add(0, element);
        currentIndentation = element.getIndentation();
      }
    }

    return new Hierarchy(closest, matches);
  }

  /**
   * Get the closest unit test or suite from the current caret position.
   *
   * @return The closest test or suite.
   */
  public TestFindResult getClosestTestFromCaret() {
    int caretOffset = editor.getCaretModel().getOffset();
    int lineNumber = document.getLineNumber(caretOffset) + 1;
    TestFindResult closest = null;
    int minDistance = Integer.MAX_VALUE;

    // Get the closest unit test or suite from the current caret.
    for (TestFindResult testFindResult : testFindResults) {
      int distance = Math.abs(lineNumber - testFindResult.getLineNumber());
      if (distance < minDistance) {
        closest = testFindResult;
        minDistance = distance;
      } else {
        return closest;
      }
    }

    return closest;
  }

  /**
   * Register for change events.
   *
   * @param changeListener The listener to be added.
   */
  public void addResultsReadyListener(ChangeListener changeListener) {
    myEventDispatcher.addListener(changeListener);
  }
}

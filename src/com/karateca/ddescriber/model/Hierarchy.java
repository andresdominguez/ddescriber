package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
class Hierarchy {
  private final Document document;
  private final TestFindResult closest;
  private final List<TestFindResult> testFindResults;

  public Hierarchy(Document document, List<FindResult> findResults, int caretOffset) {
    this.document = document;
    testFindResults = new ArrayList<TestFindResult>();
    for (FindResult findResult : findResults) {
      TestFindResult result = new TestFindResult(document, findResult);
      testFindResults.add(result);
    }

    // TODO: no longer necessary
    this.closest = getClosestTestFromCaret(caretOffset);
  }

  public Hierarchy(Document document, List<FindResult> findResults) {
    this(document, findResults, 0);
  }

  /**
   * Get the closest unit test or suite from the current caret position.
   *
   * @param caretOffset The current caret position in the editor.
   * @return The closest test or suite.
   */
  public TestFindResult getClosestTestFromCaret(int caretOffset) {
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

  public TestFindResult getClosest() {
    return closest;
  }

  public List<TestFindResult> getMarkedElements() {
    ArrayList<TestFindResult> results = new ArrayList<TestFindResult>();

    for (TestFindResult element : testFindResults) {
      if (element.getTestState() != TestState.NotModified) {
        results.add(element);
      }
    }

    return results;
  }

  public List<TestFindResult> getAllUnitTests() {
    return testFindResults;
  }
}

package com.karateca.ddescriber;

import com.intellij.find.FindResult;
import com.intellij.openapi.editor.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Hierarchy {
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

    this.closest = getClosestTestFromCaret(caretOffset);
  }

  /**
   * Get the closest unit test or suite from the current caret position.
   *
   * @param caretOffset The current caret position in the editor.
   * @return The closest test or suite.
   */
  private TestFindResult getClosestTestFromCaret(int caretOffset) {
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

  public List<TestFindResult> getUnitTestsForCurrentDescribe() {
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

    return matches;
  }

  public TestFindResult getClosest() {
    return closest;
  }
}

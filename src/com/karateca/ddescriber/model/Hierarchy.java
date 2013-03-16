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

  /**
   * Add all the it() and describe() elements before and after with the same indentation.
   *
   * @param matches     the elements in the current describe block.
   * @param pivotIndex  the index of the closest it() or describe().
   * @param indentation filter elements before and after with this indentation value.
   */
  private void addAllElementsWithSameIndentation(List<TestFindResult> matches, int pivotIndex, int indentation) {
    // Add elements before with the same indentation.
    for (int i = pivotIndex - 1; i >= 0; i--) {
      TestFindResult element = testFindResults.get(i);
      if (element.getIndentation() != indentation) {
        break;
      }
      matches.add(0, element);
    }

    // Add elements after with the same indentation.
    for (int i = pivotIndex + 1; i < testFindResults.size(); i++) {
      TestFindResult element = testFindResults.get(i);

      if (element.getIndentation() != indentation) {
        break;
      }
      matches.add(element);
    }
  }

  /**
   * Add the parent elements to the current set of matched elements.
   *
   * @param matches    the elements in the current describe block.
   * @param pivotIndex the index of the closest it() or describe().
   */
  private void addParentElements(List<TestFindResult> matches, int pivotIndex) {
    int currentIndentation = closest.getIndentation();
    for (int i = pivotIndex - 1; i >= 0; i--) {
      TestFindResult element = testFindResults.get(i);
      if (element.getIndentation() < currentIndentation) {
        matches.add(0, element);
        currentIndentation = element.getIndentation();
      }
    }
  }

  public TestFindResult getClosest(int caretOffset) {
    return closest;
  }

  public List<TestFindResult> getMarkedElements() {
    ArrayList<TestFindResult> results = new ArrayList<TestFindResult>();

    for (TestFindResult element : testFindResults) {
      if (element.isMarkedForRun()) {
        results.add(element);
      }
    }

    return results;
  }

  public List<TestFindResult> getAllUnitTests() {
    return testFindResults;
  }
}

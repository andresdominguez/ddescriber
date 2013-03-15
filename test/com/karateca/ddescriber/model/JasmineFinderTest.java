package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.karateca.ddescriber.BaseTestCase;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class JasmineFinderTest extends BaseTestCase {

  public void testFindAll() throws Exception {
    List<FindResult> findResults = whenYouFindTestsForJsFile("jasmineTestBefore.js");

    // Then ensure there are 7 tests and suites.
    assertEquals(7, findResults.size());
  }

  public void testWithCaretAtTheTop() {
    List<FindResult> findResults = whenYouFindTestsForJsFile("jasmineTestCaretTop.js");

    // Then ensure there are 6 tests and suites.
    assertEquals(7, findResults.size());
  }

  private List<FindResult> whenYouFindTestsForJsFile(String fileName) {
    prepareScenarioWithTestFile(fileName);
    jasmineFinder.findAll();
    return jasmineFinder.getFindResults();
  }
}

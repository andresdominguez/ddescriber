package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.karateca.ddescriber.BaseTestCase;

import java.util.List;

public class JasmineFinderTest extends BaseTestCase {

  public void testFindAll() throws Exception {
    findsAllTests("jasmine1/jasmineTestBefore.js");
    findsAllTests("jasmine2/jasmineTestBefore.js");
  }

  private void findsAllTests(String fileName) {
    List<FindResult> findResults = whenYouFindTestsForJsFile(fileName);

    // Then ensure there are 9 tests and suites.
    assertEquals(9, findResults.size());
  }

  public void testWithCaretAtTheTop() {
    findsTestsWithCaretAtTheTop("jasmine1/jasmineTestCaretTop.js");
    findsTestsWithCaretAtTheTop("jasmine2/jasmineTestCaretTop.js");
  }

  private void findsTestsWithCaretAtTheTop(String fileName) {
    List<FindResult> findResults = whenYouFindTestsForJsFile(fileName);

    // Then ensure there are 6 tests and suites.
    assertEquals(7, findResults.size());
  }

  public void testShouldFindTestsWithTrickyText() {
    findsTestsWithTrickyText("jasmine1/jasmineWithWeirdRegularExpressions.js");
    findsTestsWithTrickyText("jasmine2/jasmineWithWeirdRegularExpressions.js");
  }

  private void findsTestsWithTrickyText(String fileName) {
    List<FindResult> findResults = whenYouFindTestsForJsFile(
        fileName);

    // Then ensure all the tests were found.
    assertEquals(11, findResults.size());
  }

  private List<FindResult> whenYouFindTestsForJsFile(String fileName) {
    prepareScenarioWithTestFile(fileName);
    jasmineFinder.findAll();
    return jasmineFinder.getFindResults();
  }
}

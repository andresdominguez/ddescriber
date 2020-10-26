package com.karateca.ddescriber.model;

import com.karateca.ddescriber.BaseTestCase;

import java.util.List;

public class HierarchyTest extends BaseTestCase {

  private Hierarchy getHierarchyForTestFile(String testFile) {
    prepareScenarioWithTestFile(testFile);
    jasmineFinder.findAll();

    return new Hierarchy(document, jasmineFinder.findResults, myFixture.getCaretOffset());
  }

  public void testGetClosest() {
    getClosest("jasmine1/jasmineTestBefore.js");
    getClosest("jasmine2/jasmineTestBefore.js");
  }

  private void getClosest(String testFile) {
    // Given that the caret is under "inner it 2".
    Hierarchy hierarchy = getHierarchyForTestFile(testFile);

    // When you find the closest test or fixture.
    TestFindResult closest = hierarchy.getClosest();

    // Then ensure the closest test is "inner it 2".
    assertEquals(33, closest.getLineNumber());
    assertEquals("inner it 2", closest.getTestText());
  }

  public void testGetMarkedElementsJasmine1() {
    getMarkedElements("jasmine1/jasmineTestCaretTop.js");
    getMarkedElements("jasmine2/jasmineTestCaretTop.js");
  }

  private void getMarkedElements(String testFile) {
    // Given that you have the caret at the top.
    Hierarchy hierarchy = getHierarchyForTestFile(testFile);

    // When you get the marked elements.
    List<TestFindResult> elements = hierarchy.getMarkedElements();

    // Then ensure two elements are returned.
    assertEquals(2, elements.size());
    assertEquals(23, elements.get(0).getLineNumber());
    assertEquals(33, elements.get(1).getLineNumber());
  }

  public void testShouldFindExcludedElements() {
    // Given a jasmine file with excluded xdescribe and xit.
    Hierarchy hierarchy = getHierarchyForTestFile("testWihManyLevels.js");

    // When you get the elements.
    List<TestFindResult> tests = hierarchy.getAllUnitTests();

    // Then ensure there are excluded elements.
    TestFindResult itElement = tests.get(11);
    assertEquals("should be excluded 1", itElement.getTestText());
    assertSame(itElement.getTestState(), TestState.Excluded);

    TestFindResult describeElement = tests.get(12);
    assertEquals("excluded 2", describeElement.getTestText());
    assertSame(describeElement.getTestState(), TestState.Excluded);

    // And ensure it found all the tests.
    assertEquals(16, tests.size());
  }

  public void testShouldFindTestsWithTrickyText() {
    shouldFindTestsWithTrickyText("jasmine1/jasmineWithWeirdRegularExpressions.js");
    shouldFindTestsWithTrickyText("jasmine2/jasmineWithWeirdRegularExpressions.js");
  }

  private void shouldFindTestsWithTrickyText(String testFile) {
    // Load a file with tricky text.
    Hierarchy hierarchy = getHierarchyForTestFile(testFile);

    // When you get the elements.
    List<TestFindResult> tests = hierarchy.getAllUnitTests();

    // Then ensure the test count is correct.
    assertEquals(11, tests.size());

    int i = 0;
    assertDescribe(tests.get(i++), "top describe", TestState.NotModified);
    assertIt(tests.get(i++), "should not be included iit", TestState.NotModified);
    assertIt(tests.get(i++), "should not be excluded exit", TestState.NotModified);
    assertIt(tests.get(i++), "should not be a describe()", TestState.NotModified);
    assertIt(tests.get(i++), "should not be a ddescribe", TestState.NotModified);
    assertIt(tests.get(i++), "should be excluded it( iit( describe(", TestState.Excluded);
    assertIt(tests.get(i++), "should be included it( xit( describe( xdescribe(",
        TestState.Included);
    assertDescribe(tests.get(i++), "suite not excluded xdescribe(", TestState.NotModified);
    assertDescribe(tests.get(i++), "suite not included ddescribe(", TestState.NotModified);
    assertDescribe(tests.get(i++), "suite excluded describe( it(", TestState.Excluded);
    assertDescribe(tests.get(i++), "suite included describe( xdescribe( iit(", TestState.Included);
  }

  private void assertDescribe(TestFindResult findResult, String expectedText,
      TestState expectedState) {
    assertEquals(expectedText, findResult.getTestText());
    assertEquals(expectedState, findResult.getTestState());
    assertTrue(findResult.isDescribe());
  }

  private void assertIt(TestFindResult findResult, String expectedText, TestState expectedState) {
    assertEquals(expectedText, findResult.getTestText());
    assertEquals(expectedState, findResult.getTestState());
    assertFalse(findResult.isDescribe());
  }
}

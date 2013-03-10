package com.karateca.ddescriber;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class HierarchyTest extends BaseTestCase {

  private Hierarchy getHierarchyForTestFile(String testFile) {
    prepareScenarioWithTestFile(testFile);
    jasmineFinder.findAll();

    return new Hierarchy(document, jasmineFinder.findResults, myFixture.getCaretOffset());
  }

  public void testGetClosest() throws Exception {
    // Given that the caret is under "inner it 2".
    Hierarchy hierarchy = getHierarchyForTestFile("jasmineTestBefore.js");

    // When you find the closest test or fixture.
    TestFindResult closest = hierarchy.getClosest();

    // Then ensure the closest test is "inner it 2".
    assertEquals(33, closest.getLineNumber());
    assertEquals("        it('inner it 2'", closest.getTestText());
  }

  public void testGetUnitTestsForCurrentDescribe() throws Exception {
    // Given that the caret is under "inner it 2".
    Hierarchy hierarchy = getHierarchyForTestFile("jasmineTestBefore.js");

    // When you the unit tests for the current describe.
    List<TestFindResult> elements = hierarchy.getUnitTestsForCurrentDescribe();

    // Then ensure all the 'its' in the current describe and the parents
    // are returned.
    assertEquals(5, elements.size());
    assertEquals("describe('top describe'", elements.get(0).getTestText());
    assertEquals("    ddescribe('inner describe'", elements.get(1).getTestText());
    assertEquals("        it('inner it 1'", elements.get(2).getTestText());
    assertEquals("        it('inner it 2'", elements.get(3).getTestText());
    assertEquals("        iit('inner it 3'", elements.get(4).getTestText());
  }

  public void testUnitTestsForCurrentDescribeAndCaretAtTheTop() {
    // Given that you have the caret at the top.
    Hierarchy hierarchy = getHierarchyForTestFile("jasmineTestCaretTop.js");

    // When you get the unit tests.
    List<TestFindResult> elements = hierarchy.getUnitTestsForCurrentDescribe();

    // Then ensure the first level inside the top describe is returned.
    assertEquals(4, elements.size());
    int i = 0;
    assertEquals("describe('top describe'", elements.get(i++).getTestText());
    assertEquals("    it('first it'", elements.get(i++).getTestText());
    assertEquals("    it('second it'", elements.get(i++).getTestText());
    assertEquals("    ddescribe('inner describe'", elements.get(i++).getTestText());
  }

  public void testGetMarkedElements() {
    // Given that you have the caret at the top.
    Hierarchy hierarchy = getHierarchyForTestFile("jasmineTestCaretTop.js");

    // When you get the marked elements.
    List<TestFindResult> elements = hierarchy.getMarkedElements();

    // Then ensure two elements are returned.
    assertEquals(2, elements.size());
    assertEquals(23, elements.get(0).getLineNumber());
    assertEquals(33, elements.get(1).getLineNumber());
  }
}

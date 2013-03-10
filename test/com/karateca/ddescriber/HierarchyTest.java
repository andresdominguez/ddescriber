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
    assertEquals(28, closest.getLineNumber());
    assertEquals("        it('inner it 2', function () {", closest.lineText);
  }

  public void testGetUnitTestsForCurrentDescribe() throws Exception {
    // Given that the caret is under "inner it 2".
    Hierarchy hierarchy = getHierarchyForTestFile("jasmineTestBefore.js");

    // When you the unit tests for the current describe.
    List<TestFindResult> elements = hierarchy.getUnitTestsForCurrentDescribe();

    // Then ensure all the 'its' in the current describe and the parents
    // are returned.
    assertEquals(5, elements.size());
    assertEquals("describe('top describe', function () {", elements.get(0).lineText);
    assertEquals("    ddescribe('inner describe', function () {", elements.get(1).lineText);
    assertEquals("        it('inner it 1', function () {", elements.get(2).lineText);
    assertEquals("        it('inner it 2', function () {", elements.get(3).lineText);
    assertEquals("        iit('inner it 3', function () {", elements.get(4).lineText);
  }

  public void testUnitTestsForCurrentDescribeAndCaretAtTheTop() {
    // Given that you have the caret at the top.
    Hierarchy hierarchy = getHierarchyForTestFile("jasmineTestCaretTop.js");

    // When you get the unit tests.
    List<TestFindResult> elements = hierarchy.getUnitTestsForCurrentDescribe();

    // Then ensure the first level inside the top describe is returned.
    assertEquals(4, elements.size());
    int i = 0;
    assertEquals("describe('top describe', function () {", elements.get(i++).lineText);
    assertEquals("    it('first it', function () {", elements.get(i++).lineText);
    assertEquals("    it('second it', function () {", elements.get(i++).lineText);
    assertEquals("    ddescribe('inner describe', function () {", elements.get(i++).lineText);
  }

  public void testGetTestElements() throws Exception {

  }

  public void testGetClosestIndex() throws Exception {

  }
}

package com.karateca.ddescriber.model;

import com.karateca.ddescriber.BaseTestCase;

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
    assertEquals("inner it 2", closest.getTestText());
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

  public void testShouldFindExcludedElements() {
    // Given a jasmine file with excluded xdescribe and xit.
    Hierarchy hierarchy = getHierarchyForTestFile("testWihManyLevels.js");

    // When you get the elements.
    List<TestFindResult> tests = hierarchy.getAllUnitTests();

    // Then ensure there are excluded elements.
    TestFindResult itElement = tests.get(11);
    assertEquals("should be excluded 1", itElement.getTestText());
    assertTrue(itElement.isExcluded());

    TestFindResult describeElement = tests.get(12);
    assertEquals("excluded 2", describeElement.getTestText());
    assertTrue(describeElement.isExcluded());

  }
}

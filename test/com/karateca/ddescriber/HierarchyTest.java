package com.karateca.ddescriber;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class HierarchyTest extends BaseTestCase {

  private Hierarchy hierarchy;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    JasmineFinder jasmineFinder = createJasmineFinder();
    jasmineFinder.findAll();

    hierarchy = new Hierarchy(getDocument(), jasmineFinder.findResults, myFixture.getCaretOffset());
  }

  public void testGetClosest() throws Exception {
    // Given that the caret is under "inner it 2".
    // When you find the closest test or fixture.
    TestFindResult closest = hierarchy.getClosest();

    // Then ensure the closest test is "inner it 2".
    assertEquals(15, closest.getLineNumber());
    assertEquals("        it('inner it 2', function () {", closest.lineText);
  }

  public void testGetHierarchy() throws Exception {
    // Given that the caret is under "inner it 2".
    // When you get the hierarchy.
    TestFindResult closest = hierarchy.getClosest();
    List<TestFindResult> elements = hierarchy.getHierarchy(closest);

    // Then ensure all the 'its' in the current describe and the parents
    // are returned.
    assertEquals(5, elements.size());
    assertEquals("describe('top describe', function () {", elements.get(0).lineText);
    assertEquals("    ddescribe('inner describe', function () {", elements.get(1).lineText);
    assertEquals("        it('inner it 1', function () {", elements.get(2).lineText);
    assertEquals("        it('inner it 2', function () {", elements.get(3).lineText);
    assertEquals("        iit('inner it 3', function () {", elements.get(4).lineText);
  }

  public void testGetTestElements() throws Exception {

  }

  public void testGetClosestIndex() throws Exception {

  }
}

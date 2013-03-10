package com.karateca.ddescriber;

import com.intellij.find.FindResult;
import junit.framework.Assert;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class TestFindResultTest extends BaseTestCase {

  private TestFindResult testFindResult;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    jasmineFinder.findAll();
    List<FindResult> findResults = jasmineFinder.getFindResults();

    testFindResult = new TestFindResult(document, findResults.get(0));
  }

  public void testToString() throws Exception {
    Assert.assertEquals("line:     5: describe('top describe'", testFindResult.toString());
  }
}
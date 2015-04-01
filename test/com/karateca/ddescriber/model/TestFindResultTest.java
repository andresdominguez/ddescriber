package com.karateca.ddescriber.model;

import com.intellij.find.FindResult;
import com.karateca.ddescriber.BaseTestCase;

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
    prepareScenarioWithTestFile("jasmine1/jasmineTestCaretTop.js");
    jasmineFinder.findAll();
    List<FindResult> findResults = jasmineFinder.getFindResults();

    testFindResult = new TestFindResult(document, findResults.get(0));
  }

  public void testToString() throws Exception {
    assertEquals("top describe", testFindResult.toString());
  }
}

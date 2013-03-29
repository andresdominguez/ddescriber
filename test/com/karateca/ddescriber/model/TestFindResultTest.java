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
    prepareScenarioWithTestFile("jasmineTestCaretTop.js");
    jasmineFinder.findAll();
    List<FindResult> findResults = jasmineFinder.getFindResults();

    testFindResult = new TestFindResultImpl(document, findResults.get(0));
  }

  public void testToString() throws Exception {
    Assert.assertEquals("top describe", testFindResult.toString());
  }
}

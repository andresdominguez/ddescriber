package com.karateca.ddescriber.model;

/**
 * @author andresdom@google.com (Andres Dominguez)
 */
public class MockFindResult implements TestFindResult {

  private boolean describe;
  private int indentation;
  private boolean markedForRun;
  private String testText;

  @Override
  public boolean isDescribe() {
    return describe;
  }

  @Override
  public int getIndentation() {
    return indentation;
  }

  @Override
  public boolean isMarkedForRun() {
    return markedForRun;
  }

  @Override
  public int getEndOffset() {
    return 0;
  }

  @Override
  public int getStartOffset() {
    return 0;
  }

  @Override
  public int getLineNumber() {
    return 0;
  }

  @Override
  public String getTestText() {
    return testText;
  }

  public static TestFindResult buildDescribe(String testText) {
    MockFindResult findResult = new MockFindResult();
    findResult.describe = true;
    findResult.testText = testText;
    return findResult;
  }

  public static TestFindResult buildIt(String testText) {
    MockFindResult findResult = new MockFindResult();
    findResult.describe = false;
    findResult.testText = testText;
    return findResult;
  }
}

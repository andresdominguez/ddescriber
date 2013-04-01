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

  public void setDescribe(boolean describe) {
    this.describe = describe;
  }

  @Override
  public int getIndentation() {
    return indentation;
  }

  public void setIndentation(int indentation) {
    this.indentation = indentation;
  }

  @Override
  public boolean isMarkedForRun() {
    return markedForRun;
  }

  public void setMarkedForRun(boolean markedForRun) {
    this.markedForRun = markedForRun;
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

  public void setTestText(String testText) {
    this.testText = testText;
  }

  public static TestFindResult buildDescribe(String testText) {
    MockFindResult findResult = new MockFindResult();
    findResult.setDescribe(true);
    return findResult;
  }

  public static TestFindResult buildIt(String testText) {
    MockFindResult findResult = new MockFindResult();
    findResult.setDescribe(false);
    return findResult;
  }
}

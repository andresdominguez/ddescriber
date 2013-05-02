package com.karateca.ddescriber.model;

/**
 * @author Andres Dominguez.
 */
public class TestCounts {
  private final int testCount;
  private final int includedCount;
  private final int excludedCount;

  public TestCounts(int testCount, int includedCount, int excludedCount) {
    this.testCount = testCount;
    this.includedCount = includedCount;
    this.excludedCount = excludedCount;
  }

  public int getTestCount() {
    return testCount;
  }

  public int getIncludedCount() {
    return includedCount;
  }

  public int getExcludedCount() {
    return excludedCount;
  }
}

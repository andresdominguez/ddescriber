package com.karateca.ddescriber.model;

/**
 * @author Andres Dominguez.
 */
public class TestCounts {
  private final int totalCount;
  private final int runCount;
  private final int excludedCount;

  public TestCounts(int totalCount, int runCount, int excludedCount) {
    this.totalCount = totalCount;
    this.runCount = runCount;
    this.excludedCount = excludedCount;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public int getRunCount() {
    return runCount;
  }

  public int getExcludedCount() {
    return excludedCount;
  }
}

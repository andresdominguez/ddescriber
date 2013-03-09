package com.karateca.ddescriber;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Hierarchy {
  private final TestFindResult closest;
  private final List<TestFindResult> testElements;

  public Hierarchy(TestFindResult closest, List<TestFindResult> testElements) {
    this.closest = closest;
    this.testElements = testElements;
  }

  public TestFindResult getClosest() {
    return closest;
  }

  public List<TestFindResult> getTestElements() {
    return testElements;
  }

  public int getClosestIndex() {
    return testElements.indexOf(closest);
  }
}

package com.karateca.ddescriber;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class Hierarchy {
  private final TestFindResult closest;
  private final List<TestFindResult> hierarchy;

  public Hierarchy(TestFindResult closest, List<TestFindResult> hierarchy) {
    this.closest = closest;
    this.hierarchy = hierarchy;
  }

  public TestFindResult getClosest() {
    return closest;
  }

  public List<TestFindResult> getHierarchy() {
    return hierarchy;
  }
}

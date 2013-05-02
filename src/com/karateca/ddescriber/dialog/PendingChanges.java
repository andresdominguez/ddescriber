package com.karateca.ddescriber.dialog;

import com.karateca.ddescriber.model.TestFindResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andres Dominguez.
 */
public class PendingChanges {

  private final Set<TestFindResult> pendingChanges = new HashSet<TestFindResult>();

  public List<TestFindResult> getTestsToChange() {
    List<TestFindResult> changeList = new ArrayList<TestFindResult>(pendingChanges);

    Collections.sort(changeList, new Comparator<TestFindResult>() {
      @Override
      public int compare(TestFindResult left, TestFindResult right) {
        return left.getStartOffset() - right.getStartOffset();
      }
    });

    return changeList;
  }

  public void itemChanged(TestFindResult testFindResult) {
    pendingChanges.add(testFindResult);
    if (testFindResult.getPendingChangeState() == testFindResult.getTestState()) {
      pendingChanges.remove(testFindResult);
    }
  }
}

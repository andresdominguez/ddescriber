package com.karateca.ddescriber.dialog;

import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TestState;

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

  public void itemChanged(TestFindResult testFindResult, TestState newState) {
    TestState originalState = testFindResult.getTestState();
    TestState pendingState = testFindResult.getPendingChangeState();

    if (pendingState == null) {
      add(testFindResult, newState);
      return;
    }

    boolean alreadyInSet = pendingChanges.contains(testFindResult);

    if (!alreadyInSet) {
      add(testFindResult, newState);
      return;
    }

    // Already in set.
    if (originalState == TestState.NotModified && pendingState == newState) {
      remove(testFindResult);
      return;
    }

    if (originalState == newState && pendingState == TestState.RolledBack) {
      remove(testFindResult);
      return;
    }

    if (originalState == newState && originalState != pendingState) {
      remove(testFindResult);
      return;
    }

    if (originalState != pendingState && pendingState == newState) {
      testFindResult.setPendingChangeState(TestState.RolledBack);
    } else {
      testFindResult.setPendingChangeState(newState);
    }
  }

  private void remove(TestFindResult testFindResult) {
    testFindResult.setPendingChangeState(null);
    pendingChanges.remove(testFindResult);
  }

  private void add(TestFindResult testFindResult, TestState newState) {
    pendingChanges.add(testFindResult);
    if (testFindResult.getTestState() == newState) {
      testFindResult.setPendingChangeState(TestState.RolledBack);
    } else {
      testFindResult.setPendingChangeState(newState);
    }
  }
}

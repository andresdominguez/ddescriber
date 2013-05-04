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

    // Not in pending changes.
    if (!pendingChanges.contains(testFindResult)) {
      add(testFindResult, newState);
      return;
    }

    // Reverting to original not modified state.
    if (originalState == TestState.NotModified && pendingState == newState) {
      remove(testFindResult);
      return;
    }

    // Included or excluded twice.
    if (originalState == newState) {
      if (pendingState == TestState.RolledBack || originalState != pendingState) {
        remove(testFindResult);
      }
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

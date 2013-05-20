package com.karateca.ddescriber.dialog;

import com.intellij.find.impl.FindResultImpl;
import com.intellij.mock.MockDocument;
import com.karateca.ddescriber.model.TestFindResult;
import com.karateca.ddescriber.model.TestState;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author Andres Dominguez.
 */
public class PendingChangesTest extends TestCase {

  private PendingChanges pendingChanges;

  public void setUp() throws Exception {
    pendingChanges = new PendingChanges();
  }

  public void testShouldSortTestsToChange() throws Exception {
    pendingChanges.itemChanged(createPendingChange(10), TestState.Excluded);
    pendingChanges.itemChanged(createPendingChange(30), TestState.Excluded);

    pendingChanges.itemChanged(createPendingChange(20), TestState.Excluded);
    pendingChanges.itemChanged(createPendingChange(40), TestState.Excluded);

    List<TestFindResult> testsToChange = pendingChanges.getTestsToChange();

    assertEquals(4, testsToChange.size());
    assertEquals(10, testsToChange.get(0).getStartOffset());
    assertEquals(20, testsToChange.get(1).getStartOffset());
    assertEquals(30, testsToChange.get(2).getStartOffset());
    assertEquals(40, testsToChange.get(3).getStartOffset());
  }

  public void testShouldAddItems() {
    // When you add two items.
    pendingChanges.itemChanged(getTestFindResult(TestState.Included), TestState.Excluded);
    pendingChanges.itemChanged(getTestFindResult(TestState.Excluded), TestState.Included);

    // Then ensure there are two items.
    assertEquals(2, pendingChanges.getTestsToChange().size());
  }

  public void testShouldRemoveTestThatDidNotChangeStatus() {
    // Given that you add three items.
    TestFindResult included = getTestFindResult(TestState.Included);
    TestFindResult excluded = getTestFindResult(TestState.Excluded);
    TestFindResult notModified = getTestFindResult(TestState.NotModified);

    pendingChanges.itemChanged(included, TestState.Excluded);
    pendingChanges.itemChanged(excluded, TestState.Included);
    pendingChanges.itemChanged(notModified, TestState.Included);

    // When you include the included and the not modified.
    pendingChanges.itemChanged(included, TestState.Included);
    pendingChanges.itemChanged(notModified, TestState.Included);

    // Then ensure there is on item.
    assertEquals(1, pendingChanges.getTestsToChange().size());
    assertEquals(excluded, pendingChanges.getTestsToChange().get(0));
  }

  public void testShouldRollbackOriginalState() {
    // Given an excluded test.
    TestFindResult excluded = getTestFindResult(TestState.Excluded);

    // When you exclude it.
    pendingChanges.itemChanged(excluded, TestState.Excluded);

    // Then ensure the pending state is rollback and there is one pending change.
    assertEquals(TestState.RolledBack, excluded.getPendingChangeState());
    assertEquals(1, pendingChanges.getTestsToChange().size());

    // When you exclude the test again.
    pendingChanges.itemChanged(excluded, TestState.Excluded);

    // Then ensure the pending state is null and there are no pending changes.
    assertEquals(null, excluded.getPendingChangeState());
    assertEquals(0, pendingChanges.getTestsToChange().size());
  }

  public void testChangingNotModifiedTestShouldRevertToOriginal() {
    TestFindResult notModified = getTestFindResult(TestState.NotModified);

    pendingChanges.itemChanged(notModified, TestState.Included);
    pendingChanges.itemChanged(notModified, TestState.Included);

    assertEquals(null, notModified.getPendingChangeState());
    assertEquals(0, pendingChanges.getTestsToChange().size());
  }

  private TestFindResult getTestFindResult(TestState testState) {
    TestFindResult pendingChange = createPendingChange(1);
    pendingChange.setTestState(testState);
    return pendingChange;
  }

  private TestFindResult createPendingChange(final int startOffset) {
    FindResultImpl findResult = new FindResultImpl(startOffset, startOffset + 10);
    TestFindResult testFindResult = new TestFindResult(new MockDocument(""), findResult) {
      @Override
      public int getStartOffset() {
        return startOffset;
      }
    };
    testFindResult.setTestState(TestState.NotModified);
    return testFindResult;
  }
}

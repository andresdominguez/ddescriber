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

  public void testGetTestsToChange() throws Exception {
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
    pendingChanges.itemChanged(createIncluded(), TestState.Excluded);
    pendingChanges.itemChanged(createExcluded(), TestState.Included);

    // Then ensure there are two items.
    assertEquals(2, pendingChanges.getTestsToChange().size());
  }

  public void testShouldRemoveTestResultThatDidNotChangeStatus() {
    // Given that you add two items.
    TestFindResult included = createIncluded();
    TestFindResult excluded = createExcluded();
    TestFindResult notModified = createExcluded();
    notModified.setTestState(TestState.NotModified);

    pendingChanges.itemChanged(included, TestState.Excluded);
    pendingChanges.itemChanged(excluded, TestState.Included);
    pendingChanges.itemChanged(notModified, TestState.Included);

    // When you remove the change of the included.
    pendingChanges.itemChanged(included, TestState.Included);
    pendingChanges.itemChanged(notModified, TestState.Included);

    // Then ensure there is on item.
    assertEquals(1, pendingChanges.getTestsToChange().size());
    assertEquals(excluded, pendingChanges.getTestsToChange().get(0));
  }

  public void testShouldSetRollbackStateWhenRemovingFromPendingChanges() {
    // Given that you have an included and an excluded tests.
    TestFindResult included = createIncluded();
    TestFindResult excluded = createExcluded();

    // When you remove the changed state.
    included.setPendingChangeState(TestState.Included);
    excluded.setPendingChangeState(TestState.Excluded);

    pendingChanges.itemChanged(included, TestState.Included);
    pendingChanges.itemChanged(excluded, TestState.Excluded);

    // Then ensure the new state is rolled back.
    assertEquals(TestState.RolledBack, included.getPendingChangeState());
    assertEquals(TestState.RolledBack, excluded.getPendingChangeState());
  }

  private TestFindResult createIncluded() {
    TestFindResult pendingChange = createPendingChange(1);
    pendingChange.setTestState(TestState.Included);
    return pendingChange;
  }

  private TestFindResult createExcluded() {
    TestFindResult pendingChange = createPendingChange(1);
    pendingChange.setTestState(TestState.Excluded);
    return pendingChange;
  }

  private TestFindResult createPendingChange(int startOffset) {
    FindResultImpl findResult = new FindResultImpl(startOffset, startOffset + 10);
    TestFindResult testFindResult = new TestFindResult(new MockDocument(""), findResult);
    testFindResult.setTestState(TestState.NotModified);
    return testFindResult;
  }
}

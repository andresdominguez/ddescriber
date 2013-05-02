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
    pendingChanges.itemChanged(createPendingChange(10));
    pendingChanges.itemChanged(createPendingChange(30));

    pendingChanges.itemChanged(createPendingChange(20));
    pendingChanges.itemChanged(createPendingChange(40));

    List<TestFindResult> testsToChange = pendingChanges.getTestsToChange();

    assertEquals(4, testsToChange.size());
    assertEquals(10, testsToChange.get(0).getStartOffset());
    assertEquals(20, testsToChange.get(1).getStartOffset());
    assertEquals(30, testsToChange.get(2).getStartOffset());
    assertEquals(40, testsToChange.get(3).getStartOffset());
  }

  public void testShouldAddItems() {
    // When you add two items.
    pendingChanges.itemChanged(createIncluded());
    pendingChanges.itemChanged(createExcluded());

    // Then ensure there are two items.
    assertEquals(2, pendingChanges.getTestsToChange().size());
  }

  public void testShouldRemoveTestResultThatDidNotChangeStatus() {
    // Given that you add two items.
    TestFindResult included = createIncluded();
    TestFindResult excluded = createExcluded();

    pendingChanges.itemChanged(included);
    pendingChanges.itemChanged(excluded);

    // When you remove the change of the included.
    included.setPendingChangeState(TestState.Included);
    pendingChanges.itemChanged(included);

    // Then ensure there is on item.
    assertEquals(1, pendingChanges.getTestsToChange().size());
    assertEquals(excluded, pendingChanges.getTestsToChange().get(0));
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
    return new TestFindResult(new MockDocument(""), new FindResultImpl(startOffset, startOffset + 10));
  }
}
